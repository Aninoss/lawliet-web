package xyz.lawlietbot.spring;

import com.stripe.Stripe;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Map;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
@Theme(themeClass = Lumo.class, variant = Lumo.DARK)
@BodySize(width = "100%", height = "100%")
public class Application implements AppShellConfigurator {

    private final static Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        Stripe.apiKey = System.getenv("STRIPE_API_KEY");
        SpringApplication.run(Application.class, args);
        LOGGER.info("###########################");
        Console.getInstance().start();
    }

    @Override
    public void configurePage(AppShellSettings settings) {
        TranslationProvider translationProvider = new TranslationProvider();

        String target = settings.getRequest().getPathInfo().substring(1);
        if (target.contains("/")) {
            target = Arrays.stream(target.split("/"))
                    .map(subTarget -> subTarget.replaceAll("[^a-zA-Z].*", ""))
                    .filter(subTarget -> translationProvider.keyExists("category." + subTarget, settings.getRequest().getLocale()))
                    .findFirst()
                    .orElse(null);
        }

        String pageTitle;
        if (target != null && target.isEmpty()) {
            pageTitle = new TranslationProvider().getTranslation("bot.title", settings.getRequest().getLocale());
        } else if (translationProvider.keyExists("category." + target, settings.getRequest().getLocale())) {
            pageTitle = new TranslationProvider().getTranslation("category." + target, settings.getRequest().getLocale());
        } else {
            pageTitle = new TranslationProvider().getTranslation("category.notfound", settings.getRequest().getLocale());
        }

        settings.addMetaTag("og:type", "website");
        settings.addMetaTag("og:site_name", new TranslationProvider().getTranslation("bot.name", settings.getRequest().getLocale()));
        settings.addMetaTag("og:title", new TranslationProvider().getTranslation("pagetitle", settings.getRequest().getLocale(), pageTitle));
        settings.addMetaTag("og:description", new TranslationProvider().getTranslation("bot.desc.nonsfw", settings.getRequest().getLocale()));
        settings.addMetaTag("og:image", "http://lawlietbot.xyz/styles/img/bot_icon.webp");

        //Favicons
        settings.addLink("/apple-touch-icon.png", Map.of(
                "rel", "apple-touch-icon",
                "sizes", "180x180"
        ));
        settings.addLink("manifest", "/site.webmanifest");
        settings.addMetaTag("theme-color", "#ffffff");
    }

    @Bean
    public TomcatContextCustomizer sessionCookieConfigForCors() {
        return context -> {
            final Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor() {
                @Override
                public String generateHeader(Cookie cookie, HttpServletRequest request) {
                    // Needs to be secure
                    if (cookie.getName().startsWith("JSESSIONID")) {
                        cookie.setHttpOnly(true);
                        cookie.setSecure(true);
                        cookie.setAttribute("SameSite", SameSiteCookies.NONE.getValue());
                        cookie.setAttribute("Partitioned", "true");
                    }
                    return super.generateHeader(cookie, request);
                }
            };
            context.setCookieProcessor(cookieProcessor);
        };
    }


}