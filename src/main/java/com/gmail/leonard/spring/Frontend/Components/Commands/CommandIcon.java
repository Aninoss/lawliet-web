package com.gmail.leonard.spring.Frontend.Components.Commands;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;

import javax.validation.constraints.NotNull;

public class CommandIcon extends Div {

    public enum Type { PERMISSIONS, NSFW, TRACKER };

    public CommandIcon(@NotNull Type type) {
        this(type, false);
    }

    public CommandIcon(@NotNull Type type, boolean showTitle) {
        String size = "22px";

        String fileStr;
        switch (type) {
            case NSFW:
                fileStr = "icon_nsfw.png";
                break;

            case PERMISSIONS:
                fileStr = "icon_locked.png";
                break;

            case TRACKER:
                fileStr = "icon_tracker.png";
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        String IconStr = VaadinServletService.getCurrent()
                .resolveResource("/styles/img/" + fileStr,
                        VaadinSession.getCurrent().getBrowser());

        Image image = new Image(IconStr, "");
        image.addClassName("round");
        image.setWidth(size);
        image.setHeight(size);
        if (showTitle) image.setTitle(getTranslation("commands.icon." + type.name()));

        add(image);

        setWidth(size);
        setHeight(size);

        getStyle()
                .set("margin-top", "2px")
                .set("margin-bottom", "2px");
    }

}
