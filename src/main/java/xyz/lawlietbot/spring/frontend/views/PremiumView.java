package xyz.lawlietbot.spring.frontend.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.ExternalLinks;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.components.premium.PremiumConnectSection;
import xyz.lawlietbot.spring.frontend.components.premium.PremiumTiersSection;
import xyz.lawlietbot.spring.frontend.components.premium.PremiumUnlockServersSection;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

import java.util.Map;

@Route(value = "premium", layout = MainLayout.class)
@CssImport("./styles/premium.css")
@NoLiteAccess
public class PremiumView extends PageLayout {

    private final static Logger LOGGER = LoggerFactory.getLogger(PremiumView.class);

    private final ConfirmationDialog dialog = new ConfirmationDialog();

    public PremiumView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);

        PageHeader pageHeader = new PageHeader(getUiData(), getTitleText(), getTranslation("premium.desc"), getRoute(), dialog);
        if (sessionData.isLoggedIn()) {
            JSONObject responseJson = SendEvent.send(EventOut.BOUGHT_PREMIUM_CODES, Map.of("user_id", sessionData.getDiscordUser().get().getId())).join();
            try {
                JSONArray codesJson = responseJson.getJSONArray("codes");
                if (!codesJson.isEmpty()) {
                    Button revealButton = generatePremiumCodeRevealButton(codesJson);
                    revealButton.getStyle().set("margin-top", "1rem");
                    pageHeader.addComponentInnerLayer(revealButton);
                }
            } catch (Throwable e) {
                LOGGER.error("Could not retrieve Premium codes", e);
                CustomNotification.showError(getTranslation("error"));
            }
        }
        add(pageHeader);

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setPadding(false);
        mainContent.setSpacing(false);
        mainContent.getStyle().set("margin-bottom", "1em")
                .set("margin-top", "-48px");
        mainContent.add(new PremiumTiersSection(), new PremiumConnectSection(), new PremiumUnlockServersSection(sessionData, dialog));

        add(mainContent);
    }

    private Button generatePremiumCodeRevealButton(JSONArray codesJson) {
        Button revealButton = new Button(getTranslation("premium.products.revealcodes"));
        revealButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        revealButton.getStyle().set("margin-top", "0.5rem")
                .set("margin-bottom", "1rem");
        revealButton.addClickListener(e -> {
            VerticalLayout linksLayout = new VerticalLayout();
            linksLayout.setPadding(false);
            linksLayout.setSpacing(false);
            for (int i = 0; i < codesJson.length(); i++) {
                String url = ExternalLinks.LAWLIET_GIFT + codesJson.getString(i);
                Anchor a = new Anchor(url, url);
                a.setTarget("_blank");
                linksLayout.add(a);
            }
            dialog.open(linksLayout, () -> {
            });
        });
        return revealButton;
    }

}
