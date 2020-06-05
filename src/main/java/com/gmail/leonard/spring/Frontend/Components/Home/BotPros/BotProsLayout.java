package com.gmail.leonard.spring.Frontend.Components.Home.BotPros;

import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Styles;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class BotProsLayout extends VerticalLayout {

    public BotProsLayout(UIData uiData) {
        setPadding(true);
        addClassName(Styles.APP_WIDTH);

        H2 title = new H2(getTranslation("bot.card.title"));
        title.getStyle().set("margin-top", "2em");
        setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, title);

        add(title);
        add(new BotProsPanelsLayout(uiData));

        Div andMore = new Div(new Text(getTranslation("bot.note")));
        andMore.setWidthFull();
        add(andMore);
    }
}
