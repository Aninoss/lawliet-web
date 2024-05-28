package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.html.Image;
import dashboard.DashboardComponent;
import dashboard.component.DashboardImage;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardAdapter;

public class DashboardImageAdapter extends Image implements DashboardAdapter<DashboardImage> {

    public DashboardImageAdapter(DashboardImage dashboardImage) {
        setAlt("");
        addClassName("dashboard-img");
        update(dashboardImage);
    }

    @Override
    public void update(DashboardImage dashboardImage) {
        if (getSrc().equals(dashboardImage.getUrl())) {
            return;
        }

        setSrc(dashboardImage.getUrl());
    }

    @Override
    public boolean equalsType(DashboardComponent dashboardComponent) {
        return dashboardComponent instanceof DashboardImage;
    }

}
