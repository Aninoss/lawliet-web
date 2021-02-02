package xyz.lawlietbot.spring.frontend.components;

import com.vaadin.flow.component.html.Image;

public class DiscordIcon extends Image {

    public DiscordIcon() {
        super("styles/img/discord.png", "Discord");
        getStyle()
                .set("height","36px")
                .set("width","auto")
                .set("margin-top","8px")
                .set("margin-bottom","5px")
                .set("margin-right","4px");
    }
}
