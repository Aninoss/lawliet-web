package xyz.lawlietbot.spring.backend.userdata;

import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import bell.oauth.discord.domain.Guild;
import bell.oauth.discord.main.OAuthBuilder;
import bell.oauth.discord.main.Response;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.springframework.stereotype.Component;
import xyz.lawlietbot.spring.backend.util.StringUtil;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.frontend.views.DiscordLogin;
import xyz.lawlietbot.spring.frontend.views.HomeView;

@Component
@VaadinSessionScope
public class SessionData {

    private OAuthBuilder builder;
    private final String id;
    private DiscordUser discordUser = null;
    private Class<? extends PageLayout> currentTarget = HomeView.class;

    public SessionData() {
        id = StringUtil.getRandomString();
        setData();
    }

    private void setData() {
        builder = new OAuthBuilder(System.getenv("BOT_CLIENT_ID"), System.getenv("BOT_CLIENT_SECRET"))
                .setRedirectURI(getCurrentDomain() + PageLayout.getRouteStatic(DiscordLogin.class));
    }

    private String getCurrentDomain() {
        VaadinRequest vaadinRequest = VaadinService.getCurrentRequest();
        HttpServletRequest httpServletRequest = ((VaadinServletRequest)vaadinRequest).getHttpServletRequest();
        String requestUrl = httpServletRequest.getRequestURL().toString().replace("//", "|");
        return requestUrl.substring(0, requestUrl.indexOf('/')).replace("|", "//") + "/";
    }

    public String getLoginUrl(boolean withGuilds) {
        builder = builder.setScopes(withGuilds ? new String[]{"identify", "guilds"} : new String[]{"identify"});
        return builder.getAuthorizationUrl(id) + "&prompt=none";
    }

    public boolean login(String code, String state, UIData uiData) {
        if (state.equals(id)) {
            Response response = builder.exchange(code);
            if (response != Response.ERROR) {
                List<Guild> guilds = builder.getScopes().contains("guilds") ? builder.getGuilds() : null;
                discordUser = new DiscordUser(builder.getUser(), guilds);
                uiData.login(discordUser.getId());
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

    public Optional<DiscordUser> getDiscordUser() {
        return Optional.ofNullable(discordUser);
    }

    public boolean isLoggedIn() {
        return discordUser != null;
    }

    public void setCurrentTarget(Class<? extends PageLayout> c) {
        currentTarget = c;
    }

    public Class<? extends PageLayout> getCurrentTarget() {
        return currentTarget;
    }

}
