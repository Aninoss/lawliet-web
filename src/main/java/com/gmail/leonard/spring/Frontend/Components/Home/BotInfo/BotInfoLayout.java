package com.gmail.leonard.spring.Frontend.Components.Home.BotInfo;

import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Styles;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class BotInfoLayout extends Div {

    public BotInfoLayout(UIData uiData) {
        getStyle().set("background", "var(--lumo-secondary)");
        setWidthFull();

        FlexLayout parts = new FlexLayout();
        parts.addClassNames(Styles.APP_WIDTH, Styles.FLEX_NOTPC_SWITCH_COLUMN_REVERSE);

        if (!uiData.isLite()) parts.add(new BotInfoVideoLayout());
        parts.add(new BotInfoDetailsLayout(uiData));

        add(parts);
    }

}
