package xyz.lawlietbot.spring.frontend.views;

import java.io.IOException;
import java.util.List;
import com.stripe.exception.StripeException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RoutePrefix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.LoginAccess;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.payment.Subscription;
import xyz.lawlietbot.spring.backend.payment.paddle.PaddleAPI;
import xyz.lawlietbot.spring.backend.payment.paddle.PaddleManager;
import xyz.lawlietbot.spring.backend.payment.stripe.StripeManager;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.syncserver.SendEvent;

@Route(value = "manage", layout = MainLayout.class)
@RoutePrefix("premium")
@NoLiteAccess
@LoginAccess
public class ManageSubscriptionsView extends PageLayout {

    private final static Logger LOGGER = LoggerFactory.getLogger(ManageSubscriptionsView.class);

    private final VerticalLayout mainContent = new VerticalLayout();
    private final ConfirmationDialog confirmationDialog = new ConfirmationDialog();

    public ManageSubscriptionsView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);
        getStyle().set("margin-bottom", "48px");
        add(new PageHeader(getUiData(), getTitleText(), getTranslation("manage.desc"), PremiumView.class));

        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.getStyle()
                .set("margin-bottom", "-4px");
        mainContent.setPadding(true);
        updateMainContent();
        add(mainContent, confirmationDialog);
    }

    private void updateMainContent() {
        getSessionData().getDiscordUser().ifPresent(user -> {
            try {
                String sessionUrl = StripeManager.generateCustomerPortalSession(user.getId());
                updateMainContent(user, sessionUrl, false);
            } catch (StripeException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void updateMainContent(DiscordUser user, String sessionUrl, boolean clearSubCache) {
        mainContent.removeAll();
        Component grid = generateGrid(user, sessionUrl, clearSubCache);
        if (grid != null) {
            mainContent.add(grid);
        }
        if (sessionUrl != null) {
            mainContent.add(generateStripeButton(sessionUrl));
        }
    }

    private Component generateGrid(DiscordUser user, String sessionUrl, boolean clearSubCache) {
        List<Subscription> subscriptionList = SendEvent.sendListPaddleSubscriptions(user.getId(), clearSubCache).join();

        if (subscriptionList.size() > 0) {
            Grid<Subscription> grid = new Grid<>(Subscription.class, false);
            grid.setHeightByRows(true);
            grid.setItems(subscriptionList);
            grid.setSelectionMode(Grid.SelectionMode.NONE);

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
            grid.addComponentColumn(sub -> generateActionComponent(sub.getSubId(), sub.isActive(), user, sessionUrl))
                    .setAutoWidth(true);
            return grid;
        } else if (sessionUrl == null) {
            Div textDiv = new Div(new Text(getTranslation("manage.nosubs")));
            textDiv.setWidthFull();
            textDiv.getStyle().set("text-align", "center");
            return textDiv;
        } else {
            return null;
        }
    }

    private Component generateActionComponent(int subId, boolean active, DiscordUser user, String sessionUrl) {
        List<String> actionList = List.of(
                active ? "pause" : "resume",
                "cancel"
        );

        Select<String> actionSelect = new Select<>();
        actionSelect.setPlaceholder(getTranslation("manage.grid.action"));
        actionSelect.setWidthFull();
        actionSelect.setItems(actionList);
        actionSelect.setTextRenderer(action -> getTranslation("manage.grid.action." + action));
        actionSelect.addValueChangeListener(e -> {
            String action = e.getValue();
            if (action != null) {
                confirmationDialog.open(getTranslation("manage.grid.action.dialog." + action), () -> {
                    boolean success = false;
                    try {
                        switch (action) {
                            case "pause":
                                success = PaddleAPI.subscriptionSetPaused(subId, true);
                                break;

                            case "resume":
                                success = PaddleAPI.subscriptionSetPaused(subId, false);
                                break;

                            case "cancel":
                                success = PaddleAPI.subscriptionCancel(subId);
                                break;

                            default:
                        }
                    } catch (IOException ioException) {
                        LOGGER.error("Exception on sub update", ioException);
                    }
                    if (success) {
                        updateMainContent(user, sessionUrl, true);
                    } else {
                        CustomNotification.showError(getTranslation("error"));
                    }
                }, () -> actionSelect.setValue(null));
            }
        });

        return actionSelect;
    }

    private Component generateStripeButton(String sessionUrl) {
        Button button = new Button(getTranslation("manage.more"));
        button.setWidthFull();

        Anchor anchor = new Anchor(sessionUrl, button);
        anchor.setWidthFull();
        anchor.setTarget("_blank");
        return anchor;
    }

}
