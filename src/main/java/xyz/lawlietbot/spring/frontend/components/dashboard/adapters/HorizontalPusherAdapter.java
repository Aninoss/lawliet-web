package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.html.Div;
import dashboard.DashboardComponent;
import dashboard.container.HorizontalPusher;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardAdapter;

public class HorizontalPusherAdapter extends Div implements DashboardAdapter<HorizontalPusher> {

    @Override
    public void update(HorizontalPusher horizontalPusher) {}

    @Override
    public boolean equalsType(DashboardComponent dashboardComponent) {
        return dashboardComponent instanceof HorizontalPusher;
    }

}
