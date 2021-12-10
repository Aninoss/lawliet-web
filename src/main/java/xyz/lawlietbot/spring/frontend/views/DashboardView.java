package xyz.lawlietbot.spring.frontend.views;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.*;
import dashboard.ActionResult;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.LoginAccess;
import xyz.lawlietbot.spring.NavBarSolid;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.dashboard.DashboardCategoryInitData;
import xyz.lawlietbot.spring.backend.dashboard.DashboardInitData;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.frontend.components.GuildComboBox;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardComponentConverter;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.syncserver.SendEvent;

@Route(value = "dashboard", layout = MainLayout.class)
@CssImport("./styles/dashboard.css")
@NoLiteAccess
@LoginAccess
@NavBarSolid
public class DashboardView extends PageLayout implements HasUrlParameter<Long> {

    private final VerticalLayout mainLayout = new VerticalLayout();
    private final VerticalLayout tabsLayout = new VerticalLayout();
    private final Tabs categoryTabs = new Tabs();
    private final GuildComboBox guildComboBox = new GuildComboBox();
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
            mainLayout.addClassNames(Styles.VISIBLE_LARGE);
            content.add(generateCategoryBar(), mainLayout);
        }
        return content;
    }

    private Component generateCategoryBar() {
        tabsLayout.setId("dashboard-category-bar");
        categoryTabs.setWidthFull();
        categoryTabs.setOrientation(Tabs.Orientation.VERTICAL);
        categoryTabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        categoryTabs.setVisible(false);
        categoryTabs.getStyle().set("margin-left", "-16px");
        categoryTabs.setAutoselect(false);
        categoryTabs.addSelectedChangeListener(e -> {
            if (categoryTabs.getSelectedIndex() >= 0) {
                DashboardInitData.Category category = categoryList.get(categoryTabs.getSelectedIndex());
                updateMainContent(category);
                pushNewUri();
            }
        });

        Hr hr = new Hr();
        hr.setId("dashboard-tabs-hr");

        tabsLayout.add(generateGuildSelection(), hr, categoryTabs);
        return tabsLayout;
    }

    private Component generateGuildSelection() {
        HorizontalLayout guildLayout = new HorizontalLayout();
        guildLayout.setPadding(false);
        guildLayout.setWidthFull();
        guildLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Image image = new Image();
        image.setHeight("32px");
        image.setVisible(false);
        image.getStyle().set("border-radius", "50%")
                .set("margin-right", "-4px");

        guildComboBox.setItems(getSessionData().getDiscordUser().get().getGuilds());
        guildComboBox.setWidthFull();
        guildComboBox.addValueChangeListener(e -> {
            categoryTabs.removeAll();
            if (e.getValue() != null) {
                long guildId = e.getValue().getId();
                long userId = getSessionData().getDiscordUser().get().getId();
                DashboardInitData dashboardInitData = null;
                try {
                    dashboardInitData = SendEvent.sendDashboardInit(guildId, userId, UI.getCurrent().getLocale()).get(5, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                    throw new RuntimeException(ex);
                }
                if (dashboardInitData != null) {
                    categoryList = dashboardInitData.getCategories();
                    for (DashboardInitData.Category category : categoryList) {
                        Tab tab = new Tab(category.getTitle());
                        categoryTabs.add(tab);
                    }
                    categoryTabs.setVisible(true);
                    mainLayout.removeAll();
                } else {
                    categoryList = Collections.emptyList();
                    updateMainContent(null);
                }
                if (e.getValue().getIcon() != null) {
                    image.setVisible(true);
                    image.setSrc(e.getValue().getIcon());
                } else {
                    image.setVisible(false);
                }
                pushNewUri();
            } else {
                image.setVisible(false);
                categoryList = Collections.emptyList();
            }
        });

        guildLayout.add(image, guildComboBox);
        return guildLayout;
    }

    private void updateMainContent(DashboardInitData.Category category) {
        mainLayout.removeAll();
        mainLayout.setClassName(Styles.VISIBLE_LARGE, false);
        tabsLayout.setClassName(Styles.VISIBLE_LARGE, true);

        if (category != null) {
            H2 categoryTitle = new H2(category.getTitle());
            categoryTitle.getStyle().set("margin-top", "12px");
            mainLayout.add(categoryTitle);

            DashboardCategoryInitData data;
            try {
                data = SendEvent.sendDashboardCategoryInit(
                        category.getId(),
                        guildComboBox.getValue().getId(),
                        getSessionData().getDiscordUser().get().getId(),
                        getLocale()
                ).get(5, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }

            if (data.getMissingUserPermissions().isEmpty() && data.getMissingBotPermissions().isEmpty()) {
                Component component = DashboardComponentConverter.convert(data.getComponents());
                ((HasSize) component).setWidthFull();
                mainLayout.add(component);
                data.getComponents().setActionSendListener(json -> {
                    try {
                        ActionResult actionResult = SendEvent.sendDashboardAction(
                                guildComboBox.getValue().getId(),
                                getSessionData().getDiscordUser().get().getId(),
                                json
                        ).get(5, TimeUnit.SECONDS);
                        if (actionResult.getSuccessMessage() != null) {
                            CustomNotification.showSuccess(actionResult.getSuccessMessage());
                        }
                        if (actionResult.getErrorMessage() != null) {
                            //TODO: show error
                        }
                        if (actionResult.getRedraw()) {
                            updateMainContent(category);
                        }
                    } catch (Throwable e) {
                        //TODO: show error
                    }
                });
            } else {
                mainLayout.add(generateMissingPermissions(data.getMissingUserPermissions(), data.getMissingBotPermissions()));
            }
        } else {
            H2 invalidServerTitle = new H2(getTranslation("dash.invalidserver.title"));
            invalidServerTitle.getStyle().set("margin-top", "12px");
            mainLayout.add(invalidServerTitle);

            Text invalidServerText = new Text(getTranslation("dash.invalidserver.desc"));
            mainLayout.add(invalidServerText);
            mainLayout.add(generateInvalidServerButtons());
        }

        Button backButton = new Button(getTranslation("dash.back"), VaadinIcon.ARROW_LEFT.create());
        backButton.addClassNames(Styles.VISIBLE_NOT_LARGE);
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> updateMainContentBack(category == null));
        mainLayout.add(backButton);
    }

    private void updateMainContentBack(boolean resetGuild) {
        mainLayout.removeAll();
        mainLayout.setClassName(Styles.VISIBLE_LARGE, true);
        tabsLayout.setClassName(Styles.VISIBLE_LARGE, false);
        categoryTabs.setSelectedIndex(-1);
        if (resetGuild) {
            guildComboBox.setValue(null);
        }
        pushNewUri();
    }

    private Component generateMissingPermissions(List<String> missingUserPermissions, List<String> missingBotPermissions) {
        VerticalLayout content = new VerticalLayout();
        content.setId("dashboard-lock-layout");
        content.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon lockIcon = VaadinIcon.LOCK.create();
        lockIcon.setId("dashboard-lock-icon");
        content.add(lockIcon);

        List<List<String>> missingPermissionsLists = List.of(missingUserPermissions, missingBotPermissions);
        String[] missingPermissionsTexts = new String[] {
                getTranslation("dash.missingpermissions.you"),
                getTranslation("dash.missingpermissions.bot")
        };

        for (int i = 0; i < 2; i++) {
            List<String> missingPermissionsList = missingPermissionsLists.get(i);
            if (missingPermissionsList.size() > 0) {
                content.add(new Hr());

                H3 h3 = new H3(missingPermissionsTexts[i]);
                h3.setId("dashboard-lock-h3");
                content.add(h3);

                UnorderedList ul = new UnorderedList();
                ul.setId("dashboard-lock-ul");
                missingPermissionsList.forEach(p -> ul.add(new ListItem(p)));
                content.add(ul);
            }
        }

        return content;
    }

    private Component generateInvalidServerButtons() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setPadding(false);
        buttonLayout.getStyle().set("margin-top", "32px");

        Button inviteButton = new Button(getTranslation("bot.invite"), VaadinIcon.ARROW_RIGHT.create());
        inviteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        inviteButton.setIconAfterText(true);

        Anchor a = new Anchor(getUiData().getBotInviteUrl(), inviteButton);
        a.setTarget("_blank");
        buttonLayout.add(a);

        Button refreshButton = new Button(getTranslation("dash.invalidserver.refresh"));
        refreshButton.addClickListener(e -> UI.getCurrent().getPage().reload());
        buttonLayout.add(refreshButton);

        return buttonLayout;
    }

    private void pushNewUri() {
        StringBuilder uri = new StringBuilder(DashboardView.getRouteStatic(DashboardView.class));
        if (guildComboBox.getValue() != null) {
            uri.append("/").append(guildComboBox.getValue().getId());
            if (categoryTabs.getSelectedIndex() >= 0) {
                uri.append("?cat=").append(categoryList.get(categoryTabs.getSelectedIndex()).getId());
            }
        }
        getSessionData().pushUri(uri.toString());
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long guildId) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location
                .getQueryParameters();

        Map<String, List<String>> parametersMap =
                queryParameters.getParameters();

        if (guildId != null) {
            getSessionData().getDiscordUser()
                    .flatMap(u -> u.getGuilds().stream().filter(g -> g.getId() == guildId).findFirst())
                    .ifPresent(guild -> {
                        guildComboBox.setValue(guild);
                        if (categoryList != null && categoryList.size() > 0) {
                            List<String> categoryIdList = parametersMap.getOrDefault("cat", Collections.emptyList());
                            String categoryId = categoryIdList.size() > 0 ? categoryIdList.get(0) : null;
                            int index = -1;
                            for (int i = 0; i < categoryList.size(); i++) {
                                if (categoryList.get(i).getId().equals(categoryId)) {
                                    index = i;
                                    break;
                                }
                            }
                            categoryTabs.setSelectedIndex(index);
                            if (index == -1) {
                                updateMainContentBack(false);
                            }
                        }
                    });
        }
    }

}
