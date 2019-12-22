package com.gmail.leonard.spring.Backend.UserData;

import bell.oauth.discord.domain.User;
import bell.oauth.discord.main.OAuthBuilder;
import bell.oauth.discord.main.Response;
import com.gmail.leonard.spring.Backend.SecretManager;
import com.gmail.leonard.spring.Backend.StringTools;
import com.gmail.leonard.spring.Backend.WebComClient;
import com.gmail.leonard.spring.Frontend.Views.DiscordLogin;
import com.gmail.leonard.spring.Frontend.Views.HomeView;
import com.vaadin.flow.server.*;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@VaadinSessionScope
public class SessionData {

    private OAuthBuilder builder;
    private String id, username, avatarId;
    private long userId;
    private boolean loggedIn;
    private Class<?> currentTarget = HomeView.class;
    private ServerListData serverListData = new ServerListData();

    public SessionData() {
        id = StringTools.getRandomString();
        setData();
    }

    private void setData() {
        userId = 0;
        username = null;
        avatarId = null;
        loggedIn = false;

        try {
            builder = new OAuthBuilder(SecretManager.getString("bot.clientid"), SecretManager.getString("bot.clientsecret"))
                    .setScopes(new String[]{"identify"})
                    .setRedirectURI(getCurrentDomain() + DiscordLogin.ID);
        } catch (IOException e) {
            e.printStackTrace();
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

    public boolean login(String code, String state) {
        if (state.equals(id)) {
            Response response = builder.exchange(code);
            if (response != Response.ERROR) {
                loggedIn = true;
                User user = builder.getUser();
                userId = Long.parseLong(user.getId());
                username = user.getUsername();
                avatarId = user.getAvatar();
                WebComClient.getInstance().updateServers(this);
                return true;
            }
        }
        return false;
    }

    public void logout() {
        setData();
    }

    public long getUserId() {
        if (loggedIn) {
            return userId;
        } return 0;
    }

    public String getUserName() {
        if (loggedIn) {
            return username;
        } return null;
    }

    public String getUserAvatar() {
        if (loggedIn) {
            return "https://cdn.discordapp.com/avatars/" + userId + "/" + avatarId + ".png";
        } return null;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setCurrentTarget(Class<?> c) {
        currentTarget = c;
    }

    public Class<?> getCurrentTarget() {
        return currentTarget;
    }

    public ServerListData getServerListData() {
        return serverListData;
    }

}
