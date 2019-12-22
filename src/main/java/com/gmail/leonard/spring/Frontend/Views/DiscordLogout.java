package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Backend.Language.PageTitleFactory;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = DiscordLogout.ID)
public class DiscordLogout extends Main implements HasDynamicTitle, BeforeEnterObserver {

    public static final String ID = "discordlogout";

    private SessionData sessionData;

    public DiscordLogout(@Autowired SessionData sessionData) {
        this.sessionData = sessionData;
    }

    @Override
    public String getPageTitle() {
        return PageTitleFactory.getPageTitle(ID);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (sessionData.isLoggedIn())
            sessionData.logout();

        Class<? extends Component> resumeClass = HomeView.class;

        UI.getCurrent().navigate(resumeClass);
        UI.getCurrent().getPage().reload();
    }
}
