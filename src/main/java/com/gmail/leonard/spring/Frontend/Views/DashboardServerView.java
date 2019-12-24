package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Backend.Language.PageTitleFactory;
import com.gmail.leonard.spring.Backend.Redirector;
import com.gmail.leonard.spring.Backend.UserData.DiscordServerData;
import com.gmail.leonard.spring.Backend.UserData.ServerListData;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.WebComClient;
import com.gmail.leonard.spring.Frontend.Components.Dashboard.DashboardServerListLayout;
import com.gmail.leonard.spring.Frontend.Components.Dashboard.DashboardTitle;
import com.gmail.leonard.spring.Frontend.Layouts.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaddon.css.query.MediaQueryUnit;

@Route(value = DashboardServerView.ID, layout = MainLayout.class)
public class DashboardServerView extends Main implements HasDynamicTitle, BeforeEnterObserver {

    public static final String ID = "dashboard";
    private SessionData sessionData;
    private VerticalLayout mainContent = new VerticalLayout();
    private DashboardTitle dashboardTitle;
    private DiscordServerData discordServerData = null;

    public DashboardServerView(@Autowired SessionData sessionData) {
        this.sessionData = sessionData;

        setWidthFull();
        mainContent.addClassName("app-width");
        mainContent.setPadding(true);

        WebComClient.getInstance().updateServers(sessionData);
        ServerListData serverListData = sessionData.getServerListData();

        dashboardTitle = new DashboardTitle(this, serverListData);
        mainContent.add(dashboardTitle);

        if (!sessionData.isLoggedIn()) {
            mainContent.add(getTranslation("dashboard.redirect"));
        } else {
            if (serverListData.size() == 0) {
                Paragraph p = new Paragraph(getTranslation("dashboard.noserver"));
                p.getStyle().set("color", "var(--lumo-error-text-color)");
                mainContent.add(p);
            } else {
                if (serverListData.size() == 1) {
                    build(null, serverListData.getServers().get(0));
                } else {
                    debuild(serverListData);
                }
            }
        }

        add(mainContent);
    }

    public void build(DashboardServerListLayout dashboardServerListLayout, DiscordServerData discordServerData) {
        if (dashboardServerListLayout != null) mainContent.remove(dashboardServerListLayout);
        this.discordServerData = discordServerData;
        dashboardTitle.setServer(discordServerData);

        Paragraph p = new Paragraph(getTranslation("dashboard.notavailable"));
        p.getStyle().set("color", "var(--lumo-error-text-color)");
        mainContent.add(p);
    }

    public void debuild(ServerListData serverListData) {
        mainContent.getChildren().skip(1).forEach(mainContent::remove);
        discordServerData = null;
        mainContent.add(new DashboardServerListLayout(serverListData, this));
        dashboardTitle.setServer(null);
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

    public boolean isServerSelected() {
        return discordServerData != null;
    }

}
