package xyz.lawlietbot.spring.frontend.components.commands;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.VaadinServletService;
import org.jetbrains.annotations.NotNull;
import xyz.lawlietbot.spring.frontend.Styles;

public class CommandIcon extends Div {

    public enum Type { TRACKER, NSFW, PATREON };

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

            /*case PERMISSIONS:
                fileStr = "icon_locked.png";
                break;*/

            case TRACKER:
                fileStr = "icon_tracker.png";
                break;

            case PATREON:
                fileStr = "icon_patreon.png";
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        String IconStr = VaadinServletService.getCurrent()
                .resolveResource("/styles/img/" + fileStr);

        Image image = new Image(IconStr, "");
        image.addClassName(Styles.ROUND);
        image.setWidth(size);
        image.setHeight(size);
        if (showTitle) image.setTitle(getTranslation("commands.icon." + type.name()));

        add(image);

        setWidth(size);
        setHeight(size);

        getStyle()
                .set("margin-top", "2px")
                .set("margin-bottom", "2px")
                .set("margin-left", "8px");
    }

}
