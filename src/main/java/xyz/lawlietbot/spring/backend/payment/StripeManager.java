package xyz.lawlietbot.spring.backend.payment;

import java.util.*;
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
import xyz.lawlietbot.spring.backend.UICache;
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

    public static String generateCheckoutSession(SubDuration duration, SubLevel level, long discordId, String discordTag, int quantity) throws StripeException {
        String returnUrl = ExternalLinks.LAWLIET_PREMIUM;
        SessionCreateParams.Builder paramsBuilder = new SessionCreateParams.Builder()
                .setSuccessUrl(returnUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(returnUrl)
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .putMetadata("discord_id", String.valueOf(discordId))
                .putMetadata("unlock_servers", String.valueOf(level.getSubLevelType() == SubLevelType.PRO))
                .putMetadata("quantity", String.valueOf(quantity))
                .putMetadata("tier", level.getSubLevelType().name() + " " + duration.name())
                .putMetadata("discord_tag", discordTag)
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

    public static String generateCustomerPortalSession(long discordId) throws StripeException {
        Optional<Customer> customerOpt = StripeManager.retrieveCustomer(discordId);
        if (customerOpt.isPresent()) {
            Map<String, Object> params = new HashMap<>();
            params.put("customer", customerOpt.get().getId());

            com.stripe.model.billingportal.Session session = com.stripe.model.billingportal.Session.create(params);
            return session.getUrl();
        } else {
            return null;
        }
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
        long discordId = Long.parseLong(metadata.get("discord_id"));
        UI ui = UICache.get(discordId);
        if (ui != null) {
            SendEvent.sendStripe(
                    discordId,
                    ui.getTranslation("premium.usermessage.title"),
                    ui.getTranslation("premium.usermessage.desc", ExternalLinks.LAWLIET_PREMIUM, ExternalLinks.BETA_SERVER_INVITE)
            );
        }
    }

    public static String getPriceId(SubDuration duration, SubLevel level) {
        if (duration == SubDuration.MONTHLY) {
            switch (level) {
                case BASIC_USD:
                    return "price_1Jpwd7AXVG0I7dQKmwG96kMu";

                case BASIC_EUR:
                    return "price_1Jpwd7AXVG0I7dQK5ynjRkk5";

                case BASIC_GBP:
                    return "price_1Jpwd7AXVG0I7dQKfhQJedUf";

                case PRO_USD:
                    return "price_1JpwdFAXVG0I7dQKXJMsmNud";

                case PRO_EUR:
                    return "price_1JpwdFAXVG0I7dQKyU3a6BNu";

                case PRO_GBP:
                    return "price_1JpwdEAXVG0I7dQK19ORzM8E";

                default:
                    return null;
            }
        } else {
            switch (level) {
                case BASIC_USD:
                    return "price_1Jpwd7AXVG0I7dQKrwBUQn2M";

                case BASIC_EUR:
                    return "price_1Jpwd7AXVG0I7dQKMLeVC8uZ";

                case BASIC_GBP:
                    return "price_1Jpwd7AXVG0I7dQKmnRfmc51";

                case PRO_USD:
                    return "price_1JpwdFAXVG0I7dQK26d9VRiA";

                case PRO_EUR:
                    return "price_1JpwdEAXVG0I7dQK4VBLgW7l";

                case PRO_GBP:
                    return "price_1JpwdEAXVG0I7dQK70LbHisi";

                default:
                    return null;
            }
        }
    }

}
