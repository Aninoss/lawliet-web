package xyz.lawlietbot.spring.frontend.components.dashboard;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

import java.util.ArrayList;

public class DashboardTitle extends FlexLayout {

    ArrayList<DashboardServerClickListener> serverClickListeners = new ArrayList<>();

    public DashboardTitle(String dashboardTitle, String discordServerName) {
        setWidthFull();
        getStyle().set("margin", "0")
                .set("flex-direction", "row");


        H1 title = new H1(dashboardTitle);
        title.setId("dashboard-title");
        title.addClassName("dashboard-title");
        add(title);
        title.addClickListener(click -> serverClickListeners.forEach(listener -> listener.onServerClick(null)));

        H1 arrow = new H1("»");
        arrow.getStyle()
                .set("margin-left", "8px")
                .set("margin-right", "8px");
        arrow.addClassName("dashboard-title");

        H1 name = new H1(discordServerName);
        name.addClassName("dashboard-title");
        add(arrow, name);
    }

    public void addServerClickListener(DashboardServerClickListener listener) { serverClickListeners.add(listener); }

    public void removeServerClickListener(DashboardServerClickListener listener) { serverClickListeners.remove(listener); }

}
