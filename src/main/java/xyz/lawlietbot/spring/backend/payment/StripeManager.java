package xyz.lawlietbot.spring.backend.payment;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerListParams;
import com.stripe.param.SubscriptionListParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.vaadin.flow.component.UI;
import xyz.lawlietbot.spring.ExternalLinks;
import xyz.lawlietbot.spring.syncserver.SendEvent;

public class StripeManager {

    private static final HashSet<String> usedSubscriptions = new HashSet<>();

    public static List<Subscription> retrieveActiveSubscriptions(long userId) throws StripeException {
        return Subscription.list(SubscriptionListParams.builder()
                        .setStatus(SubscriptionListParams.Status.ACTIVE)
                        .build()
                )
                .getData()
                .stream()
                .filter(sub -> sub.getMetadata().containsKey("discord_id") && sub.getMetadata().get("discord_id").equals(String.valueOf(userId)))
                .collect(Collectors.toList());
    }

    public static Optional<Customer> retrieveCustomer(long userId) throws StripeException {
        return retrieveCustomer(userId, null);
    }

    public static Optional<Customer> retrieveCustomer(long userId, SubCurrency currency) throws StripeException {
        return Customer.list(CustomerListParams.builder()
                .build()
        ).getData().stream()
                .filter(customer -> customer.getMetadata().containsKey("discord_id") &&
                        customer.getMetadata().get("discord_id").equals(String.valueOf(userId)) &&
                        (currency == null || customer.getCurrency().equalsIgnoreCase(currency.name()))
                )
                .findFirst();
    }

    public static String generateSession(SubDuration duration, SubLevel level, String returnUrl, long discordId, int quantity) throws StripeException {
        SessionCreateParams.Builder paramsBuilder = new SessionCreateParams.Builder()
                .setSuccessUrl(returnUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(returnUrl)
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .putMetadata("discord_id", String.valueOf(discordId))
                .putMetadata("unlock_servers", String.valueOf(level.getSubLevelType() == SubLevelType.PRO))
                .setAutomaticTax(SessionCreateParams.AutomaticTax.builder().setEnabled(false).build())
                .setAllowPromotionCodes(duration == SubDuration.MONTHLY)
                .addLineItem(new SessionCreateParams.LineItem.Builder()
                        .setQuantity((long) quantity)
                        .setPrice(StripeManager.getPriceId(duration, level))
                        .build()
                );

        Optional<Customer> customerOpt = StripeManager.retrieveCustomer(discordId, level.getCurrency());
        if (customerOpt.isPresent()) {
            paramsBuilder = paramsBuilder.setCustomer(customerOpt.get().getId());
        } else {
            customerOpt = StripeManager.retrieveCustomer(discordId);
            if (customerOpt.isPresent()) {
                paramsBuilder = paramsBuilder.setCustomerEmail(customerOpt.get().getEmail());
            }
        }

        if (level.getCurrency() == SubCurrency.EUR) {
            paramsBuilder = paramsBuilder.addPaymentMethodType(SessionCreateParams.PaymentMethodType.SEPA_DEBIT);
        }

        Session session = Session.create(paramsBuilder.build());
        return session.getUrl();
    }

    public static synchronized void registerSubscription(Session session) throws StripeException {
        if (!usedSubscriptions.contains(session.getId())) {
            usedSubscriptions.add(session.getId());
            Subscription subscription = Subscription.retrieve(session.getSubscription());
            if (subscription.getMetadata().isEmpty()) {
                processSubscription(session, subscription);
            }
        }
    }

    private static void processSubscription(Session session, Subscription subscription) throws StripeException {
        Map<String, String> metadata = session.getMetadata();
        Customer.retrieve(session.getCustomer()).update(Map.of("metadata", metadata));
        subscription.update(Map.of("metadata", metadata));
        SendEvent.sendStripe(
                Long.parseLong(metadata.get("discord_id")),
                UI.getCurrent().getTranslation("premium.usermessage.title"),
                UI.getCurrent().getTranslation("premium.usermessage.desc", ExternalLinks.LAWLIET_PREMIUM, ExternalLinks.BETA_SERVER_INVITE)
        );
    }

    public static String getPriceId(SubDuration duration, SubLevel level) {
        if (duration == SubDuration.MONTHLY) {
            switch (level) {
                case BASIC_USD:
                    return "price_1JnmJ4AXVG0I7dQKbrLsqgoo";

                case BASIC_EUR:
                    return "price_1Jo8LdAXVG0I7dQKzdLBOlB7";

                case BASIC_GBP:
                    return "price_1JoXFMAXVG0I7dQKebMAcbQr";

                case PRO_USD:
                    return "price_1Jo8i6AXVG0I7dQKYCLy5TEm";

                case PRO_EUR:
                    return "price_1Jo8j8AXVG0I7dQKYHzodoF7";

                case PRO_GBP:
                    return "price_1JoXHTAXVG0I7dQKu1BTc3LK";

                default:
                    return null;
            }
        } else {
            switch (level) {
                case BASIC_USD:
                    return "price_1JnmKUAXVG0I7dQKMDzrczi5";

                case BASIC_EUR:
                    return "price_1Jo8LLAXVG0I7dQKP9SuM54c";

                case BASIC_GBP:
                    return "price_1JoXFnAXVG0I7dQKS7Jg9xBl";

                case PRO_USD:
                    return "price_1Jo8kBAXVG0I7dQK1LOdDXB8";

                case PRO_EUR:
                    return "price_1Jo8oEAXVG0I7dQKkRacDwFk";

                case PRO_GBP:
                    return "price_1JoXHeAXVG0I7dQKK4CXf9X9";

                default:
                    return null;
            }
        }
    }

}
