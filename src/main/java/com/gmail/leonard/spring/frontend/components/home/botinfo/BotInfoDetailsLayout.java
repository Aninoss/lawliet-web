package com.gmail.leonard.spring.frontend.components.home.botinfo;

import com.gmail.leonard.spring.backend.userdata.UIData;
import com.gmail.leonard.spring.frontend.Styles;
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
        addClassNames(Styles.FLEX_NOTPC_SWITCH_ROW, "size-small-fullwidth");
        setPadding(true);

        //Bot Icon
        String iconStr = VaadinServletService.getCurrent()
                .resolveResource("/styles/img/bot_icon.png",
                        VaadinSession.getCurrent().getBrowser());

        Image icon = new Image(iconStr, "");
        icon.setId("bot-info-details-icon");

        //Title
        H1 title = new H1(getTranslation("bot.name"));
        title.getStyle()
                .set("font-size", "150%")
                .set("font-weight", "bold")
                .set("margin-top", "-8px")
                .set("margin-bottom", "5px");
        title.addClassName("bot-info-details-width");
        title.setWidthFull();

        //Description
        Div description = new Div(new Text(getTranslation(uiData.isNSFWDisabled() ? "bot.desc.nonsfw" : "bot.desc")));
        description.getStyle().set("font-size", "80%");
        description.addClassNames("bot-info-details-width", "size-small-fullwidth");

        //Button
        Button inviteButton = new Button(getTranslation("bot.invite"), VaadinIcon.ARROW_RIGHT.create());
        inviteButton.setWidthFull();
        inviteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        inviteButton.setIconAfterText(true);

        Anchor a = new Anchor(uiData.getBotInviteUrl(), inviteButton);
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
