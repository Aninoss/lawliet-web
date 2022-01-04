package xyz.lawlietbot.spring.backend.payment.stripe;

import java.util.*;
import java.util.stream.Collectors;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.vaadin.flow.component.UI;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.lawlietbot.spring.ExternalLinks;
import xyz.lawlietbot.spring.backend.UICache;
import xyz.lawlietbot.spring.backend.payment.*;
import xyz.lawlietbot.spring.syncserver.SendEvent;

public class StripeManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(StripeManager.class);
    private static final HashSet<String> usedSubscriptions = new HashSet<>();

    public static List<Subscription> retrieveActiveSubscriptions(long userId) throws StripeException {
        return StripeCache.getSubscriptions()
                .stream()
                .filter(sub -> sub.getMetadata().containsKey("discord_id") && sub.getMetadata().get("discord_id").equals(String.valueOf(userId)))
                .collect(Collectors.toList());
    }

    public static Optional<Customer> retrieveCustomer(long userId) {
        return retrieveCustomer(userId, null);
    }

    public static Optional<Customer> retrieveCustomer(long userId, SubCurrency currency) {
        return StripeCache.getCustomers()
                .stream()
                .filter(customer -> customer.getMetadata().containsKey("discord_id") &&
                        customer.getMetadata().get("discord_id").equals(String.valueOf(userId)) &&
                        (currency == null || customer.getCurrency().equalsIgnoreCase(currency.name()))
                )
                .findFirst();
    }

    public static String generateCheckoutSession(SubDuration duration, SubLevel level, long discordId, String discordTag, String discordAvatar, int quantity) throws StripeException {
        String returnUrl = ExternalLinks.LAWLIET_PREMIUM;
        SessionCreateParams.Builder paramsBuilder = new SessionCreateParams.Builder()
                .setSuccessUrl(returnUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(returnUrl)
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .putMetadata("discord_id", String.valueOf(discordId))
                .putMetadata("unlock_servers", String.valueOf(level.getSubLevelType() == SubLevelType.PRO))
                .putMetadata("quantity", String.valueOf(quantity))
                .putMetadata("tier", WordUtils.capitalizeFully(level.getSubLevelType().name() + " " + duration.name()))
                .putMetadata("discord_tag", discordTag)
                .putMetadata("discord_avatar", discordAvatar)
                .setAutomaticTax(SessionCreateParams.AutomaticTax.builder().setEnabled(true).build())
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
                StripeCache.reload();
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
                    ui.getTranslation("premium.usermessage.desc", ExternalLinks.LAWLIET_PREMIUM, ExternalLinks.BETA_SERVER_INVITE),
                    0,
                    false
            );
        }
        try {
            WebhookNotifier.newSub(
                    metadata.get("discord_tag"),
                    discordId,
                    metadata.get("discord_avatar"),
                    metadata.get("tier"),
                    Integer.parseInt(metadata.get("quantity")),
                    session.getCurrency().toUpperCase(),
                    session.getAmountTotal() / 100.0,
                    0
            );
        } catch (Throwable e) {
            LOGGER.error("Error in new sub webhook", e);
        }
    }

    public static String getPriceId(SubDuration duration, SubLevel level) {
        if (duration == SubDuration.MONTHLY) {
            switch (level) {
                case BASIC_USD:
                    return "price_1JwSZ4AXVG0I7dQKW6lVBISh";

                case BASIC_EUR:
                    return "price_1JwSZ4AXVG0I7dQKrVfXIvFj";

                case BASIC_GBP:
                    return "price_1JwSZ4AXVG0I7dQKska73IYA";

                case PRO_USD:
                    return "price_1JwScJAXVG0I7dQKpRvKueM9";

                case PRO_EUR:
                    return "price_1JwScJAXVG0I7dQKQVIKFHAY";

                case PRO_GBP:
                    return "price_1JwScJAXVG0I7dQKQHBoWu9r";

                default:
                    return null;
            }
        } else {
            switch (level) {
                case BASIC_USD:
                    return "price_1JwSZ4AXVG0I7dQKEPWBiV4e";

                case BASIC_EUR:
                    return "price_1JwSZ4AXVG0I7dQKrNvMDgCT";

                case BASIC_GBP:
                    return "price_1JwSZ4AXVG0I7dQKYS4FaRB1";

                case PRO_USD:
                    return "price_1JwScJAXVG0I7dQKqKFwrsEu";

                case PRO_EUR:
                    return "price_1JwScJAXVG0I7dQKs4SOBgoC";

                case PRO_GBP:
                    return "price_1JwScJAXVG0I7dQK7Nk6j3g5";

                default:
                    return null;
            }
        }
    }

}
