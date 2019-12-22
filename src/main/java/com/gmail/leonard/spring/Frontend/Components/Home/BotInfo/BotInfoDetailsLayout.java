package com.gmail.leonard.spring.Frontend.Components.Home.BotInfo;

import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.ExternalLinks;
import com.gmail.leonard.spring.Frontend.Components.CustomButton;
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
        if (!uiData.isLite()) {
            addClassName("flex-small-row");
            addClassName("size-small-fullwidth");
        } else {
            getStyle().set("flex-direction", "row");
            setMaxWidth("100%");
        }
        setPadding(true);
        setAlignItems(Alignment.CENTER);

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
        Span title = new Span(getTranslation("bot.name"));
        title.getStyle()
                .set("font-size", "150%")
                .set("font-weight", "bold")
                .set("margin-top", "-12px");
        title.addClassName("bot-info-details-width");

        //Description
        Span description = new Span(getTranslation("bot.desc"));
        description.getStyle().set("font-size", "80%");
        if (!uiData.isLite()) {
            description.addClassName("bot-info-details-width");
            description.addClassName("size-small-fullwidth");
        } else {
            description.setMaxWidth("100%");
        }

        //Button
        Button inviteButton = new CustomButton(getTranslation("bot.invite"), VaadinIcon.ARROW_RIGHT.create());
        inviteButton.setWidthFull();
        inviteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        inviteButton.setIconAfterText(true);

        Anchor a = new Anchor(ExternalLinks.BOT_INVITE_URL, inviteButton);
        a.setTarget("_blank");
        a.setWidthFull();
        a.addClassName("bot-info-details-width");

        //Title & Description
        VerticalLayout texts = new VerticalLayout();
        texts.setSpacing(false);
        texts.setPadding(false);
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
