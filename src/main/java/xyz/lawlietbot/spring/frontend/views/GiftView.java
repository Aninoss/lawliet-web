package xyz.lawlietbot.spring.frontend.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.backend.util.StringUtil;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.components.SpanWithLinebreaks;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

import java.util.List;
import java.util.Map;

@Route(value = "gift", layout = MainLayout.class)
@CssImport("./styles/gift.css")
@NoLiteAccess
public class GiftView extends PageLayout implements HasUrlParameter<String> {

    private final VerticalLayout mainContent = new VerticalLayout();
    private final ConfirmationDialog dialog = new ConfirmationDialog();

    public GiftView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);
        getStyle().set("margin-bottom", "48px");
        PageHeader pageHeader = new PageHeader(getUiData(), getTitleText(), getTranslation("gift.desc"));
        pageHeader.getStyle().set("padding-bottom", "42px")
                .set("margin-bottom", "59px");
        add(pageHeader);

        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.getStyle()
                .set("margin-top", "-20px")
                .set("margin-bottom", "-4px");
        mainContent.setPadding(true);
        add(mainContent, dialog);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (parameter == null) {
            event.rerouteTo(PageNotFoundView.class);
            return;
        }

        JSONObject responseJson = SendEvent.send(EventOut.GET_PREMIUM_CODE, Map.of("code", parameter)).join();
        if (responseJson.has("found")) {
            String plan = responseJson.getString("plan");
            int durationDays = responseJson.getInt("durationDays");


            String contentString = getTranslation("gift.content", getTranslation("premium.tier." + plan), StringUtil.numToString(durationDays));
            SpanWithLinebreaks content = new SpanWithLinebreaks(contentString);
            mainContent.add(content);

            Span disclaimer = new Span(getTranslation("gift.disclaimer"));
            disclaimer.addClassName("gift-disclaimer");
            mainContent.add(disclaimer);

            Hr hr = new Hr();
            hr.getStyle().set("margin-top", "1.5rem");
            mainContent.add(hr);

            if (getSessionData().isLoggedIn()) {
                Button redeemButton = new Button(getTranslation("gift.redeem"));
                redeemButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                redeemButton.addClickListener(e -> {
                    Map<String, Object> params = Map.of(
                            "user_id", getSessionData().getDiscordUser().get().getId(),
                            "code", parameter,
                            "duration_days", durationDays
                    );
                    JSONObject redeemResponseJson = SendEvent.send(EventOut.REDEEM_PREMIUM_CODE, params).join();

                    if (redeemResponseJson.has("ok")) {
                        if (plan.equals("PRO")) {
                            dialog.open(getTranslation("gift.sucessful.PRO"), () -> {
                                QueryParameters queryParameters = new QueryParameters(Map.of("tab", List.of("2")));
                                UI.getCurrent().navigate(FeatureRequestsView.getRouteStatic(PremiumView.class), queryParameters);
                            }, () -> UI.getCurrent().navigate(HomeView.class));
                        } else {
                            dialog.setTriggerConfirmListenerOnClose(true);
                            dialog.open(getTranslation("gift.sucessful.BASIC"), () -> UI.getCurrent().navigate(HomeView.class));
                        }
                    } else {
                        CustomNotification.showError(getTranslation("error"));
                    }
                });
                mainContent.add(redeemButton);
            } else {
                Button loginButton = new Button(getTranslation("gift.logintoredeem"));
                loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                mainContent.add(new Anchor(getSessionData().getLoginUrl(), loginButton));
            }
        } else {
            mainContent.add(getTranslation("gift.notfound"));
        }
    }

}
