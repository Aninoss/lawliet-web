package com.gmail.leonard.spring.Backend.UserData;

import bell.oauth.discord.domain.User;
import bell.oauth.discord.main.OAuthBuilder;
import bell.oauth.discord.main.Response;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
@UIScope
public class UIData {

    private boolean lite = false, noNSFW = false;
    private Optional<Long> userId = Optional.empty();
    private final boolean fromDiscordServersMe;

    public UIData() {
        Map<String, String[]> parametersMap = VaadinService.getCurrentRequest().getParameterMap();
        if (parameterMapIsTrue(parametersMap, "lite")) lite = true;
        if (parameterMapIsTrue(parametersMap, "nonsfw")) noNSFW = true;

        String referer = VaadinService.getCurrentRequest().getHeader("Referer");
        fromDiscordServersMe = referer != null && referer.startsWith("https://discordservers.me");
    }

    public boolean parameterMapIsTrue(Map<String, String[]> parametersMap, String key) {
        return parametersMap != null && parametersMap.containsKey(key) &&
                parametersMap.get(key).length > 0 &&
                parametersMap.get(key)[0].equals("true");
    }

    public boolean isLite() {
        return lite;
    }

    public boolean isNSFWDisabled() {
        return noNSFW;
    }

    public void login(long userId) {
        this.userId = Optional.of(userId);
    }

    public void logout() {
        this.userId = Optional.empty();
    }

    public Optional<Long> getUserId() {
        return userId;
    }

    public boolean isFromDiscordServersMe() {
        return fromDiscordServersMe;
    }

}
