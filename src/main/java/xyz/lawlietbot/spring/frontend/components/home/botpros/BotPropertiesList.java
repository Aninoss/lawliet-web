package xyz.lawlietbot.spring.frontend.components.home.botpros;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;

public class BotPropertiesList extends VerticalLayout {

    public BotPropertiesList(UIData uiData) {
        setPadding(false);
        getStyle().set("background", "var(--lumo-secondary)");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.addClassName(Styles.APP_WIDTH);
        mainLayout.setId("prop-list");

        for (int i = 0; i < 4; i++) {
            String text = getTranslation("bot.adv." + i);
            mainLayout.add(generateProperty(text));
        }

        mainLayout.add(generateInviteButton(uiData));
        add(mainLayout);
    }

    private Component generateProperty(String text) {
        FlexLayout content = new FlexLayout();
        content.setFlexDirection(FlexLayout.FlexDirection.ROW);
        Icon icon = VaadinIcon.CHECK.create();
        icon.addClassName("prop-check");

        content.add(icon, new Text(text));
        return content;
    }

    private Component generateInviteButton(UIData uiData) {
        Button inviteButton = new Button(getTranslation("bot.invite"), VaadinIcon.ARROW_RIGHT.create());
        inviteButton.setWidthFull();
        inviteButton.setHeight("48px");
        inviteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        inviteButton.setIconAfterText(true);

        Anchor a = new Anchor(uiData.getBotInviteUrl(), inviteButton);
        a.setTarget("_blank");
        a.setId("prop-button");

        return a;
    }

}
