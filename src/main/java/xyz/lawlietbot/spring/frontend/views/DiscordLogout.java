package xyz.lawlietbot.spring.frontend.views;

import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.NoLiteAccess;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

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

        if (sessionData.isLoggedIn())
            sessionData.logout(uiData);

        Class<? extends Component> resumeClass = HomeView.class;

        UI.getCurrent().navigate(resumeClass);
        UI.getCurrent().getPage().reload();
    }
}
