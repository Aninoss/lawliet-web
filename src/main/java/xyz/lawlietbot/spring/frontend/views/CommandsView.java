package xyz.lawlietbot.spring.frontend.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.components.commands.CommandCategoryLayout;
import xyz.lawlietbot.spring.frontend.components.commands.CommandList;
import xyz.lawlietbot.spring.frontend.components.commands.CommandSearchArea;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;

@Route(value = "commands", layout = MainLayout.class)
@CssImport("./styles/commands.css")
public class CommandsView extends PageLayout implements HasUrlParameter<String> {

    private final ArrayList<CommandCategoryLayout> categories = new ArrayList<>();
    private CommandList commandList;

    public CommandsView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);
        getStyle().set("margin-bottom", "48px");
        commandList = new CommandList(this, uiData);
        add(
                new CommandSearchArea(this, uiData, getRoute()),
                commandList
        );
    }

    public ArrayList<CommandCategoryLayout> getCategories() {
        return categories;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> parametersMap = queryParameters.getParameters();

        if (parametersMap.containsKey("c") && parametersMap.get("c").size() == 1) {
            String category = parametersMap.get("c").get(0);
            commandList.openCategory(category);
        }
    }

}
