package com.gmail.leonard.spring.Backend.UserData;

import bell.oauth.discord.domain.User;
import bell.oauth.discord.main.OAuthBuilder;
import bell.oauth.discord.main.Response;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@UIScope
public class UIData {

    private boolean lite = false, noNSFW = false;

    public UIData() {
        Map<String, String[]> parametersMap = VaadinService.getCurrentRequest().getParameterMap();
        if (parameterMapIsTrue(parametersMap, "lite")) lite = true;
        if (parameterMapIsTrue(parametersMap, "nonsfw")) noNSFW = true;
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

}
