package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Backend.Language.PageTitleFactory;
import com.gmail.leonard.spring.Backend.Redirector;
import com.gmail.leonard.spring.Backend.UserData.ServerListData;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.WebComClient;
import com.gmail.leonard.spring.Frontend.Layouts.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = DashboardServerView.ID, layout = MainLayout.class)
public class DashboardServerView extends Main implements HasDynamicTitle, BeforeEnterObserver {

    public static final String ID = "dashboard";
    private SessionData sessionData;

    public DashboardServerView(@Autowired SessionData sessionData) {
        this.sessionData = sessionData;

        setWidthFull();
        VerticalLayout mainContent = new VerticalLayout();

        mainContent.addClassName("app-width");
        mainContent.setPadding(true);

        if (!sessionData.isLoggedIn()) {
            getStyle().set("position", "relative");
            Label redirectLabel = new Label (getTranslation("dashboard.redirect"));
            redirectLabel.addClassName("center-fixed");
            redirectLabel.getStyle().set("margin-top", "12px");
            add(redirectLabel);
            return;
        }

        WebComClient.getInstance().updateServers(sessionData);
        ServerListData serverListData = sessionData.getServerListData();

        H2 title = new H2(getTranslation("category." + ID));
        mainContent.add(title);

        if (serverListData.size() == 1) {
            mainContent.add(getTranslation("dashboard.noserver"));
        }

        add(mainContent);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!sessionData.isLoggedIn())
            new Redirector().redirect(sessionData.getLoginUrl());
    }

    @Override
    public String getPageTitle() {
        return PageTitleFactory.getPageTitle(ID);
    }

}
