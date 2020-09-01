package com.gmail.leonard.spring.Frontend.Components.Commands;

import com.gmail.leonard.spring.Backend.CommandList.CommandListCategory;
import com.gmail.leonard.spring.Backend.CommandList.CommandListSlot;
import com.gmail.leonard.spring.Frontend.Components.Card;
import com.gmail.leonard.spring.Frontend.Components.HtmlText;
import com.gmail.leonard.spring.Frontend.Components.LoadingIndicator;
import com.gmail.leonard.spring.Frontend.Styles;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.checkerframework.checker.units.qual.C;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CommandCategoryLayout extends VerticalLayout {

    private final CommandListCategory commandListCategory;
    private final HashMap<String, Details> commandFields = new HashMap<>();
    private AccordionPanel accordionPanel;
    private final boolean showNsfw;
    private boolean build = false;
    private String lastSearchTerm = "";
    private final LoadingIndicator loadingIndicator = new LoadingIndicator();

    public CommandCategoryLayout(CommandListCategory commandListCategory, boolean showNsfw) {
        this.commandListCategory = commandListCategory;
        this.showNsfw = showNsfw;

        setWidthFull();
        setPadding(false);
        setSpacing(false);

        loadingIndicator.getStyle().set("margin", "48px 0");
        add(loadingIndicator);
    }

    public void build() {
        if (build) return;
        build = true;
        remove(loadingIndicator);

        Locale locale = getLocale();

        for (CommandListSlot slot : commandListCategory.getSlots()) {
            VerticalLayout titleContent = new VerticalLayout();
            titleContent.setPadding(false);


            HorizontalLayout titleArea = new HorizontalLayout();
            H5 title = new H5("L." + slot.getTrigger());
            title.getStyle().set("margin-top", "0px");
            titleArea.setAlignItems(Alignment.CENTER);
            titleArea.setPadding(false);
            titleArea.setSizeUndefined();

            titleArea.add(title);
            if (slot.isNsfw()) titleArea.add(new CommandIcon(CommandIcon.Type.NSFW, true));
            if (slot.isRequiresUserPermissions()) titleArea.add(new CommandIcon(CommandIcon.Type.PERMISSIONS, true));
            if (slot.isCanBeTracked()) titleArea.add(new CommandIcon(CommandIcon.Type.TRACKER, true));
            if (slot.isPatreonOnly()) titleArea.add(new CommandIcon(CommandIcon.Type.PATREON, true));

            titleContent.add(titleArea, new Text(slot.getLangDescShort().get(locale)));

            VerticalLayout openedContent = new VerticalLayout();
            Div commandDescLong = new Div(new Text(slot.getLangDescLong().get(locale)));
            openedContent.add(commandDescLong);

            String[] specContent = {
                    slot.getLangUsage().get(locale),
                    slot.getLangExamples().get(locale),
                    slot.getLangUserPermissions().get(locale)
            };
            int n = (int) Arrays.stream(specContent).filter(e -> e != null && !e.isEmpty()).count();

            Div specs = new Div();
            specs.setWidthFull();

            boolean moreInfo = false;
            for (int i = 0; i < 3; i++) {
                if (specContent[i] != null && !specContent[i].isEmpty()) {
                    moreInfo = true;
                    VerticalLayout spec = new VerticalLayout();
                    spec.setPadding(false);

                    H5 specTitle = new H5(getTranslation("commands.specs" + i));
                    spec.add(specTitle);

                    HtmlText htmlText = new HtmlText(specContent[i]);
                    htmlText.getStyle().set("margin-top", "0px")
                        .set("padding-right", "16px");
                    spec.add(htmlText);

                    spec.setMinWidth("250px");
                    spec.setWidth((100 / n) + "%");
                    spec.getStyle().set("display", "inline-block")
                        .set("vertical-align", "top");
                    specs.add(spec);
                }
            }

            if (moreInfo)
                openedContent.add(new Hr(), specs);

            Card commandContent = new Card(openedContent);
            Details component = new Details(titleContent, commandContent);
            component.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
            component.getElement().getStyle().set("width", "100%");
            component.getElement().setAttribute("class", Styles.FADE_IN_FAST);
            if (!slot.isNsfw() || showNsfw) add(component);
            commandFields.put(slot.getTrigger(), component);
        }

        search(lastSearchTerm, locale, false, false);
    }

    public int search(String searchKey, Locale locale, boolean firstCategory, boolean changeAccordionPanel) {
        lastSearchTerm = searchKey;

        AtomicInteger found = new AtomicInteger(0);
        AtomicBoolean exactHit = new AtomicBoolean(false);

        if (accordionPanel != null) {
            for (CommandListSlot slot : commandListCategory.getSlots())
                updateSlot(slot, locale, searchKey, exactHit, found);

            if (changeAccordionPanel)
                updateAccordionPanel(found, exactHit, firstCategory);
        }

        return found.get();
    }

    private void updateAccordionPanel(AtomicInteger found, AtomicBoolean exactHit, boolean firstCategory) {
        accordionPanel.setEnabled(found.get() > 0);
        accordionPanel.setOpened((firstCategory && found.get() > 0) || exactHit.get());
        accordionPanel.setSummaryText(getSummaryText(found.get()));
    }

    private void updateSlot(CommandListSlot slot, Locale locale, String searchKey, AtomicBoolean exactHit, AtomicInteger found) {
        if (!slot.isNsfw() || showNsfw) {
            Details commandField = commandFields.get(slot.getTrigger());

            if (slot.getTrigger().replace(" ", "").equalsIgnoreCase(searchKey)) exactHit.set(true);
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

            if (visible) found.incrementAndGet();
        }
    }

    public void setAccordionPanel(AccordionPanel accordionPanel) {
        this.accordionPanel = accordionPanel;
    }

    public String getSummaryText(int n) {
        return commandListCategory.getLangName().get(getLocale()) + " (" + getTranslation("commands.searchresults", n != 1, n) + ")";
    }
}
