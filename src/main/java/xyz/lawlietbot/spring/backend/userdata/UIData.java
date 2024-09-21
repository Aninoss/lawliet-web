package xyz.lawlietbot.spring.backend.userdata;

import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@Component
@UIScope
public class UIData implements Serializable {

    private final boolean lite;
    private final boolean noNSFW;
    private Long userId = null;

    public UIData() {
        Map<String, String[]> parametersMap = VaadinService.getCurrentRequest().getParameterMap();
        lite = parameterMapIsTrue(parametersMap, "lite");
        noNSFW = parameterMapIsTrue(parametersMap, "nonsfw");
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
        this.userId = userId;
    }

    public void logout() {
        this.userId = null;
    }

    public Optional<Long> getUserId() {
        return Optional.ofNullable(userId);
    }

    public String getBotInviteUrl() {
        if (isLite()) return "/invite?WEBSITE_TOPGG";
        return "/invite?WEBSITE";
    }

}
