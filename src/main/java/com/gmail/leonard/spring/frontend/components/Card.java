package com.gmail.leonard.spring.frontend.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

public class Card extends Div {

    public Card(Component... components) {
        super(components);
        getStyle().set("border-radius", "5px")
                .set("background", "var(--lumo-tint-5pct)");
    }

}
