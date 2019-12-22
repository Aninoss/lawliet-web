package com.gmail.leonard.spring.Frontend.Components.Home.BotInfo;

import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class BotInfoLayout extends Div {

    public BotInfoLayout(UIData uiData) {
        getStyle().set("background", "var(--lumo-secondary)");
        setWidthFull();

        HorizontalLayout parts = new HorizontalLayout();
        parts.addClassName("app-width");
        parts.addClassName("flex-small-column-reverse");
        parts.setSpacing(false);

        if (!uiData.isLite()) parts.add(new BotInfoVideoLayout());
        parts.add(new BotInfoDetailsLayout(uiData));

        add(parts);
    }

}
