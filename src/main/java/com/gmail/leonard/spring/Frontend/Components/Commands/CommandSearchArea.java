package com.gmail.leonard.spring.Frontend.Components.Commands;

import com.gmail.leonard.spring.Backend.CommandList.CommandListContainer;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.CustomButton;
import com.gmail.leonard.spring.Frontend.Views.CommandsView;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class CommandSearchArea extends Div {

    public CommandSearchArea(CommandsView parent, UIData uiData) {
        setWidthFull();
        setId("commands-searcharea");

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.addClassName("app-width");
        mainContent.setPadding(true);

        HorizontalLayout searchArea = new HorizontalLayout();
        searchArea.setPadding(false);
        searchArea.setSpacing(false);
        searchArea.setWidthFull();

        H2 title = new H2(getTranslation("category." + CommandsView.ID));
        mainContent.add(title);

        TextField searchField = new TextField();
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setWidthFull();
        int n = CommandListContainer.getInstance().allCommandsSize(!uiData.isNSFWDisabled());
        Label searchResults = new Label(getTranslation("commands.searchresults", n != 1, n));
        searchResults.setId("commands-resultslabel");
        searchField.setPlaceholder(getTranslation("commands.search"));
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.setValueChangeTimeout(500);
        searchField.addValueChangeListener(event -> {
            int found = 0;
            String searchKey = event.getValue().toLowerCase().replace(" ", "");

            for(CommandCategoryLayout category: parent.getCategories())
                found += category.search(searchKey, getLocale(), found == 0, true);

            if (searchKey.isEmpty()) searchResults.setText("");
            searchResults.setText(getTranslation("commands.searchresults", found != 1, found));
        });

        mainContent.add(searchField);
        mainContent.add(searchResults);

        if (uiData.isNSFWDisabled()) {
            Label hideLabel = new Label(getTranslation("commands.hide"));
            hideLabel.getStyle().set("font-size", "80%");

            Icon icon = VaadinIcon.INFO_CIRCLE.create();
            icon.setSize("18px");
            icon.getStyle().set("margin-right", "4px");

            HorizontalLayout info = new HorizontalLayout(icon, hideLabel);
            info.setSpacing(false);
            info.setAlignItems(FlexComponent.Alignment.CENTER);
            mainContent.add(info);
        }

        add(mainContent);
    }
}
