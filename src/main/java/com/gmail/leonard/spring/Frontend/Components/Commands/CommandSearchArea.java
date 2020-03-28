package com.gmail.leonard.spring.Frontend.Components.Commands;

import com.gmail.leonard.spring.Backend.CommandList.CommandListContainer;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.IconLabel;
import com.gmail.leonard.spring.Frontend.Styles;
import com.gmail.leonard.spring.Frontend.Views.CommandsView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class CommandSearchArea extends Div {

    public CommandSearchArea(CommandsView parent, UIData uiData) {
        setWidthFull();
        setId("commands-searcharea");

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.setPadding(true);

        HorizontalLayout searchArea = new HorizontalLayout();
        searchArea.setPadding(false);
        searchArea.setSpacing(false);
        searchArea.setWidthFull();

        H1 title = new H1(parent.getTitleText());
        title.setWidthFull();
        mainContent.add(title);

        TextField searchField = new TextField();
        searchField.getStyle().set("margin-top", "0");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setWidthFull();
        int n = CommandListContainer.getInstance().allCommandsSize(!uiData.isNSFWDisabled());
        Div searchResults = new Div(new Text(getTranslation("commands.searchresults", n != 1, n)));
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
            mainContent.add(new IconLabel(VaadinIcon.WARNING.create(), getTranslation("commands.hide")));
        }

        add(mainContent);
    }
}
