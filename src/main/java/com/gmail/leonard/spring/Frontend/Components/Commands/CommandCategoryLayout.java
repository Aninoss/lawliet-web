package com.gmail.leonard.spring.Frontend.Components.Commands;

import com.gmail.leonard.spring.Backend.CommandList.CommandListCategory;
import com.gmail.leonard.spring.Backend.CommandList.CommandListSlot;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.apache.commons.lang3.StringEscapeUtils;
import java.util.HashMap;
import java.util.Locale;

public class CommandCategoryLayout extends VerticalLayout {

    private CommandListCategory commandListCategory;
    private HashMap<String, Details> commandFields = new HashMap<>();
    private AccordionPanel accordionPanel;
    private boolean showNsfw, build = false;
    private String lastSearchTerm = "";
    private Div loadingDiv;

    public CommandCategoryLayout(CommandListCategory commandListCategory, boolean showNsfw) {
        this.commandListCategory = commandListCategory;
        this.showNsfw = showNsfw;

        setWidthFull();
        setPadding(false);
        setSpacing(false);

        loadingDiv = new Div(new Div(), new Div(), new Div(), new Div());
        loadingDiv.addClassName("lds-ellipsis");

        add(loadingDiv);
    }

    public void build() {
        if (build) return;
        build = true;

        remove(loadingDiv);

        Locale locale = getLocale();

        for (CommandListSlot slot : commandListCategory.getSlots()) {
            VerticalLayout titleContent = new VerticalLayout();
            titleContent.setPadding(false);


            HorizontalLayout titleArea = new HorizontalLayout();
            H5 title = new H5(slot.getEmoji() + "⠀L." + slot.getTrigger());
            title.getStyle().set("margin-top", "0px");
            titleArea.setAlignItems(Alignment.CENTER);
            titleArea.setPadding(false);
            titleArea.setSizeUndefined();

            titleArea.add(title);
            if (slot.isNsfw()) titleArea.add(new CommandIcon(CommandIcon.Type.NSFW, true));
            if (slot.isRequiresUserPermissions()) titleArea.add(new CommandIcon(CommandIcon.Type.PERMISSIONS, true));
            if (slot.isCanBeTracked()) titleArea.add(new CommandIcon(CommandIcon.Type.TRACKER, true));

            titleContent.add(titleArea, new Text(slot.getLangDescShort().get(locale)));

            VerticalLayout openedContent = new VerticalLayout();
            openedContent.setPadding(false);

            Hr seperator = new Hr();
            seperator.getStyle().set("margin-top", "16px");
            openedContent.add(seperator);

            openedContent.add(new Text(slot.getLangDescLong().get(locale)));

            String[] specContent = {slot.getLangUsage().get(locale), slot.getLangExamples().get(locale), slot.getLangUserPermissions().get(locale)};

            VerticalLayout specs = new VerticalLayout();
            specs.setPadding(false);

            for (int i = 0; i < 3; i++) {
                if (specContent[i] != null && !specContent[i].isEmpty()) {
                    VerticalLayout spec = new VerticalLayout();
                    spec.setPadding(false);

                    spec.add(new H5(getTranslation("commands.specs" + i)));

                    Span specContentText = new Span();
                    specContentText.getElement().setProperty("innerHTML", StringEscapeUtils.escapeHtml4(specContent[i]).replace("\n", "<br>"));
                    specContentText.getStyle().set("margin-top", "0px");
                    spec.add(specContentText);

                    specs.add(spec);
                }
            }
            openedContent.add(specs);


            Details component = new Details(titleContent, openedContent);
            component.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
            component.getElement().getStyle().set("width", "100%");
            if (!slot.isNsfw() || showNsfw) add(component);
            commandFields.put(slot.getTrigger(), component);
        }

        search(lastSearchTerm, locale, false, false);
    }

    public int search(String searchKey, Locale locale, boolean firstCategory, boolean changeAccordionPanel) {
        lastSearchTerm = searchKey;

        int found = 0;
        boolean exactHit = false;

        if (accordionPanel != null) {
            for (CommandListSlot slot : commandListCategory.getSlots()) {
                if (!slot.isNsfw() || showNsfw) {
                    Details commandField = commandFields.get(slot.getTrigger());

                    if (slot.getTrigger().replace(" ", "").equalsIgnoreCase(searchKey)) exactHit = true;
                    boolean visible = slot.getTrigger().toLowerCase().replace(" ", "").contains(searchKey) ||
                            slot.getLangDescLong().get(locale).toLowerCase().replace(" ", "").contains(searchKey) ||
                            slot.getLangExamples().get(locale).toLowerCase().replace(" ", "").contains(searchKey) ||
                            slot.getLangDescShort().get(locale).toLowerCase().replace(" ", "").contains(searchKey) ||
                            slot.getLangUsage().get(locale).toLowerCase().replace(" ", "").contains(searchKey) ||
                            slot.getTrigger().toLowerCase().replace(" ", "").contains(searchKey);

                    if (commandField != null) {
                        commandField.setVisible(visible);
                        commandField.setOpened(false);
                    }
                    if (visible) found++;
                }
            }

            if (changeAccordionPanel) {
                accordionPanel.setEnabled(found > 0);
                accordionPanel.setOpened((firstCategory && found > 0) || exactHit);
                accordionPanel.setSummaryText(getSummaryText(found));
            }
        }
        return found;
    }

    public void setAccordionPanel(AccordionPanel accordionPanel) {
        this.accordionPanel = accordionPanel;
    }

    public String getSummaryText(int n) {
        return commandListCategory.getLangName().get(getLocale()) + " (" + getTranslation("commands.searchresults", n != 1, n) + ")";
    }
}
