package xyz.lawlietbot.spring;

import com.stripe.Stripe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import xyz.lawlietbot.spring.backend.payment.stripe.StripeCache;
import xyz.lawlietbot.spring.syncserver.SyncManager;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    private final static Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        Stripe.apiKey = System.getenv("STRIPE_API_KEY");
        SpringApplication.run(Application.class, args);
        LOGGER.info("###########################");
        Console.getInstance().start();
        SyncManager.getInstance().start();
        StripeCache.startScheduler();
    }

}