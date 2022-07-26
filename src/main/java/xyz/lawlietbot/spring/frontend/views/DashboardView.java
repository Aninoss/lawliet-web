package xyz.lawlietbot.spring.frontend.views;

import java.util.*;
import java.util.concurrent.ExecutionException;
import bell.oauth.discord.domain.Guild;
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
import dashboard.DashboardComponent;
import dashboard.container.DashboardContainer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.LoginAccess;
import xyz.lawlietbot.spring.NavBarSolid;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.dashboard.DashboardCategoryInitData;
import xyz.lawlietbot.spring.backend.dashboard.DashboardInitData;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.frontend.components.GuildComboBox;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardComponentConverter;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.syncserver.EventOut;
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
    private final VerticalLayout premiumUnlockedLayout = new VerticalLayout();
    private final GuildComboBox guildComboBox = new GuildComboBox();
    private final ConfirmationDialog confirmationDialog = new ConfirmationDialog();
    private List<DashboardInitData.Category> categoryList;
    private String autoCategoryId = null;

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
        add(content, confirmationDialog);
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
        categoryTabs.getStyle().set("margin-left", "-16px")
                .set("margin-top", "10px");
        categoryTabs.setAutoselect(false);
        categoryTabs.addSelectedChangeListener(e -> {
            if (categoryTabs.getSelectedIndex() >= 0) {
                DashboardInitData.Category category = categoryList.get(categoryTabs.getSelectedIndex());
                updateMainContentCategory(category, true);
                pushNewUri();
            }
        });

        premiumUnlockedLayout.setId("dashboard-premium-unlocked-layout");
        premiumUnlockedLayout.setWidthFull();
        premiumUnlockedLayout.setVisible(false);
        premiumUnlockedLayout.setPadding(true);

        Hr hr = new Hr();
        hr.setId("dashboard-tabs-hr");

        tabsLayout.add(generateGuildSelection(), hr, categoryTabs, premiumUnlockedLayout);
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
            premiumUnlockedLayout.setVisible(false);
            if (e.getValue() != null) {
                long guildId = e.getValue().getId();
                long userId = getSessionData().getDiscordUser().get().getId();
                DashboardInitData dashboardInitData = sendDashboardInit(guildId, userId, UI.getCurrent().getLocale());

                if (dashboardInitData != null) {
                    categoryList = dashboardInitData.getCategories();
                    for (DashboardInitData.Category category : categoryList) {
                        Tab tab = new Tab(category.getTitle());
                        categoryTabs.add(tab);
                    }
                    categoryTabs.setVisible(true);
                    premiumUnlockedLayout.setVisible(true);
                    updatePremiumUnlocked(dashboardInitData.isPremiumUnlocked());

                    int index = -1;
                    if (autoCategoryId != null && categoryList != null && categoryList.size() > 0) {
                        for (int i = 0; i < categoryList.size(); i++) {
                            if (categoryList.get(i).getId().equals(autoCategoryId)) {
                                index = i;
                                break;
                            }
                        }
                    }
                    autoCategoryId = null;

                    if (index != -1) {
                        categoryTabs.setSelectedIndex(index);
                    } else {
                        UI.getCurrent().getPage().retrieveExtendedClientDetails(receiver -> {
                            int screenWidth = receiver.getScreenWidth();
                            if (screenWidth >= 1000) {
                                categoryTabs.setSelectedIndex(0);
                            } else {
                                updateMainContentEntry(e.getValue());
                                pushNewUri();
                            }
                        });
                    }
                } else {
                    categoryList = Collections.emptyList();
                    updateMainContentCategory(null, true);
                    pushNewUri();
                }

                if (e.getValue().getIcon() != null) {
                    image.setVisible(true);
                    image.setSrc(e.getValue().getIcon());
                } else {
                    image.setVisible(false);
                }
            } else {
                image.setVisible(false);
                categoryList = Collections.emptyList();
            }
        });

        guildLayout.add(image, guildComboBox);
        return guildLayout;
    }

    private void updateMainContentEntry(Guild guild) {
        mainLayout.removeAll();
        mainLayout.setClassName(Styles.VISIBLE_LARGE, true);
        tabsLayout.setClassName(Styles.VISIBLE_LARGE, false);

        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(false);
        layout.getStyle().set("margin-top", "12px");

        if (guild.getIcon() != null) {
            Image image = new Image(guild.getIcon(), "");
            image.setHeight("100px");
            image.getStyle().set("border-radius", "8px")
                    .set("margin-top", "8px")
                    .set("margin-right", "6px");
            layout.add(image);
        }

        VerticalLayout titleLayout = new VerticalLayout();
        titleLayout.setPadding(false);

        H2 pageTitle = new H2(guild.getName());
        pageTitle.getStyle().set("margin-top", "16px")
                .set("margin-bottom", "8px");
        titleLayout.add(pageTitle, new Text(getTranslation("dashboard.selectpage")));

        layout.add(titleLayout);
        mainLayout.add(layout);
    }

    private void updateMainContentCategory(DashboardInitData.Category category, boolean createNew) {
        mainLayout.removeAll();
        mainLayout.setClassName(Styles.VISIBLE_LARGE, false);
        tabsLayout.setClassName(Styles.VISIBLE_LARGE, true);

        FlexLayout titleLayout = new FlexLayout();
        titleLayout.setFlexDirection(FlexLayout.FlexDirection.ROW);
        titleLayout.getStyle().set("margin-top", "12px");

        Button backButton = new Button(VaadinIcon.ARROW_LEFT.create());
        backButton.getStyle().set("margin-left", "-12px");
        backButton.addClassNames(Styles.VISIBLE_NOT_LARGE);
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> updateMainContentBack(category == null));
        mainLayout.add(backButton);

        H2 pageTitle = new H2();
        pageTitle.getStyle().set("margin-top", "0");

        titleLayout.add(backButton, pageTitle);
        mainLayout.add(titleLayout);

        if (category != null) {
            mainLayout.add(generateMainWithCategory(pageTitle, category, createNew));
        } else {
            pageTitle.setText(getTranslation("dash.invalidserver.title"));
            Text invalidServerText = new Text(getTranslation("dash.invalidserver.desc"));
            mainLayout.add(invalidServerText);
            mainLayout.add(generateInvalidServerButtons());
        }
    }

    private void updatePremiumUnlocked(boolean premiumUnlocked) {
        premiumUnlockedLayout.removeAll();

        FlexLayout textLayout = new FlexLayout();
        textLayout.setWidthFull();
        textLayout.setFlexDirection(FlexLayout.FlexDirection.ROW);
        textLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        textLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Text text = new Text(getTranslation("dash.unlocked"));
        Icon yesNo = (premiumUnlocked ? VaadinIcon.CHECK : VaadinIcon.CLOSE).create();
        yesNo.addClassName(premiumUnlocked ? "dashboard-premium-unlocked-yes" : "dashboard-premium-unlocked-no");
        textLayout.add(text, yesNo);
        premiumUnlockedLayout.add(textLayout);

        if (!premiumUnlocked) {
            Button unlockButton = new Button(getTranslation("dash.getpremium"));
            unlockButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            unlockButton.setWidthFull();
            unlockButton.addClickListener(e -> UI.getCurrent().navigate(PremiumView.class));
            premiumUnlockedLayout.add(unlockButton);
        }
    }

    private Component generateMainWithCategory(H2 pageTitle, DashboardInitData.Category category, boolean createNew) {
        Guild guild = guildComboBox.getValue();
        DiscordUser discordUser = getSessionData().getDiscordUser().get();

        pageTitle.setText(category.getTitle());
        DashboardCategoryInitData data = sendDashboardCategoryInit(
                category.getId(),
                guild.getId(),
                discordUser.getId(),
                getLocale(),
                createNew
        );

        if (data != null) {
            updatePremiumUnlocked(data.isPremiumUnlocked());
            if (data.getMissingUserPermissions().isEmpty() && data.getMissingBotPermissions().isEmpty()) {
                Component component = DashboardComponentConverter.convert(guild.getId(), discordUser.getId(), data.getComponents(), confirmationDialog);
                ((HasSize) component).setWidthFull();
                data.getComponents().setActionSendListener((json, confirmationMessage) -> {
                    if (confirmationMessage != null) {
                        Span confirmationMessageSpan = new Span(confirmationMessage);
                        confirmationMessageSpan.getStyle().set("color", "var(--lumo-error-text-color)");
                        confirmationDialog.open(confirmationMessageSpan, () -> sendAction(category, json), () -> updateMainContentCategory(category, false));
                    } else {
                        sendAction(category, json);
                    }
                });
                return component;
            } else {
                return generateMissingPermissions(data.getMissingUserPermissions(), data.getMissingBotPermissions());
            }
        } else {
            confirmationDialog.open(getTranslation("error"), () -> updateMainContentBack(false));
            return new Div();
        }
    }

    private void sendAction(DashboardInitData.Category category, JSONObject json) {
        try {
            ActionResult actionResult = sendDashboardAction(
                    guildComboBox.getValue().getId(),
                    getSessionData().getDiscordUser().get().getId(),
                    json
            );
            if (actionResult.getSuccessMessage() != null) {
                CustomNotification.showSuccess(actionResult.getSuccessMessage());
            }
            if (actionResult.getErrorMessage() != null) {
                confirmationDialog.open(actionResult.getErrorMessage(), () -> {
                });
            }
            if (actionResult.getRedraw()) {
                updateMainContentCategory(category, false);
            }
            if (actionResult.getScrollToTop()) {
                UI.getCurrent().getPage().executeJs("window.scrollTo(0, 0);");
            }
        } catch (Throwable e) {
            confirmationDialog.open(getTranslation("error"), () -> UI.getCurrent().getPage().reload());
        }
    }

    private void updateMainContentBack(boolean resetGuild) {
        updateMainContentEntry(guildComboBox.getValue());
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
                uri.append("?c=").append(categoryList.get(categoryTabs.getSelectedIndex()).getId());
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
                        List<String> categoryIdList = parametersMap.getOrDefault("c", Collections.emptyList());
                        autoCategoryId = categoryIdList.size() > 0 ? categoryIdList.get(0) : null;
                        guildComboBox.setValue(guild);
                    });
        }
    }

    private DashboardInitData sendDashboardInit(long guildId, long userId, Locale locale) {
        JSONObject json = new JSONObject();
        json.put("user_id", userId);
        json.put("locale", locale);

        try {
            return SendEvent.sendToGuild(EventOut.DASH_INIT, json, guildId)
                    .thenApply(r -> {
                        if (r.getBoolean("ok")) {
                            ArrayList<DashboardInitData.Category> categories = new ArrayList<>();
                            JSONArray titlesJson = r.getJSONArray("titles");
                            for (int i = 0; i < titlesJson.length(); i++) {
                                JSONObject data = titlesJson.getJSONObject(i);
                                DashboardInitData.Category category = new DashboardInitData.Category(
                                        data.getString("id"),
                                        data.getString("title")
                                );
                                categories.add(category);
                            }
                            return new DashboardInitData(categories, r.getBoolean("premium"));
                        } else {
                            return null;
                        }
                    }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private DashboardCategoryInitData sendDashboardCategoryInit(String categoryId, long guildId, long userId, Locale locale, boolean createNew) {
        JSONObject json = new JSONObject();
        json.put("category", categoryId);
        json.put("user_id", userId);
        json.put("locale", locale);
        json.put("create_new", createNew);

        try {
            return SendEvent.sendToGuild(EventOut.DASH_CAT_INIT, json, guildId)
                    .thenApply(r -> {
                        if (r.getBoolean("ok")) {
                            ArrayList<String> missingBotPermissions = new ArrayList<>();
                            JSONArray missingBotPermissionsJson = r.getJSONArray("missing_bot_permissions");
                            for (int i = 0; i < missingBotPermissionsJson.length(); i++) {
                                missingBotPermissions.add(missingBotPermissionsJson.getString(i));
                            }

                            ArrayList<String> missingUserPermissions = new ArrayList<>();
                            JSONArray missingUserPermissionsJson = r.getJSONArray("missing_user_permissions");
                            for (int i = 0; i < missingUserPermissionsJson.length(); i++) {
                                missingUserPermissions.add(missingUserPermissionsJson.getString(i));
                            }

                            DashboardContainer components = null;
                            if (missingBotPermissions.isEmpty() && missingUserPermissions.isEmpty()) {
                                components = (DashboardContainer) DashboardComponent.generate(r.getJSONObject("components"));
                            }

                            return new DashboardCategoryInitData(missingBotPermissions, missingUserPermissions, components, r.getBoolean("premium"));
                        } else {
                            return null;
                        }
                    }).get();
        } catch (ExecutionException | InterruptedException e) {
            //Ignore
            return null;
        }
    }

    private ActionResult sendDashboardAction(long guildId, long userId, JSONObject actionJson) throws ExecutionException, InterruptedException {
        JSONObject json = new JSONObject();
        json.put("user_id", userId);
        json.put("action", actionJson);

        return SendEvent.sendToGuild(EventOut.DASH_ACTION, json, guildId)
                .thenApply(r -> {
                    if (r.getBoolean("ok")) {
                        ActionResult actionResult = new ActionResult();
                        if (r.getBoolean("redraw")) {
                            if (r.has("scroll_to_top") && r.getBoolean("scroll_to_top")) {
                                actionResult = actionResult.withRedrawScrollToTop();
                            } else {
                                actionResult = actionResult.withRedraw();
                            }
                        }
                        if (r.has("success_message")) {
                            actionResult = actionResult.withSuccessMessage(r.getString("success_message"));
                        }
                        if (r.has("error_message")) {
                            actionResult = actionResult.withErrorMessage(r.getString("error_message"));
                        }
                        return actionResult;
                    } else {
                        throw new RuntimeException();
                    }
                }).get();
    }

}
