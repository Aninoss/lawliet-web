package xyz.lawlietbot.spring.frontend.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinService;
import jakarta.servlet.http.Cookie;

public class CookieConsent extends VerticalLayout {

    private static final String COOKIE_NAME = "cookie-consent";
    private static final String COOKIE_VALUE = "true";

    public CookieConsent() {
        setId("cookie-consent");
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setPadding(false);
        buttonLayout.getStyle().set("margin-top", "0");

        if (cookieMessageClosed()) {
            setVisible(false);
            return;
        }

        Paragraph cookieText = new Paragraph(getTranslation("cookie.text"));
        Anchor moreInfo = new Anchor("https://www.cookiesandyou.com/", new Paragraph(getTranslation("cookie.learnmore")));
        moreInfo.setTarget("_blank");

        Button gotItButtom = new Button(getTranslation("cookie.button"));
        gotItButtom.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        gotItButtom.getElement().setAttribute("onclick", String.format("closeCookieConsent(\"%s\", \"%s\");", COOKIE_NAME, COOKIE_VALUE));

        buttonLayout.add(gotItButtom, moreInfo);
        add(cookieText, buttonLayout);
    }

    private boolean cookieMessageClosed() {
        if (VaadinService.getCurrentRequest().getCookies() == null) return false;
        for(Cookie cookie: VaadinService.getCurrentRequest().getCookies()) {
            if (cookie.getName().equals(COOKIE_NAME) && cookie.getValue().equals(COOKIE_VALUE)) return true;
        }
        return false;
    }
}
