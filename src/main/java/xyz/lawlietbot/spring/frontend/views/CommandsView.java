package xyz.lawlietbot.spring.frontend.views;

import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.components.commands.CommandCategoryLayout;
import xyz.lawlietbot.spring.frontend.components.commands.CommandList;
import xyz.lawlietbot.spring.frontend.components.commands.CommandSearchArea;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
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
