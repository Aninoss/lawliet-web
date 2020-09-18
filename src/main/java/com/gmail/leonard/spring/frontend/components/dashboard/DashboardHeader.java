package com.gmail.leonard.spring.frontend.components.dashboard;

import com.gmail.leonard.spring.backend.StringUtil;
import com.gmail.leonard.spring.backend.userdata.DiscordServerData;
import com.gmail.leonard.spring.backend.userdata.SessionData;
import com.gmail.leonard.spring.backend.webcomclient.modules.Dashboard;
import com.gmail.leonard.spring.frontend.components.IconLabel;
import com.gmail.leonard.spring.frontend.components.PageHeader;
import com.gmail.leonard.spring.frontend.Styles;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.json.JSONObject;

public class DashboardHeader extends PageHeader {

    private final DashboardTitle dashboardTitle;

    public DashboardHeader(SessionData sessionData, String dashboardName, DiscordServerData discordServerData) {
        super(null, null, null);
        removeOnlyPC();

        dashboardTitle = new DashboardTitle(dashboardName, discordServerData.getName());

        JSONObject data = Dashboard.fetchServerMembersCount(sessionData, discordServerData.getId()).join();
        IconLabel iconLabel = new IconLabel(VaadinIcon.USER.create(), StringUtil.numToString(data.getLong("members_online")) + " / " + StringUtil.numToString(data.getLong("members_total")));
        iconLabel.getStyle().set("margin-top", "0");
        iconLabel.setWidthFull();

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        mainLayout.setPadding(false);
        mainLayout.setId("dashboard-info");

        VerticalLayout serverDataLayout = new VerticalLayout(dashboardTitle, iconLabel);
        serverDataLayout.getStyle().set("margin-top", "10px");
        serverDataLayout.setPadding(false);

        if (discordServerData.getIcon().isPresent()) {
            Image image = new Image(discordServerData.getIcon().get(), "");
            image.setId("dashboard-servericon");
            image.addClassName(Styles.ROUND);
            mainLayout.add(image);
        }

        mainLayout.add(serverDataLayout);
        getOuterLayout().add(mainLayout);
    }

    public void addServerClickListener(DashboardServerClickListener listener) { dashboardTitle.addServerClickListener(listener); }

    public void removeServerClickListener(DashboardServerClickListener listener) { dashboardTitle.removeServerClickListener(listener); }

}