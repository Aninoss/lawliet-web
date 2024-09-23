package xyz.lawlietbot.spring.frontend.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;

@Route(value = "discordlogout")
@NoLiteAccess
public class DiscordLogout extends PageLayout implements BeforeEnterObserver {

    public DiscordLogout(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        SessionData sessionData = getSessionData();
        UIData uiData = getUiData();

        if (sessionData.isLoggedIn()) {
            sessionData.logout(uiData);
        }

        UI.getCurrent().getPage().setLocation("/");
    }
}
