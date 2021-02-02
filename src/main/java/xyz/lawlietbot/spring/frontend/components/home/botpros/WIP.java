package xyz.lawlietbot.spring.frontend.components.home.botpros;

import com.vaadin.flow.component.html.Label;

public class WIP extends Label {

    public WIP(String text) {
        super(text);
        getStyle()
                .set("color", "var(--lumo-error-color)")
                .set("transform", "rotate(15deg)")
                .set("font-size", "150%")
                .set("margin-bottom", "50px")
                .set("margin-top", "50px");
    }
}
