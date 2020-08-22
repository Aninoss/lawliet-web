package com.gmail.leonard.spring.Backend.UserData;

import bell.oauth.discord.domain.User;
import bell.oauth.discord.main.OAuthBuilder;
import bell.oauth.discord.main.Response;
import com.gmail.leonard.spring.Backend.SecretManager;
import com.gmail.leonard.spring.Backend.StringUtil;
import com.gmail.leonard.spring.Frontend.Layouts.PageLayout;
import com.gmail.leonard.spring.Frontend.Views.DiscordLogin;
import com.gmail.leonard.spring.Frontend.Views.HomeView;
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

    final static Logger LOGGER = LoggerFactory.getLogger(SessionData.class);

    private OAuthBuilder builder;
    private final String id;
    private String username, avatarId, discriminator;
    private long userId;
    private boolean loggedIn;
    private Class<? extends PageLayout> currentTarget = HomeView.class;

    public static HashMap<Long, ArrayList<SessionData>> userCache = new HashMap<>();

    public SessionData() {
        id = StringUtil.getRandomString();
        setData();
    }

    private void setData() {
        userId = 0;
        username = null;
        avatarId = null;
        discriminator = null;
        loggedIn = false;

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
        return builder.getAuthorizationUrl(id);
    }

    public boolean login(String code, String state, UIData uiData) {
        if (state.equals(id)) {
            Response response = builder.exchange(code);
            if (response != Response.ERROR) {
                User user = builder.getUser();
                userId = Long.parseLong(user.getId());
                username = user.getUsername();
                avatarId = user.getAvatar();
                discriminator = user.getDiscriminator();
                loggedIn = true;
                userCache.computeIfAbsent(userId, id -> new ArrayList<>()).add(this);
                uiData.login(userId);
                return true;
            }
        }
        return false;
    }

    public void logout(UIData uiData) {
        if (isLoggedIn()) {
            userCache.computeIfAbsent(userId, id -> new ArrayList<>()).remove(this);
        }
        uiData.logout();
        setData();
    }

    public Optional<Long> getUserId() {
        if (loggedIn) {
            return Optional.of(userId);
        } return Optional.empty();
    }

    public Optional<String> getUserName() {
        if (loggedIn) {
            return Optional.of(username);
        } return Optional.empty();
    }

    public Optional<String> getDiscriminator() {
        if (loggedIn) {
            return Optional.of(discriminator);
        } return Optional.empty();
    }

    public Optional<String> getUserAvatar() {
        if (loggedIn) {
            return Optional.of("https://cdn.discordapp.com/avatars/" + userId + "/" + avatarId + ".png");
        } return Optional.empty();
    }

    public boolean isLoggedIn() {
        return loggedIn;
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
