package xyz.lawlietbot.spring.frontend.components.premium;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import xyz.lawlietbot.spring.ExternalLinks;
import xyz.lawlietbot.spring.frontend.Styles;

public class PremiumConnectSection extends VerticalLayout {

    public PremiumConnectSection() {
        setPadding(true);
        getStyle().set("background", "var(--lumo-secondary)");
        addClassName("section");
        add(generateMainContent());
    }

    public Component generateMainContent() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setPadding(false);
        mainLayout.addClassName(Styles.APP_WIDTH);
        mainLayout.add(generateTitle());
        mainLayout.add(generateDescription());
        mainLayout.add(generateLeaveDisclaimer());
        mainLayout.add(generateConnectedAppsButton());
        return mainLayout;
    }

    private Component generateTitle() {
        String text = getTranslation("premium.step2");
        H2 title = new H2(text);
        title.addClassName("section-title");
        return title;
    }

    private Component generateDescription() {
        Div div = new Div(getTranslation("premium.connect.content"));
        div.getStyle().set("margin-top", "0");
        return div;
    }

    private Component generateLeaveDisclaimer() {
        Div div = new Div(getTranslation("premium.connect.leave"));
        div.addClassName("section-subtext");
        return div;
    }

    private Component generateConnectedAppsButton() {
        Button connectedAppsButton = new Button(getTranslation("premium.connect.button"), VaadinIcon.ARROW_RIGHT.create());
        connectedAppsButton.setHeight("48px");
        connectedAppsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        connectedAppsButton.setIconAfterText(true);

        Anchor a = new Anchor(ExternalLinks.PATREON_CONNECTED_APPS_SETTINGS, connectedAppsButton);
        a.setTarget("_blank");
        return a;
    }

}
