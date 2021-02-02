package xyz.lawlietbot.spring.frontend.components.commands;

import xyz.lawlietbot.spring.backend.commandlist.CommandListContainer;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.components.IconLabel;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.views.CommandsView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class CommandSearchArea extends PageHeader {

    public CommandSearchArea(CommandsView parent, UIData uiData, String route) {
        super(parent.getTitleText(), null, route);

        HorizontalLayout searchArea = new HorizontalLayout();
        searchArea.setPadding(false);
        searchArea.setSpacing(false);
        searchArea.setWidthFull();

        TextField searchField = new TextField();
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

        getInnerLayout().add(searchField, searchResults);

        if (uiData.isNSFWDisabled()) {
            getInnerLayout().add(new IconLabel(VaadinIcon.WARNING.create(), getTranslation("commands.hide")));
        }
    }
}
