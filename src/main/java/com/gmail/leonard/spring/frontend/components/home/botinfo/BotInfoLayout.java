package com.gmail.leonard.spring.frontend.components.home.botinfo;

import com.gmail.leonard.spring.backend.userdata.UIData;
import com.gmail.leonard.spring.frontend.components.HeaderDummy;
import com.gmail.leonard.spring.frontend.Styles;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class BotInfoLayout extends Div {

    public BotInfoLayout(UIData uiData) {
        setId("bot-info");
        addClassName("tablet-switch");
        setWidthFull();

        FlexLayout parts = new FlexLayout();
        parts.addClassNames(Styles.APP_WIDTH, Styles.FLEX_NOTPC_SWITCH_COLUMN_REVERSE);

        parts.add(new BotInfoVideoLayout());
        parts.add(new BotInfoDetailsLayout(uiData));

        add(new HeaderDummy(), parts);
    }

}
