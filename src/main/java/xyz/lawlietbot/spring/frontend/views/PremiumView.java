package xyz.lawlietbot.spring.frontend.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.ExceptionLogger;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.payment.SubLevel;
import xyz.lawlietbot.spring.backend.payment.paddle.PaddleAPI;
import xyz.lawlietbot.spring.backend.payment.paddle.PaddleManager;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.backend.util.StringUtil;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.components.premium.*;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Route(value = "premium", layout = MainLayout.class)
@CssImport("./styles/premium.css")
@NoLiteAccess
public class PremiumView extends PageLayout implements HasUrlParameter<String> {

    private final static Logger LOGGER = LoggerFactory.getLogger(PremiumView.class);

    private final Tabs tabs;
    private final Div content = new Div();

    public PremiumView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);
        ConfirmationDialog dialog = new ConfirmationDialog();
        PremiumUnlockPage premiumUnlockPage = new PremiumUnlockPage(sessionData, dialog);

        add(new PageHeader(getUiData(), getTitleText(), getTranslation("premium.desc"), getRoute()), dialog);

        Tab tabSubscriptions = new Tab(getTranslation("premium.tab.subscriptions"));
        Tab tabProducts = new Tab(getTranslation("premium.tab.products"));
        Tab tabUnlock = new Tab(getTranslation("premium.tab.unlock"));
        tabUnlock.setEnabled(sessionData.isLoggedIn());
        Tab tabManage = new Tab(getTranslation("premium.tab.manage"));
        tabManage.setEnabled(sessionData.isLoggedIn());
        Map<Tab, PremiumPage> areaMap = Map.of(
                tabSubscriptions, new PremiumSubscriptionsPage(sessionData, dialog, this),
                tabProducts, new PremiumProductsPage(sessionData, this),
                tabUnlock, premiumUnlockPage,
                tabManage, new PremiumManagePage(sessionData, dialog, premiumUnlockPage)
        );

        tabs = new Tabs(tabSubscriptions, tabProducts, tabUnlock, tabManage);
        tabs.setWidthFull();
        tabs.addSelectedChangeListener(e -> handleTabsValueChange(areaMap.get(e.getSelectedTab()), e.getSource().getSelectedIndex()));

        content.setWidthFull();
        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setPadding(true);
        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.getStyle().set("margin-bottom", "48px")
                .set("margin-top", "-32px");
        mainContent.add(tabs, content);

        add(mainContent);
        handleTabsValueChange(areaMap.get(tabs.getSelectedTab()), tabs.getSelectedIndex());
    }

    private void handleTabsValueChange(PremiumPage premiumPage, int i) {
        content.removeAll();
        content.add(premiumPage);
        premiumPage.initialize();
        premiumPage.open();
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
                    int paddleCheckoutWaitTimeMinutes = Integer.parseInt(Objects.requireNonNullElse(System.getenv("PADDLE_CHECKOUT_WAIT_TIME_MINUTES"), "1"));
                    PaddleManager.waitForCheckoutAsync(checkoutId).get(paddleCheckoutWaitTimeMinutes, TimeUnit.MINUTES);

                    JSONObject checkout = PaddleAPI.retrieveCheckout(checkoutId);
                    long planId = checkout.getJSONObject("order").getInt("product_id");
                    SubLevel subLevel = PaddleManager.getSubLevelType(planId);

                    if (subLevel == SubLevel.PRO) {
                        tabs.setSelectedIndex(2);
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
                } catch (Throwable e) {
                    LOGGER.error("Could not load subscription", e);
                    CustomNotification.showError(getTranslation("error"));
                }
            }

            if (parametersMap.containsKey("paddle_billing") && parametersMap.containsKey("type")) {
                String transactionId = parametersMap.get("paddle_billing").get(0);
                UI.getCurrent().getPage().getHistory().replaceState(null, getRoute());
                try {
                    int paddleCheckoutWaitTimeMinutes = Integer.parseInt(Objects.requireNonNullElse(System.getenv("PADDLE_CHECKOUT_WAIT_TIME_MINUTES"), "1"));
                    PaddleManager.waitForCheckoutAsync(transactionId).get(paddleCheckoutWaitTimeMinutes, TimeUnit.MINUTES);

                    String messageKey = parametersMap.get("type").get(0).equals("txt2img")
                            ? "premium.buy.success.txt2img"
                            : "premium.buy.success.premium";
                    ConfirmationDialog confirmationDialog = new ConfirmationDialog();
                    confirmationDialog.open(getTranslation(messageKey), () -> {
                    });
                    add(confirmationDialog);
                    tabs.setSelectedIndex(1);
                } catch (Throwable e) {
                    LOGGER.error("Could not load product", e);
                    CustomNotification.showError(getTranslation("error"));
                }
            }

            if (parametersMap.containsKey("tab")) {
                String indexString = parametersMap.get("tab").get(0);
                if (StringUtil.stringIsInt(indexString)) {
                    int index = Integer.parseInt(indexString);
                    if (index >= 0 && index < 4 && (index < 2 || getSessionData().isLoggedIn())) {
                        tabs.setSelectedIndex(index);
                    }
                }
            }
        }
    }

    public Component generateCouponField() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(false);
        layout.setId("notification-field");
        layout.getStyle().set("border-color", "rgb(var(--warning-color-rgb))");

        Icon icon = VaadinIcon.INFO_CIRCLE_O.create();
        icon.setId("notification-icon");
        icon.getStyle().set("color", "rgb(var(--warning-color-rgb))");
        layout.add(icon);

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);

        Span text = new Span(getTranslation("premium.sale.subscriptions"));
        content.add(text);
        layout.add(content);
        return layout;
    }

}
