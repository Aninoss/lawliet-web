package com.gmail.leonard.spring.frontend.components.home.botinfo;

import com.gmail.leonard.spring.frontend.ComponentChanger;
import com.gmail.leonard.spring.frontend.components.Video;
import com.gmail.leonard.spring.frontend.Styles;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;

public class BotInfoVideoLayout extends Div {
    public BotInfoVideoLayout() {
        setId("bot-info-video");
        addClassName(Styles.VISIBLE_NOTMOBILE);
        setWidthFull();

        //Video
        String videoURL = VaadinServletService.getCurrent()
                .resolveResource("/styles/video/bot_demo.webm",
                        VaadinSession.getCurrent().getBrowser());

        String posterURL = VaadinServletService.getCurrent()
                .resolveResource("/styles/img/bot_demo_poster.jpg",
                        VaadinSession.getCurrent().getBrowser());

        add(new Video(videoURL, posterURL, "bot-video"));

        //Lower right chat bar
        String discordBarIconStr = VaadinServletService.getCurrent()
                .resolveResource("/styles/img/discordbar_right.jpg",
                        VaadinSession.getCurrent().getBrowser());

        Image discordBarIcon = new Image(discordBarIconStr, "");
        ComponentChanger.setNotInteractive(discordBarIcon);
        discordBarIcon.getStyle()
                .set("position", "absolute")
                .set("right", "0px")
                .set("bottom", "0px");

        add(discordBarIcon);

        //Gradient foreground large
        Div fade = new Div();
        ComponentChanger.setNotInteractive(fade);
        fade.addClassName("videofade");
        fade.addClassName(Styles.VISIBLE_NOTMOBILE);
        add(fade);
    }
}
