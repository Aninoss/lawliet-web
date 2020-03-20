package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Backend.Language.PageTitleGen;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Layouts.PageLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "discordlogout")
public class DiscordLogout extends PageLayout implements BeforeEnterObserver {

    private SessionData sessionData;
    private UIData uiData;

    public DiscordLogout(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        this.sessionData = sessionData;
        this.uiData = uiData;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (sessionData.isLoggedIn())
            sessionData.logout(uiData);

        Class<? extends Component> resumeClass = HomeView.class;

        UI.getCurrent().navigate(resumeClass);
        UI.getCurrent().getPage().reload();
    }
}
