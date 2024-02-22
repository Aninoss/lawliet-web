package xyz.lawlietbot.spring.frontend.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.LoginAccess;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.backend.util.StringUtil;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

import java.util.Map;

@Route(value = "banappeal", layout = MainLayout.class)
@NoLiteAccess
@LoginAccess
public class BanAppealView extends PageLayout implements HasUrlParameter<String> {

    private enum Response {
        OK, GUILD_NOT_FOUND, NOT_CONFIGURED, MISSING_PERMISSIONS, NOT_BANNED, APPEAL_OPEN, APPEAL_DECLINED
    }

    private final Div guildHeaderDiv = new Div();
    private final VerticalLayout mainContent;
    private final ConfirmationDialog confirmationDialog = new ConfirmationDialog();

    public BanAppealView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);
        getStyle().set("margin-bottom", "48px");
        guildHeaderDiv.setWidthFull();
        PageHeader pageHeader = new PageHeader(getUiData(), getTitleText(), getTranslation("banappeal.desc"), guildHeaderDiv);
        pageHeader.getStyle().set("padding-bottom", "42px")
                .set("margin-bottom", "59px");
        add(pageHeader);

        mainContent = new VerticalLayout();
        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.getStyle()
                .set("margin-top", "-20px")
                .set("margin-bottom", "-4px");
        mainContent.setPadding(true);
        add(confirmationDialog, mainContent);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (parameter == null) {
            event.rerouteTo(PageNotFoundView.class);
            return;
        }
        if (!StringUtil.stringIsLong(parameter)) {
            event.rerouteTo(PageNotFoundView.class);
            return;
        }

        long guildId = Long.parseLong(parameter);
        if (guildId <= 0L) {
            event.rerouteTo(PageNotFoundView.class);
            return;
        }

        update(guildId);
    }

    private void update(long guildId) {
        mainContent.removeAll();
        guildHeaderDiv.removeAll();

        if (!getSessionData().isLoggedIn()) {
            return;
        }

        DiscordUser discordUser = getSessionData().getDiscordUser().get();
        long userId = discordUser.getId();
        Map<String, Object> parameters = Map.of(
                "user_id", userId,
                "guild_id", guildId
        );
        JSONObject json = SendEvent.sendToGuild(EventOut.BAN_APPEAL_INIT, parameters, guildId).join();
        handleJson(json, userId, discordUser.getUsername(), discordUser.getUserAvatar(), guildId);
    }

    private void handleJson(JSONObject json, long userId, String username, String avatar, long guildId) {
        Response response = Response.valueOf(json.getString("response"));
        String guildName = json.has("guild_name") ? json.getString("guild_name") : null;
        String guildIcon = json.has("guild_icon") ? json.getString("guild_icon") : null;

        if (guildName != null && guildIcon != null) {
            HorizontalLayout guildLayout = new HorizontalLayout();
            guildLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            guildLayout.setWidthFull();
            guildLayout.setPadding(true);
            guildLayout.setSpacing(true);
            guildLayout.getStyle()
                    .set("border-radius", "8px")
                    .set("background", "var(--lumo-tint-5pct)")
                    .set("margin-top", "32px");

            Image image = new Image(guildIcon, "");
            image.setHeight("32px");
            image.addClassName(Styles.ROUND);
            image.getStyle().set("margin-right", "12px");
            guildLayout.add(image);

            guildLayout.add(guildName);
            guildHeaderDiv.add(guildLayout);
        }

        if (response == Response.OK) {
            TextArea message = new TextArea(getTranslation("banappeal.textfield"));
            message.setWidthFull();
            message.setMaxLength(1024);
            message.getStyle().set("margin-top", "0");
            mainContent.add(message);

            Button submit = new Button(getTranslation("banappeal.button"));
            submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            submit.addClickListener(e -> {
                Map<String, Object> parameters = Map.of(
                        "user_id", userId,
                        "username", username,
                        "avatar", avatar,
                        "guild_id", guildId,
                        "message", message.getValue()
                );
                JSONObject submitResponse = SendEvent.sendToGuild(EventOut.BAN_APPEAL, parameters, guildId).join();
                boolean ok = submitResponse.has("ok") && submitResponse.getBoolean("ok");
                String text = getTranslation(ok ? "banappeal.success" : "banappeal.error");
                update(guildId);
                confirmationDialog.open(text, () -> {});
            });
            mainContent.add(submit);
        } else {
            mainContent.add(getTranslation("banappeal.response." + response.name()));
        }
    }

}
