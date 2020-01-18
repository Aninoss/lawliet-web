package com.gmail.leonard.spring.Frontend.Components.Dashboard;

import com.gmail.leonard.spring.Backend.UserData.DiscordServerData;
import com.gmail.leonard.spring.Frontend.Views.DashboardServerView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class DashboardTitle extends HorizontalLayout {

    public DashboardTitle() {
        this(null, null);
    }

    public DashboardTitle(DashboardServerView dashboardServerView, DiscordServerData discordServerData) {
        setAlignItems(Alignment.CENTER);
        setWidthFull();
        getStyle().set("overflow", "visible");

        H2 title = new H2(getTranslation("category." + DashboardServerView.ID));
        title.setId("dashboard-title");

        add(title);

        if (discordServerData != null) {
            title.addClassName("dashboard-small-title");
            title.addClickListener(event -> dashboardServerView.setServer(0));

            H2 arrow = new H2("»");
            arrow.getStyle()
                    .set("margin-left", "8px")
                    .set("margin-right", "-8px");
            arrow.addClassName("dashboard-small-title");
            H2 name = new H2(discordServerData.getName());
            name.addClassName("dashboard-small-title");
            add(arrow, name);
        }
    }

}
