package xyz.lawlietbot.spring.frontend.components.premium;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.QueryParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.lawlietbot.spring.backend.Redirector;
import xyz.lawlietbot.spring.backend.payment.PremiumCode;
import xyz.lawlietbot.spring.backend.payment.Subscription;
import xyz.lawlietbot.spring.backend.payment.paddle.PaddleAPI;
import xyz.lawlietbot.spring.backend.payment.paddle.PaddleManager;
import xyz.lawlietbot.spring.backend.subscriptionfeedback.SubscriptionFeedbackIdManager;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.syncserver.SyncUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PremiumManagePage extends PremiumPage {

    private final static Logger LOGGER = LoggerFactory.getLogger(PremiumManagePage.class);

    private final Div subscriptionsGridLayout = new Div();
    private final SessionData sessionData;
    private final ConfirmationDialog dialog;
    private final PremiumUnlockPage premiumUnlockPage;

    public PremiumManagePage(SessionData sessionData, ConfirmationDialog dialog, PremiumUnlockPage premiumUnlockPage) {
        this.sessionData = sessionData;
        this.dialog = dialog;
        this.premiumUnlockPage = premiumUnlockPage;
        setPadding(true);

        subscriptionsGridLayout.setWidthFull();
        add(new H2(getTranslation("manage.title.subscriptions")), subscriptionsGridLayout);
    }

    @Override
    public void build() {
        sessionData.getDiscordUser().ifPresent(user -> {
            updateSubscriptionsContent(user, 0);
            H2 h2 = new H2(getTranslation("manage.title.redeemedcodes"));
            h2.getStyle().set("margin-top", "2em");
            add(h2, generateCodesGrid(user.getId()));
        });
    }

    private void updateSubscriptionsContent(DiscordUser user, long reloadSubId) {
        subscriptionsGridLayout.removeAll();
        subscriptionsGridLayout.add(generateSubscriptionsGrid(user, reloadSubId));
    }

    private Component generateSubscriptionsGrid(DiscordUser user, long reloadSubId) {
        List<Subscription> subscriptionList = SyncUtil.retrievePaddleSubscriptions(user.getId(), reloadSubId).join();
        if (!subscriptionList.isEmpty()) {
            Grid<Subscription> grid = new Grid<>(Subscription.class, false);
            grid.setItems(subscriptionList);
            grid.setSelectionMode(Grid.SelectionMode.NONE);

            grid.addComponentColumn(sub -> generateActionComponent(sub, user))
                    .setAutoWidth(true);
            grid.addColumn(sub -> getTranslation("premium.tier." + PaddleManager.getSubLevelType(sub.getPlanId()).name()))
                    .setHeader(getTranslation("manage.grid.header.level"))
                    .setAutoWidth(true);
            grid.addColumn(sub -> getTranslation("manage.grid.status", sub.isActive()))
                    .setHeader(getTranslation("manage.grid.header.status"))
                    .setAutoWidth(true);
            grid.addColumn(Subscription::getQuantity)
                    .setHeader(getTranslation("manage.grid.header.quantity"))
                    .setAutoWidth(true);
            grid.addColumn(Subscription::getPrice)
                    .setHeader(getTranslation("manage.grid.header.price"))
                    .setAutoWidth(true);
            grid.addColumn(Subscription::getNextPayment)
                    .setHeader(getTranslation("manage.grid.header.nextpayment"))
                    .setAutoWidth(true);
            return grid;
        } else {
            return new Div(new Text(getTranslation("manage.nosubs")));
        }
    }

    private Component generateCodesGrid(long userId) {
        List<PremiumCode> premiumCodes = SyncUtil.retrieveRedeemedPremiumCodes(userId).join();

        if (!premiumCodes.isEmpty()) {
            Grid<PremiumCode> grid = new Grid<>(PremiumCode.class, false);
            grid.setItems(premiumCodes);
            grid.setSelectionMode(Grid.SelectionMode.NONE);

            grid.addColumn(PremiumCode::getCode)
                    .setHeader(getTranslation("manage.grid.header.code"))
                    .setAutoWidth(true);
            grid.addColumn(code -> getTranslation("premium.tier." + code.getLevel()))
                    .setHeader(getTranslation("manage.grid.header.level"))
                    .setAutoWidth(true);
            grid.addColumn(code -> code.getExpiration().toString().split("T")[0])
                    .setHeader(getTranslation("manage.grid.header.expires"))
                    .setAutoWidth(true);
            return grid;
        } else {
            return new Div(new Text(getTranslation("manage.nocodes")));
        }
    }

    private Component generateActionComponent(Subscription sub, DiscordUser user) {
        List<String> actionList = sub.isActive()
                ? List.of("pause", "payment_details")
                : List.of("resume", "cancel", "payment_details");

        Select<String> actionSelect = new Select<>();
        actionSelect.setPlaceholder(getTranslation("manage.grid.action"));
        actionSelect.setWidthFull();
        actionSelect.setItems(actionList);
        actionSelect.setTextRenderer(action -> getTranslation("manage.grid.action." + action));
        actionSelect.addValueChangeListener(e -> {
            String action = e.getValue();
            if (action != null) {
                if (action.equals("payment_details")) {
                    new Redirector().redirect(sub.getUpdateUrl());
                } else {
                    Span outerSpan = new Span(getTranslation("manage.grid.action.dialog." + action));
                    outerSpan.setWidthFull();
                    outerSpan.getStyle().set("color", "black");
                    if (action.equals("cancel")) {
                        Span innerSpan = new Span(" " + getTranslation("manage.grid.action.dialog.cancel.warning"));
                        innerSpan.getStyle().set("color", "var(--lumo-error-text-color)");
                        outerSpan.add(innerSpan);
                    }

                    dialog.open(outerSpan, () -> {
                        boolean success = false;
                        boolean navigateToFeedbackPage = false;
                        try {
                            switch (action) {
                                case "pause":
                                    success = PaddleAPI.subscriptionSetPaused(sub.getSubId(), true);
                                    navigateToFeedbackPage = true;
                                    break;

                                case "resume":
                                    success = PaddleAPI.subscriptionSetPaused(sub.getSubId(), false);
                                    break;

                                case "cancel":
                                    success = PaddleAPI.subscriptionCancel(sub.getSubId());
                                    break;

                                default:
                            }
                        } catch (IOException ioException) {
                            LOGGER.error("Exception on sub update", ioException);
                        }
                        if (success) {
                            updateSubscriptionsContent(user, sub.getSubId());
                            CustomNotification.showSuccess(getTranslation("manage.success"));
                            if (navigateToFeedbackPage) {
                                QueryParameters queryParameters = new QueryParameters(Map.of("id", List.of(SubscriptionFeedbackIdManager.generateId())));
                                UI.getCurrent().navigate("/subscriptionfeedback", queryParameters);
                            } else {
                                premiumUnlockPage.update();
                            }
                        } else {
                            CustomNotification.showError(getTranslation("error"));
                        }
                    }, () -> actionSelect.setValue(null));
                }
            }
        });

        return actionSelect;
    }
}
