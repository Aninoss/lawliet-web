package com.gmail.leonard.spring.Frontend.Components.Home.BotInfo;

import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.HeaderDummy;
import com.gmail.leonard.spring.Frontend.Styles;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.polymertemplate.Id;

public class BotInfoLayout extends Div {

    public BotInfoLayout() {
        setId("bot-info");
        addClassName("tablet-switch");
        setWidthFull();

        FlexLayout parts = new FlexLayout();
        parts.addClassNames(Styles.APP_WIDTH, Styles.FLEX_NOTPC_SWITCH_COLUMN_REVERSE);

        parts.add(new BotInfoVideoLayout());
        parts.add(new BotInfoDetailsLayout());

        add(new HeaderDummy(), parts);
    }

}
