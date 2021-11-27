package xyz.lawlietbot.spring.frontend.views;

import java.util.List;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.LoginAccess;
import xyz.lawlietbot.spring.NavBarSolid;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.dashboard.DashboardInitData;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.GuildComboBox;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.syncserver.SendEvent;

@Route(value = "dashboard", layout = MainLayout.class)
@CssImport("./styles/dashboard.css")
@NoLiteAccess
@LoginAccess
@NavBarSolid
public class DashboardView extends PageLayout {

    private final VerticalLayout mainLayout = new VerticalLayout();
    private final Tabs categoryTabs = new Tabs();
    private List<DashboardInitData.Category> categoryList;

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
        }
        return content;
    }

    private Component generateCategoryBar() {
        VerticalLayout content = new VerticalLayout();
        content.setId("dashboard-category-bar");
        content.addClassNames(Styles.VISIBLE_NOTMOBILE);

        categoryTabs.setWidthFull();
        categoryTabs.setOrientation(Tabs.Orientation.VERTICAL);
        categoryTabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        categoryTabs.setVisible(false);
        categoryTabs.getStyle().set("margin-left", "-16px");
        categoryTabs.addSelectedChangeListener(e -> {
            if (categoryTabs.getSelectedIndex() >= 0) {
                DashboardInitData.Category category = categoryList.get(categoryTabs.getSelectedIndex());
                updateMainContent(category);
            }
        });

        content.add(generateGuildSelection(), categoryTabs);
        return content;
    }

    private Component generateGuildSelection() {
        HorizontalLayout guildLayout = new HorizontalLayout();
        guildLayout.setPadding(false);
        guildLayout.setWidthFull();
        guildLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Image image = new Image();
        image.setHeight("32px");
        image.getStyle().set("border-radius", "50%")
                .set("margin-right", "-4px");

        GuildComboBox guildComboBox = new GuildComboBox();
        guildComboBox.setItems(getSessionData().getDiscordUser().get().getGuilds());
        guildComboBox.setWidthFull();
        guildComboBox.addValueChangeListener(e -> {
            categoryTabs.removeAll();
            long guildId = e.getValue().getId();
            DashboardInitData dashboardInitData = SendEvent.sendDashboardInit(guildId, UI.getCurrent().getLocale()).join();
            categoryList = dashboardInitData.getCategories();
            for (DashboardInitData.Category category : categoryList) {
                Tab tab = new Tab(category.getTitle());
                categoryTabs.add(tab);
            }
            categoryTabs.setVisible(true);
            if (e.getValue().getIcon() != null) {
                image.setVisible(true);
                image.setSrc(e.getValue().getIcon());
            } else {
                image.setVisible(false);
            }
        });

        guildLayout.add(image, guildComboBox);
        return guildLayout;
    }

    private void updateMainContent(DashboardInitData.Category category) {
        mainLayout.removeAll();

        if (category != null) {
            H2 categoryTitle = new H2(category.getTitle());
            categoryTitle.getStyle().set("margin-top", "12px");
            mainLayout.add(categoryTitle);
        }
    }

}
