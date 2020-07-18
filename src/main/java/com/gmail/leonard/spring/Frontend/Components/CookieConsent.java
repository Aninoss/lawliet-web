package com.gmail.leonard.spring.Frontend.Components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinService;
import javax.servlet.http.Cookie;

public class CookieConsent extends VerticalLayout {

    private static final String COOKIE_NAME = "cookie-consent", COOKIE_VALUE = "true";

    public CookieConsent() {
        setId("cookie-consent");
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setPadding(false);
        buttonLayout.getStyle().set("margin-top", "0");

        if (cookieMessageClosed()) {
            setVisible(false);
            return;
        }

        getStyle().set("position", "fixed")
                .set("left", "0px")
                .set("bottom", "0px")
                .set("background", "black")
                .set("width", "100vw")
                .set("max-width", "400px")
                .set("z-index", "6")
                .set("transition", "transform 500ms")
                .set("transform", "none")
                .set("padding", "32px");

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
