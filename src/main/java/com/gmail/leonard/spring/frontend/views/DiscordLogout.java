package com.gmail.leonard.spring.frontend.views;

import com.gmail.leonard.spring.backend.userdata.SessionData;
import com.gmail.leonard.spring.backend.userdata.UIData;
import com.gmail.leonard.spring.frontend.layouts.PageLayout;
import com.gmail.leonard.spring.NoLiteAccess;
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
        getStyle().set("margin-bottom", "48px");
        SessionData sessionData = getSessionData();
        UIData uiData = getUiData();

        if (sessionData.isLoggedIn())
            sessionData.logout(uiData);

        Class<? extends Component> resumeClass = HomeView.class;

        UI.getCurrent().navigate(resumeClass);
        UI.getCurrent().getPage().reload();
    }
}
