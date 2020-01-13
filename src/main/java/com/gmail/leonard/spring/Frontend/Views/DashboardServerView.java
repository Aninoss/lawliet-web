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
import com.vaadin.flow.component.page.History;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.*;
import elemental.json.JsonValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaddon.css.query.MediaQueryUnit;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Route(value = DashboardServerView.ID, layout = MainLayout.class)
public class DashboardServerView extends Main implements HasDynamicTitle, BeforeEnterObserver, HasUrlParameter<Long> {

    public static final String ID = "dashboard";

    private SessionData sessionData;
    private VerticalLayout mainContent = new VerticalLayout();

    public DashboardServerView(@Autowired SessionData sessionData) {
        this.sessionData = sessionData;

        setWidthFull();
        mainContent.addClassName("app-width");
        mainContent.setPadding(true);

        if (!sessionData.isLoggedIn()) {
            mainContent.add(new DashboardTitle());
            mainContent.add(getTranslation("dashboard.redirect"));
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

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter Long serverId) {
        if (!sessionData.isLoggedIn()) return;
        mainContent.getChildren().forEach(mainContent::remove);

        WebComClient.getInstance().updateServers(sessionData);
        ServerListData serverListData = sessionData.getServerListData();
        if (serverListData.size() == 1) serverId = serverListData.getServers().get(0).getId();
        Optional<DiscordServerData> optionalServerListData;

        if (serverId == null || !(optionalServerListData = serverListData.find(serverId)).isPresent()) {
            mainContent.add(new DashboardTitle());

            if (serverListData.size() == 0) {
                Paragraph p = new Paragraph(getTranslation("dashboard.noserver"));
                p.getStyle().set("color", "var(--lumo-error-text-color)");
                mainContent.add(p);
            } else {
                mainContent.add(new DashboardServerListLayout(this, serverListData));
            }
            return;
        }

        DiscordServerData discordServerData = optionalServerListData.get();

        DashboardTitle dashboardTitle = new DashboardTitle(this, discordServerData);
        Paragraph p = new Paragraph(getTranslation("dashboard.notavailable"));
        p.getStyle().set("color", "var(--lumo-error-text-color)");

        mainContent.add(dashboardTitle, p);
    }

    public void setServer(long serverId) {
        if (serverId == 0) UI.getCurrent().navigate(DashboardServerView.class);
        else UI.getCurrent().navigate(DashboardServerView.class, serverId);
    }

}
