package com.gmail.leonard.spring.Frontend.Components.Home.BotInfo;

import com.gmail.leonard.spring.Frontend.ComponentChanger;
import com.gmail.leonard.spring.Frontend.Components.Video;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;

public class BotInfoVideoLayout extends HorizontalLayout {
    public BotInfoVideoLayout() {
        setId("bot-info-video");
        addClassName("visible-not-xsmall");
        setWidthFull();
        setSpacing(false);

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
        String fadeStr = VaadinServletService.getCurrent()
                .resolveResource("/styles/img/video_fade.png",
                        VaadinSession.getCurrent().getBrowser());

        Image fade = new Image(fadeStr, "");
        ComponentChanger.setNotInteractive(fade);
        fade.addClassName("videofade");
        fade.addClassName("visible-large");
        add(fade);

        //Gradient foreground small
        String fadeStr2 = VaadinServletService.getCurrent()
                .resolveResource("/styles/img/video_fade2.png",
                        VaadinSession.getCurrent().getBrowser());

        Image fade2 = new Image(fadeStr2, "");
        ComponentChanger.setNotInteractive(fade2);
        fade2.addClassName("videofade");
        fade2.addClassName("visible-small");
        add(fade2);
    }
}
