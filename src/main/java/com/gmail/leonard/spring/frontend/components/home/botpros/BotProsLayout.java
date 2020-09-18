package com.gmail.leonard.spring.frontend.components.home.botpros;

import com.gmail.leonard.spring.backend.userdata.UIData;
import com.gmail.leonard.spring.frontend.Styles;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class BotProsLayout extends VerticalLayout {

    public BotProsLayout(UIData uiData) {
        setPadding(true);
        addClassName(Styles.APP_WIDTH);
        getStyle().set("margin-bottom", "48px");

        H2 title = new H2(getTranslation("bot.card.title"));
        title.getStyle().set("margin-top", "2em");
        title.setWidthFull();
        title.addClassName(Styles.CENTER_TEXT);

        add(title);
        add(new BotProsPanelsLayout(uiData));

        Div andMore = new Div(new Text(getTranslation("bot.note")));
        andMore.setWidthFull();
        add(andMore);
    }
}
