package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.html.H3;
import dashboard.DashboardComponent;
import dashboard.component.DashboardTitle;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardAdapter;

public class DashboardTitleAdapter extends H3 implements DashboardAdapter<DashboardTitle> {

    public DashboardTitleAdapter(DashboardTitle dashboardTitle) {
        setClassName("dashboard-header");
        update(dashboardTitle);
    }

    @Override
    public void update(DashboardTitle dashboardTitle) {
        if (getText().equals(dashboardTitle.getText())) {
            return;
        }

        setText(dashboardTitle.getText());
    }

    @Override
    public boolean equalsType(DashboardComponent dashboardComponent) {
        return dashboardComponent instanceof DashboardTitle;
    }

}
