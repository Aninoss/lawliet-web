package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import dashboard.DashboardComponent;
import dashboard.container.VerticalContainer;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardComponentConverter;

public class VerticalContainerAdapter extends VerticalLayout {

    public VerticalContainerAdapter(VerticalContainer verticalContainer) {
        if (verticalContainer.isCard()) {
            addClassName("dashboard-card");
        } else {
            setPadding(false);
        }

        for (DashboardComponent dashboardComponent : verticalContainer.getChildren()) {
            Component component = DashboardComponentConverter.convert(dashboardComponent);
            if (component != null) {
                if (component instanceof HasSize) {
                    ((HasSize) component).setWidthFull();
                }
                add(component);
            }
        }
    }

}
