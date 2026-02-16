package xyz.lawlietbot.spring.backend.userdata;

import bell.oauth.discord.domain.Guild;
import bell.oauth.discord.main.OAuthBuilder;
import bell.oauth.discord.main.Response;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Component
@VaadinSessionScope
public class SessionData implements Serializable {

    private final static Logger LOGGER = LoggerFactory.getLogger(SessionData.class);

    private OAuthBuilder builder;
    private final UUID id;
    private DiscordUser discordUser = null;
    private String currentTarget = "";
    private ArrayList<String> errorMessages = new ArrayList<>();

    public SessionData() {
        VaadinSession.getCurrent().getSession().setAttribute("session", this);
        id = UUID.randomUUID();
        setData();
    }

    private void setData() {
        builder = new OAuthBuilder(System.getenv("BOT_CLIENT_ID"), System.getenv("BOT_CLIENT_SECRET"))
                .setRedirectURI(System.getenv("URL") + "discordlogin");
    }

    public String getLoginUrl() {
        builder = builder.setScopes(new String[]{"identify", "guilds"});
        return builder.getAuthorizationUrl(id.toString()) + "&prompt=none";
    }

    public boolean login(String code, String state) {
        if (!state.equals(id.toString())) {
            pushErrorMessage("login.error");
            return false;
        }

        Response response = builder.exchange(code);
        if (response != Response.OK) {
            pushErrorMessage("login.error");
            return false;
        }

        try {
            if (Boolean.parseBoolean(System.getenv("CHECK_LOGIN")) &&
                    SendEvent.sendToAnyCluster(EventOut.USER_CHECK_BANNED, Map.of("user_id", builder.getUser().getId())).get().getBoolean("banned")
            ) {
                pushErrorMessage("login.banned");
                return false;
            }

            List<Guild> guilds = builder.getScopes().contains("guilds") ? builder.getGuilds() : null;
            discordUser = new DiscordUser(builder.getUser(), guilds);
            return true;
        } catch (InterruptedException | ExecutionException | JSONException e) {
            LOGGER.error("Login error", e);
            pushErrorMessage("login.error");
            return false;
        }
    }

    public void logout(UIData uiData) {
        if (isLoggedIn()) {
            uiData.logout();
            setData();
            discordUser = null;
        }
    }

    public UUID getId() {
        return id;
    }

    public Optional<DiscordUser> getDiscordUser() {
        return Optional.ofNullable(discordUser);
    }

    public boolean isLoggedIn() {
        return discordUser != null;
    }

    public void pushUri(String uri) {
        UI.getCurrent().getPage().getHistory().pushState(null, uri);
        currentTarget = uri;
    }

    public void setCurrentTarget(Location location) {
        String path = location.getPath();
        if (location.getQueryParameters().getParameters().size() > 0) {
            path += "?" + location.getQueryParameters().getQueryString();
        }
        currentTarget = path;
    }

    public String getCurrentTarget() {
        return currentTarget;
    }

    public void pushErrorMessage(String messageKey) {
        errorMessages.add(messageKey);
    }

    public List<String> flushErrorMessages() {
        ArrayList<String> oldErrorMessages = errorMessages;
        errorMessages = new ArrayList<>();
        return Collections.unmodifiableList(oldErrorMessages);
    }

}
