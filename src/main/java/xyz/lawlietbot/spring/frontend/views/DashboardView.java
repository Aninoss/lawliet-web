package xyz.lawlietbot.spring.frontend.views;

import bell.oauth.discord.domain.Guild;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.LoginAccess;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;

@Route(value = "dashboard", layout = MainLayout.class)
@CssImport("./styles/dashboard.css")
@NoLiteAccess
@LoginAccess
public class DashboardView extends PageLayout {

    private final VerticalLayout mainLayout = new VerticalLayout();

    public DashboardView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);

        FlexLayout content = new FlexLayout();
        content.setFlexDirection(FlexLayout.FlexDirection.ROW);
        content.setSizeFull();

        Div left = new Div();
        left.addClassName("dashboard-col");

        Div right = new Div();
        right.addClassName("dashboard-col");

        content.add(left, generateMain(), right);
        add(content);
    }

    private Component generateMain() {
        FlexLayout content = new FlexLayout();
        content.setFlexDirection(FlexLayout.FlexDirection.ROW);
        content.setId("dashboard-center");
        if (getSessionData().isLoggedIn()) {
            mainLayout.setId("dashboard-main");
            content.add(generateCategoryBar(), mainLayout);
            updateMainContent();
        }
        return content;
    }

    private Component generateCategoryBar() {
        VerticalLayout content = new VerticalLayout();
        content.setId("dashboard-category-bar");
        content.addClassNames(Styles.VISIBLE_NOTMOBILE);

        ComboBox<Guild> guildComboBox = new ComboBox<>();
        guildComboBox.setItemLabelGenerator((ItemLabelGenerator<Guild>) Guild::getName);
        guildComboBox.setPlaceholder(getTranslation("premium.server"));
        guildComboBox.setItems(getSessionData().getDiscordUser().get().getGuilds());
        guildComboBox.setWidthFull();
        content.add(guildComboBox);

        return content;
    }

    private void updateMainContent() {
        mainLayout.removeAll();
        /*H2 categoryTitle = new H2("Test");
        categoryTitle.getStyle().set("margin-top", "12px");
        mainLayout.add(categoryTitle);*/
    }

}
