package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.Commands.CommandCategoryLayout;
import com.gmail.leonard.spring.Frontend.Components.Commands.CommandList;
import com.gmail.leonard.spring.Frontend.Components.Commands.CommandSearchArea;
import com.gmail.leonard.spring.Frontend.Layouts.MainLayout;
import com.gmail.leonard.spring.Frontend.Layouts.PageLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@Route(value = "commands", layout = MainLayout.class)
public class CommandsView extends PageLayout {

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

    public ArrayList<CommandCategoryLayout> getCategories() {
        return categories;
    }

}
