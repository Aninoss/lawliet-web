package xyz.lawlietbot.spring.backend.userdata;

import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

@Component
@UIScope
public class UIData implements Serializable {

    private boolean lite;
    private boolean noNSFW;
    private Long userId = null;

    public void setLite(boolean lite) {
        this.lite = lite;
    }

    public boolean isLite() {
        return lite;
    }

    public void setNSFWDisabled(boolean noNSFW) {
        this.noNSFW = noNSFW;
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
