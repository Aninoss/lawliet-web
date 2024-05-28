package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.html.Hr;
import dashboard.DashboardComponent;
import dashboard.component.DashboardSeparator;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardAdapter;

public class DashboardSeparatorAdapter extends Hr implements DashboardAdapter<DashboardSeparator> {

    private DashboardSeparator dashboardSeparator;

    public DashboardSeparatorAdapter(DashboardSeparator dashboardSeparator) {
        update(dashboardSeparator);
    }

    @Override
    public void update(DashboardSeparator dashboardSeparator) {
        DashboardComponent previousDashboardComponent = this.dashboardSeparator;
        this.dashboardSeparator = dashboardSeparator;
        if (dashboardComponentsAreEqual(previousDashboardComponent, dashboardSeparator)) {
            return;
        }

        getStyle().remove("margin");
        if (dashboardSeparator.getLargeGap()) {
            getStyle().set("margin", "2em 0 0.75em 0");
        }
    }

    @Override
    public boolean equalsType(DashboardComponent dashboardComponent) {
        return dashboardComponent instanceof DashboardSeparator;
    }

}
