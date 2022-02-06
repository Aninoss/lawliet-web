package xyz.lawlietbot.spring.backend.userdata;

import java.util.List;
import java.util.Optional;
import bell.oauth.discord.domain.Guild;
import bell.oauth.discord.main.OAuthBuilder;
import bell.oauth.discord.main.Response;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.springframework.stereotype.Component;
import xyz.lawlietbot.spring.backend.util.StringUtil;

@Component
@VaadinSessionScope
public class SessionData {

    private OAuthBuilder builder;
    private final String id;
    private DiscordUser discordUser = null;
    private String currentTarget = "";

    public SessionData() {
        VaadinSession.getCurrent().getSession().setAttribute("session", this);
        id = StringUtil.getRandomString();
        setData();
    }

    private void setData() {
        String url = "https://" + System.getenv("DOMAIN") + "/discordlogin";
        builder = new OAuthBuilder(System.getenv("BOT_CLIENT_ID"), System.getenv("BOT_CLIENT_SECRET"))
                .setRedirectURI(url);
    }

    public String getLoginUrl() {
        builder = builder.setScopes(new String[]{"identify", "guilds"});
        return builder.getAuthorizationUrl(id) + "&prompt=none";
    }

    public boolean login(String code, String state) {
        if (state.equals(id)) {
            Response response = builder.exchange(code);
            if (response != Response.ERROR) {
                List<Guild> guilds = builder.getScopes().contains("guilds") ? builder.getGuilds() : null;
                discordUser = new DiscordUser(builder.getUser(), guilds);
                return true;
            }
        }
        return false;
    }

    public void logout(UIData uiData) {
        if (isLoggedIn()) {
            uiData.logout();
            setData();
            discordUser = null;
        }
    }

    public String getId() {
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

}
