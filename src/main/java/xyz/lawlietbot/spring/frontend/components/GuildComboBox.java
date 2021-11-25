package xyz.lawlietbot.spring.frontend.components;

import bell.oauth.discord.domain.Guild;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

public class GuildComboBox extends ComboBox<Guild> {

    public GuildComboBox() {
        setItemLabelGenerator((ItemLabelGenerator<Guild>) Guild::getName);
        setPlaceholder(getTranslation("premium.server"));
        setRenderer(new ComponentRenderer<>(guild -> {
            HorizontalLayout layout = new HorizontalLayout();
            layout.setAlignItems(FlexComponent.Alignment.CENTER);
            layout.setPadding(false);
            if (guild.getIcon() != null) {
                Image image = new Image(guild.getIcon(), "");
                image.setHeight("24px");
                image.getStyle().set("border-radius", "50%")
                        .set("margin-left", "0")
                        .set("margin-right", "8px");
                layout.add(image);
            }
            layout.add(new Text(guild.getName()));
            return layout;
        }));
    }

}
