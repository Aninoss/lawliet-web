package com.gmail.leonard.spring.Frontend.Components.Dashboard;

import com.gmail.leonard.spring.Backend.UserData.DiscordServerData;
import com.gmail.leonard.spring.Frontend.Views.DashboardView;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class DashboardTitle extends HorizontalLayout {

    public DashboardTitle(DashboardView dashboardView) {
        this(dashboardView, null);
    }

    public DashboardTitle(DashboardView dashboardView, DiscordServerData discordServerData) {
        setAlignItems(Alignment.CENTER);
        setWidthFull();
        setPadding(false);
        getStyle().set("overflow", "visible")
            .set("margin-top", "0");

        H1 title = new H1(dashboardView.getTitleText());
        add(title);

        if (discordServerData != null) {
            getStyle().set("margin-bottom", "-8px");
            title.setId("dashboard-title");
            title.addClassName("dashboard-small-title");
            title.addClickListener(event -> dashboardView.setServer(0));

            H1 arrow = new H1("»");
            arrow.getStyle()
                    .set("margin-left", "8px")
                    .set("margin-right", "-8px");
            arrow.addClassName("dashboard-small-title");
            H1 name = new H1(discordServerData.getName());
            name.addClassName("dashboard-small-title");
            name.setWidthFull();
            add(arrow, name);
        }
    }

}
