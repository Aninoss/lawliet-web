package xyz.lawlietbot.spring.frontend.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

public class Card extends Div {

    public Card(Component... components) {
        super(components);
        getStyle().set("border-radius", "6px")
                .set("background", "var(--lumo-tint-5pct)");
    }

}
