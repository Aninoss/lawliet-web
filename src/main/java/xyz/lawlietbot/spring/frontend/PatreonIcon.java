package xyz.lawlietbot.spring.frontend;

import com.vaadin.flow.component.html.Image;

public class PatreonIcon extends Image {

    public PatreonIcon() {
        super("styles/img/patreon.svg", "Patreon Icon");
        getStyle()
                .set("height","24px")
                .set("width","auto")
                .set("margin-top","3px")
                .set("margin-right","8px");
    }
}
