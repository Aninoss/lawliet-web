package com.gmail.leonard.spring.Frontend.Components.Dashboard;

import com.gmail.leonard.spring.Backend.UserData.DiscordServerData;
import com.gmail.leonard.spring.Backend.UserData.ServerListData;
import com.gmail.leonard.spring.Frontend.Views.DashboardServerView;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;

public class DashboardTitle extends HorizontalLayout {

    public DashboardTitle(DashboardServerView discordServerView, ServerListData serverListData) {
        setAlignItems(Alignment.CENTER);
        setWidthFull();
        getStyle().set("overflow", "visible");

        H2 title = new H2(getTranslation("category." + DashboardServerView.ID));
        title.addClickListener(listener -> {
            if (discordServerView.isServerSelected()) discordServerView.debuild(serverListData);
        });
        title.setId("dashboard-title");
        add(title);
    }

    public void setServer(DiscordServerData discordServerData) {
        getChildren().skip(1).forEach(this::remove);
        if (discordServerData != null) {
            add(new H2(">"), new H2(discordServerData.getName()));
            getChildren().filter(c -> c instanceof HtmlComponent).forEach(c -> ((HtmlComponent) c).addClassName("dashboard-small-title"));
        } else {
            getChildren().filter(c -> c instanceof HtmlComponent).forEach(c -> ((HtmlComponent) c).removeClassName("dashboard-small-title"));
        }
    }
}
