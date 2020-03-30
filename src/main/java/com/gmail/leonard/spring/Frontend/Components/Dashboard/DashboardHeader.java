package com.gmail.leonard.spring.Frontend.Components.Dashboard;

import com.gmail.leonard.spring.Backend.StringTools;
import com.gmail.leonard.spring.Backend.UserData.DiscordServerData;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.WebComClient;
import com.gmail.leonard.spring.Frontend.Components.IconLabel;
import com.gmail.leonard.spring.Frontend.Components.PageHeader;
import com.gmail.leonard.spring.Frontend.Styles;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.json.JSONObject;

public class DashboardHeader extends PageHeader {

    private DashboardTitle dashboardTitle;

    public DashboardHeader(SessionData sessionData, String dashboardName, DiscordServerData discordServerData) {
        super();
        removeOnlyPC();

        dashboardTitle = new DashboardTitle(dashboardName, discordServerData.getName());

        JSONObject data = WebComClient.getInstance().getServerMembersCount(sessionData, discordServerData.getId()).join();
        IconLabel iconLabel = new IconLabel(VaadinIcon.USER.create(), StringTools.numToString(data.getLong("members_online")) + " / " + StringTools.numToString(data.getLong("members_total")));
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
        getMainLayout().add(mainLayout);
    }

    public void addServerClickListener(DashboardServerClickListener listener) { dashboardTitle.addServerClickListener(listener); }

    public void removeServerClickListener(DashboardServerClickListener listener) { dashboardTitle.removeServerClickListener(listener); }

}