package xyz.lawlietbot.spring.frontend.components;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;

public class LawlietBotLogo extends HorizontalLayout {

    public LawlietBotLogo() {
        setPadding(false);
        setSpacing(false);
        setAlignItems(Alignment.CENTER);
        setHeight("48px");

        addLogoImage();
        addLawlietText();
        addBotText();
    }

    private void addLogoImage() {
        String logoString = VaadinServletService.getCurrent()
                .resolveResource("/styles/img/bot_icon_small.webp",
                        VaadinSession.getCurrent().getBrowser());

        Image logoImage = new Image(logoString, "");
        logoImage.setHeight("48px");
        logoImage.getStyle().set("border-radius", "50%");
        add(logoImage);
    }

    private void addLawlietText() {
        H2 lawlietText = new H2("Lawliet");
        lawlietText.getStyle()
                .set("font-family","Oswald")
                .set("color", "white")
                .set("margin", "0 4px 0 12px");
        add(lawlietText);
    }

    private void addBotText() {
        H2 lawlietText = new H2("BOT");
        lawlietText.getStyle()
                .set("font-family","Oswald")
                .set("color", "var(--lumo-primary-text-color)")
                .set("margin", "0");
        add(lawlietText);
    }

}
