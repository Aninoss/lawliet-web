package com.gmail.leonard.spring.Frontend.Components.Home.BotPros;

import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class BotProsLayout extends VerticalLayout {

    public BotProsLayout(UIData uiData) {
        setPadding(true);
        addClassName("app-width");

        H2 title = new H2(getTranslation("bot.card.title"));
        setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, title);

        add(title);
        add(new BotProsPanelsLayout(uiData));
        add(new Label(getTranslation("bot.note")));
    }
}
