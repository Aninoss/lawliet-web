package xyz.lawlietbot.spring.frontend.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.ExceptionLogger;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.payment.SubLevel;
import xyz.lawlietbot.spring.backend.payment.paddle.PaddleAPI;
import xyz.lawlietbot.spring.backend.payment.paddle.PaddleManager;
import xyz.lawlietbot.spring.backend.premium.UserPremium;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.components.premium.PremiumManagePage;
import xyz.lawlietbot.spring.frontend.components.premium.PremiumSubscriptionsPage;
import xyz.lawlietbot.spring.frontend.components.premium.PremiumUnlockPage;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Route(value = "premium", layout = MainLayout.class)
@CssImport("./styles/premium.css")
@JavaScript("https://cdn.paddle.com/paddle/paddle.js")
@NoLiteAccess
public class PremiumView extends PageLayout implements HasUrlParameter<String> {

    private final static Logger LOGGER = LoggerFactory.getLogger(PremiumView.class);

    private final Tabs tabs;
    private final Div content = new Div();
    private boolean slotsBuild = false;

    private final PremiumUnlockPage unlockArea;

    public PremiumView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);
        ConfirmationDialog dialog = new ConfirmationDialog();
        unlockArea = new PremiumUnlockPage(sessionData, dialog);

        add(new PageHeader(getUiData(), getTitleText(), getTranslation("premium.desc"), getRoute()), dialog);

        Tab tabSubscriptions = new Tab(getTranslation("premium.tab.subscriptions"));
        Tab tabUnlock = new Tab(getTranslation("premium.tab.unlock"));
        tabUnlock.setEnabled(sessionData.isLoggedIn());
        Tab tabManage = new Tab(getTranslation("premium.tab.manage"));
        tabManage.setEnabled(sessionData.isLoggedIn());
        Map<Tab, Component> areaMap = Map.of(
                tabSubscriptions, new PremiumSubscriptionsPage(sessionData, dialog),
                tabUnlock, unlockArea,
                tabManage, new PremiumManagePage(sessionData, dialog)
        );

        tabs = new Tabs(tabSubscriptions, tabUnlock, tabManage);
        tabs.setWidthFull();
        tabs.addSelectedChangeListener(e -> handleTabsValueChange(areaMap.get(e.getSelectedTab())));

        content.setWidthFull();
        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setPadding(true);
        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.getStyle().set("margin-bottom", "48px")
                .set("margin-top", "-32px");
        mainContent.add(tabs, content);

        add(mainContent);
        handleTabsValueChange(areaMap.get(tabs.getSelectedTab()));
    }

    private void handleTabsValueChange(Component component) {
        content.removeAll();
        content.add(component);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (parameter != null) {
            PaddleManager.openPopupCustom(parameter);
        }

        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        if (parametersMap != null) {
            if (parametersMap.containsKey("paddle")) {
                String checkoutId = parametersMap.get("paddle").get(0);
                UI.getCurrent().getPage().getHistory().replaceState(null, getRoute());
                try {
                    PaddleManager.waitForCheckoutAsync(checkoutId).get(1, TimeUnit.MINUTES);

                    JSONObject checkout = PaddleAPI.retrieveCheckout(checkoutId);
                    long planId = checkout.getJSONObject("order").getInt("product_id");
                    SubLevel subLevel = PaddleManager.getSubLevelType(planId);

                    if (subLevel == SubLevel.PRO) {
                        tabs.setSelectedIndex(1);
                    }

                    String dialogText = subLevel == SubLevel.PRO ? "premium.buy.success.pro" : "premium.buy.success";
                    ConfirmationDialog confirmationDialog = new ConfirmationDialog();
                    confirmationDialog.open(getTranslation(dialogText), () -> {
                    });
                    add(confirmationDialog);

                    if (getSessionData().isLoggedIn()) {
                        Map<String, Object> map = Map.of(
                                "user_id", getSessionData().getDiscordUser().get().getId(),
                                "locale", getLocale().getLanguage()
                        );
                        SendEvent.send(EventOut.DEV_VOTES_UPDATE_REMINDER, map)
                                .exceptionally(ExceptionLogger.get());
                    }
                } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
                    LOGGER.error("Could not load subscription", e);
                    CustomNotification.showError(getTranslation("error"));
                }
            }
        }

        SessionData sessionData = getSessionData();
        if (!slotsBuild) {
            slotsBuild = true;
            if (sessionData.getDiscordUser().map(DiscordUser::hasGuilds).orElse(false)) {
                try {
                    DiscordUser discordUser = sessionData.getDiscordUser().get();
                    UserPremium userPremium = SendEvent.send(EventOut.PREMIUM, Map.of("user_id", discordUser.getId()))
                            .thenApply(jsonResponse -> {
                                ArrayList<Long> slots = new ArrayList<>();
                                JSONArray jsonSlots = jsonResponse.getJSONArray("slots");
                                for (int i = 0; i < jsonSlots.length(); i++) {
                                    slots.add(jsonSlots.getLong(i));
                                }

                                return new UserPremium(discordUser.getId(), slots);
                            })
                            .get(5, TimeUnit.SECONDS);
                    unlockArea.setUserPremium(userPremium);
                    unlockArea.setAvailableGuilds(new ArrayList<>(discordUser.getGuilds()));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    LOGGER.error("Could not load slots", e);
                    CustomNotification.showError(getTranslation("error"));
                }
            }
            unlockArea.generate();
        }
    }

}
