package com.gmail.leonard.spring.frontend.views;

import com.gmail.leonard.spring.backend.userdata.SessionData;
import com.gmail.leonard.spring.backend.userdata.UIData;
import com.gmail.leonard.spring.frontend.components.commands.CommandCategoryLayout;
import com.gmail.leonard.spring.frontend.components.commands.CommandList;
import com.gmail.leonard.spring.frontend.components.commands.CommandSearchArea;
import com.gmail.leonard.spring.frontend.layouts.MainLayout;
import com.gmail.leonard.spring.frontend.layouts.PageLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@Route(value = "commands", layout = MainLayout.class)
@CssImport("./styles/commands.css")
public class CommandsView extends PageLayout {

    private final ArrayList<CommandCategoryLayout> categories = new ArrayList<>();

    public CommandsView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);
        getStyle().set("margin-bottom", "48px");
        add(new CommandSearchArea(this, uiData, getRoute()), new CommandList(this, uiData));
    }

    public ArrayList<CommandCategoryLayout> getCategories() {
        return categories;
    }

}
