package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.html.Image;
import dashboard.component.DashboardImage;

public class DashboardImageAdapter extends Image {

    public DashboardImageAdapter(DashboardImage dashboardImage) {
        setSrc(dashboardImage.getUrl());
        setAlt("");
        addClassName("dashboard-img");
    }

}
