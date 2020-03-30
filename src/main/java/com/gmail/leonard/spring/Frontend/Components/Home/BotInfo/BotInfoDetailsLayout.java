package com.gmail.leonard.spring.Frontend.Components.Home.BotInfo;

import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.ExternalLinks;
import com.gmail.leonard.spring.Frontend.Styles;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;

public class BotInfoDetailsLayout extends VerticalLayout {
    public BotInfoDetailsLayout(UIData uiData) {
        setId("bot-info-details");
        if (uiData.isLite()) addClassName("lite-class");

        if (!uiData.isLite()) {
            addClassName(Styles.FLEX_NOTPC_SWITCH_ROW);
            addClassName("size-small-fullwidth");
        } else {
            getStyle().set("flex-direction", "row");
            setMaxWidth("100%");
        }
        setPadding(true);

        //Bot Icon
        String iconStr = VaadinServletService.getCurrent()
                .resolveResource("/styles/img/bot_icon.png",
                        VaadinSession.getCurrent().getBrowser());

        Image icon = new Image(iconStr, "");
        if (uiData.isLite()) {
            icon.setMaxWidth("125px");
            icon.setMaxHeight("125px");
            icon.getStyle().set("margin-right", "16px");
        }
        icon.setId("bot-info-details-icon");

        //Title
        Div title = new Div(new Text(getTranslation("bot.name")));
        title.getStyle()
                .set("font-size", "150%")
                .set("font-weight", "bold")
                .set("margin-top", "-12px");
        title.addClassName("bot-info-details-width");
        title.setWidthFull();

        //Description
        Div description = new Div(new Text(getTranslation("bot.desc")));
        description.getStyle().set("font-size", "80%");
        if (!uiData.isLite()) {
            description.addClassName("bot-info-details-width");
            description.addClassName("size-small-fullwidth");
        } else {
            description.setMaxWidth("100%");
        }

        //Button
        Button inviteButton = new Button(getTranslation("bot.invite"), VaadinIcon.ARROW_RIGHT.create());
        inviteButton.setWidthFull();
        inviteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        inviteButton.setIconAfterText(true);

        Anchor a = new Anchor(ExternalLinks.BOT_INVITE_URL, inviteButton);
        a.setTarget("_blank");
        a.setMaxWidth("var(--bot-info-width)");
        a.setWidth("100%");

        //Title & Description
        Div texts = new Div();
        texts.add(title, description);

        //Title, Description & Button
        VerticalLayout notIconElements = new VerticalLayout();
        notIconElements.setPadding(false);
        notIconElements.add(texts, a);

        Div div = new Div();
        div.add(icon);

        add(div, notIconElements);
    }
}
