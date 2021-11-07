package xyz.lawlietbot.spring.backend.payment;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import com.stripe.param.CustomerListParams;
import com.stripe.param.SubscriptionListParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StripeCache {

    private final static Logger LOGGER = LoggerFactory.getLogger(StripeCache.class);
    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private static List<Subscription> subscriptions = Collections.emptyList();
    private static List<Customer> customers = Collections.emptyList();;

    public static void startScheduler() {
        executor.scheduleAtFixedRate(() -> {
            try {
                reload();
            } catch (Throwable e) {
                LOGGER.error("Stripe error", e);
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    public static void reload() throws StripeException {
        subscriptions = Subscription.list(SubscriptionListParams.builder()
                .setStatus(SubscriptionListParams.Status.ACTIVE)
                .setLimit(9999L)
                .build()
        ).getData();

        customers = Customer.list(CustomerListParams.builder()
                .setLimit(9999L)
                .build()
        ).getData();

        LOGGER.info("Stripe load successful");
    }

    public static List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public static List<Customer> getCustomers() {
        return customers;
    }

}
