package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Backend.Language.PageTitleFactory;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.Commands.CommandCategoryLayout;
import com.gmail.leonard.spring.Frontend.Components.Commands.CommandList;
import com.gmail.leonard.spring.Frontend.Components.Commands.CommandSearchArea;
import com.gmail.leonard.spring.Frontend.Layouts.MainLayout;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@Route(value = CommandsView.ID, layout = MainLayout.class)
public class CommandsView extends Main implements HasDynamicTitle {

    public static final String ID = "commands";
    private ArrayList<CommandCategoryLayout> categories = new ArrayList<>();

    public CommandsView(@Autowired UIData uiData) {
        setWidthFull();

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setPadding(false);
        verticalLayout.setSizeFull();

        verticalLayout.add(new CommandSearchArea(this, uiData));
        verticalLayout.add(new CommandList(this, uiData));

        add(verticalLayout);
    }

    @Override
    public String getPageTitle() {
        return PageTitleFactory.getPageTitle(ID);
    }

    public ArrayList<CommandCategoryLayout> getCategories() {
        return categories;
    }
}
