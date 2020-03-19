package com.gmail.leonard.spring.Frontend.Components.Dashboard;

import com.gmail.leonard.spring.Backend.Pair;
import com.gmail.leonard.spring.Backend.StringTools;
import com.gmail.leonard.spring.Backend.UserData.DiscordServerData;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.WebComClient;
import com.gmail.leonard.spring.Frontend.Components.IconLabel;
import com.gmail.leonard.spring.Frontend.Views.DashboardServerView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class DashboardTitleArea extends Div {

    public DashboardTitleArea(SessionData sessionData, DashboardServerView dashboardServerView, DiscordServerData discordServerData) {
        setWidthFull();
        setId("titlearea");

        HorizontalLayout mainContent = new HorizontalLayout();
        mainContent.setAlignItems(FlexComponent.Alignment.CENTER);
        mainContent.addClassName("app-width");
        mainContent.setPadding(true);

        if (discordServerData.getIcon().isPresent()) {
            Image image = new Image(discordServerData.getIcon().get(), "");
            image.setId("dashboard-servericon");
            image.addClassName("round");
            mainContent.add(image);
        }

        DashboardTitle dashboardTitle = new DashboardTitle(dashboardServerView, discordServerData);
        VerticalLayout serverInfoLayout = new VerticalLayout();
        serverInfoLayout.setPadding(false);
        serverInfoLayout.add(dashboardTitle);

        try {
            Optional<Pair<Long, Long>> pairOptional = WebComClient.getInstance().getServerMembersCount(sessionData, discordServerData.getId()).get();
            if (pairOptional.isPresent()) {
                Pair<Long, Long> membersCount = pairOptional.get();

                IconLabel iconLabel = new IconLabel(VaadinIcon.USER.create(), StringTools.numToString(membersCount.getKey()) + " / " + StringTools.numToString(membersCount.getValue()));
                iconLabel.getStyle()
                        .set("margin-bottom", "32px")
                        .set("margin-top", "-4px");
                serverInfoLayout.add(iconLabel);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        mainContent.add(serverInfoLayout);
        add(mainContent);
    }
}
