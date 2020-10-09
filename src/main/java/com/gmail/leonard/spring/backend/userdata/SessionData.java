package com.gmail.leonard.spring.backend.userdata;

import bell.oauth.discord.main.OAuthBuilder;
import bell.oauth.discord.main.Response;
import com.gmail.leonard.spring.backend.SecretManager;
import com.gmail.leonard.spring.backend.StringUtil;
import com.gmail.leonard.spring.frontend.layouts.PageLayout;
import com.gmail.leonard.spring.frontend.views.DiscordLogin;
import com.gmail.leonard.spring.frontend.views.HomeView;
import com.vaadin.flow.server.*;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@Component
@VaadinSessionScope
public class SessionData {

    private final static Logger LOGGER = LoggerFactory.getLogger(SessionData.class);

    private OAuthBuilder builder;
    private final String id;
    private DiscordUser discordUser = null;
    private Class<? extends PageLayout> currentTarget = HomeView.class;

    public static HashMap<Long, ArrayList<SessionData>> userCache = new HashMap<>();

    public SessionData() {
        id = StringUtil.getRandomString();
        setData();
    }

    private void setData() {
        try {
            builder = new OAuthBuilder(SecretManager.getString("bot.clientid"), SecretManager.getString("bot.clientsecret"))
                    .setScopes(new String[]{"identify"})
                    .setRedirectURI(getCurrentDomain() + PageLayout.getRouteStatic(DiscordLogin.class));
        } catch (IOException e) {
            LOGGER.error("Error while trying to log in user", e);
        }
    }

    private String getCurrentDomain() {
        VaadinRequest vaadinRequest = VaadinService.getCurrentRequest();
        HttpServletRequest httpServletRequest = ((VaadinServletRequest)vaadinRequest).getHttpServletRequest();
        String requestUrl = httpServletRequest.getRequestURL().toString().replace("//", "|");
        return requestUrl.substring(0, requestUrl.indexOf('/')).replace("|", "//") + "/";
    }

    public String getLoginUrl() {
        return builder.getAuthorizationUrl(id) + "&prompt=none";
    }

    public boolean login(String code, String state, UIData uiData) {
        if (state.equals(id)) {
            Response response = builder.exchange(code);
            if (response != Response.ERROR) {
                discordUser = new DiscordUser(builder.getUser());
                userCache.computeIfAbsent(discordUser.getId(), id -> new ArrayList<>()).add(this);
                uiData.login(discordUser.getId());
                return true;
            }
        }
        return false;
    }

    public void logout(UIData uiData) {
        if (isLoggedIn()) {
            userCache.computeIfAbsent(discordUser.getId(), id -> new ArrayList<>()).remove(this);
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

    public static ArrayList<SessionData> getSessionData(long userId) {
        return userCache.computeIfAbsent(userId, id -> new ArrayList<>());
    }

}
