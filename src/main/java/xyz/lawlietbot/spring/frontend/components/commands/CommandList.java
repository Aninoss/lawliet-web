package xyz.lawlietbot.spring.frontend.components.commands;

import xyz.lawlietbot.spring.backend.commandlist.CommandListCategory;
import xyz.lawlietbot.spring.backend.commandlist.CommandListContainer;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.views.CommandsView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class CommandList extends VerticalLayout {

    public CommandList(CommandsView parent, UIData uiData) {
        addClassName(Styles.APP_WIDTH);
        getStyle().set("margin-top", "-14px");

        Accordion accordion = new Accordion();
        accordion.setWidthFull();

        for (CommandListCategory commandListCategory : CommandListContainer.getInstance().getCategories()) {
            CommandCategoryLayout commandCategoryLayout = new CommandCategoryLayout(commandListCategory, !uiData.isNSFWDisabled());
            parent.getCategories().add(commandCategoryLayout);
            if (commandListCategory.hasCommands(!uiData.isNSFWDisabled())) {
                AccordionPanel accordionPanel = new AccordionPanel(commandCategoryLayout.getSummaryText(commandListCategory.size(!uiData.isNSFWDisabled())), commandCategoryLayout);
                accordion.add(accordionPanel);
                commandCategoryLayout.setAccordionPanel(accordionPanel);
                accordionPanel.addOpenedChangeListener(listener -> {
                    if (listener.isOpened()) commandCategoryLayout.build();
                });
            }
        }

        add(accordion);

        VerticalLayout notesLayout = new VerticalLayout();
        notesLayout.setPadding(false);
        notesLayout.setSpacing(false);
        notesLayout.setWidthFull();
        notesLayout.getStyle().set("margin-top", "2em");

        for(CommandIcon.Type type: CommandIcon.Type.values()) {
            if (type != CommandIcon.Type.NSFW || !uiData.isNSFWDisabled()) {
                CommandIcon commandIcon = new CommandIcon(type);
                commandIcon.getStyle()
                        .set("margin-left", "0")
                        .set("margin-right", "8px");

                Div equalSignLabel = new Div(new Text("="));
                equalSignLabel.getStyle().set("margin-right", "8px");

                Text contentLabel = new Text(getTranslation("commands.icon." + type.name()));

                HorizontalLayout info = new HorizontalLayout(commandIcon, equalSignLabel, contentLabel);
                info.setSpacing(false);
                info.setAlignItems(Alignment.CENTER);
                info.getStyle().set("margin-top", "4px");
                info.setWidthFull();
                notesLayout.add(info);
            }
        }

        add(notesLayout);
    }
}
