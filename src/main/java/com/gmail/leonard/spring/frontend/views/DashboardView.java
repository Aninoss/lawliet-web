package com.gmail.leonard.spring.frontend.views;

import com.gmail.leonard.spring.backend.userdata.UIData;
import com.gmail.leonard.spring.backend.webcomclient.modules.Dashboard;
import com.gmail.leonard.spring.frontend.components.HtmlText;
import com.gmail.leonard.spring.LoginAccess;
import com.gmail.leonard.spring.NoLiteAccess;
import com.gmail.leonard.spring.backend.userdata.DiscordServerData;
import com.gmail.leonard.spring.backend.userdata.ServerListData;
import com.gmail.leonard.spring.backend.userdata.SessionData;
import com.gmail.leonard.spring.frontend.components.dashboard.DashboardHeader;
import com.gmail.leonard.spring.frontend.components.dashboard.DashboardServerListLayout;
import com.gmail.leonard.spring.frontend.components.IconLabel;
import com.gmail.leonard.spring.frontend.components.PageHeader;
import com.gmail.leonard.spring.frontend.layouts.MainLayout;
import com.gmail.leonard.spring.frontend.layouts.PageLayout;
import com.gmail.leonard.spring.frontend.Styles;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Route(value = "dashboard", layout = MainLayout.class)
@CssImport("./styles/dashboard.css")
@NoLiteAccess
@LoginAccess
public class DashboardView extends PageLayout implements HasUrlParameter<Long> {

    public DashboardView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);
        getStyle().set("margin-bottom", "48px");

        if (!sessionData.isLoggedIn()) {
            /* Not logged in */
            VerticalLayout mainLayout = generateMainLayout();
            Div div = new Div(new Text(getTranslation("dashboard.redirect")));
            div.setWidthFull();
            mainLayout.add(div);

            add(new PageHeader(getTitleText(), null, null), mainLayout);
        }
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter Long serverId) {
        SessionData sessionData = getSessionData();
        if (!sessionData.isLoggedIn()) return;
        removeAll();

        ServerListData serverListData = Dashboard.fetchServerListData(sessionData).join();
        Optional<DiscordServerData> optionalServerListData;

        if (serverId == null || !(optionalServerListData = serverListData.find(serverId)).isPresent()) {
            /* Server Overview */
            VerticalLayout mainLayout = generateMainLayout();

            if (serverListData.size() == 0) {
                Div div = new Div(new Text(getTranslation("dashboard.noserver")));
                div.getStyle().set("color", "var(--lumo-error-text-color)");
                div.setWidthFull();
                mainLayout.add(div);
            } else {
                DashboardServerListLayout dashboardServerListLayout = new DashboardServerListLayout(serverListData);
                dashboardServerListLayout.addServerClickListener(this::setServer);
                mainLayout.add(dashboardServerListLayout);
            }

            Hr hr = new Hr();
            hr.getStyle()
                    .set("margin-top", "32px")
                    .set("margin-bottom", "-8px");

            mainLayout.add(hr);
            IconLabel iconLabel = new IconLabel(VaadinIcon.WARNING.create(), getTranslation("dashboard.admin"));
            iconLabel.setWidthFull();
            mainLayout.add(iconLabel);

            HtmlText htmlText = new HtmlText(getTranslation("dashboard.desc"));
            add(new PageHeader(getTitleText(), null, null, htmlText), mainLayout);
            return;
        }

        /* Server Dashboard */
        DiscordServerData discordServerData = optionalServerListData.get();
        VerticalLayout mainLayout = generateMainLayout();

        Div notAvailable = new Div(new Text(getTranslation("dashboard.notavailable")));
        notAvailable.getStyle().set("color", "var(--lumo-error-text-color)");
        mainLayout.add(notAvailable);

        DashboardHeader dashboardHeader = new DashboardHeader(sessionData, getTitleText(), discordServerData);
        dashboardHeader.addServerClickListener(this::setServer);

        add(dashboardHeader, mainLayout);
    }

    private void setServer(Long serverId) {
        if (serverId == null) UI.getCurrent().navigate(DashboardView.class);
        else UI.getCurrent().navigate(DashboardView.class, serverId);
    }

    private VerticalLayout generateMainLayout() {
        VerticalLayout usedContent = new VerticalLayout();
        usedContent.addClassName(Styles.APP_WIDTH);
        usedContent.setPadding(true);
        return usedContent;
    }

}
