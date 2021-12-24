package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import dashboard.DashboardComponent;
import dashboard.container.VerticalContainer;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardComponentConverter;

public class VerticalContainerAdapter extends FlexLayout {

    public VerticalContainerAdapter(VerticalContainer verticalContainer, long guildId, long userId, ConfirmationDialog dialog) {
        addClassName("dashboard-vertical");
        if (verticalContainer.isCard()) {
            addClassName("dashboard-card");
        }

        for (DashboardComponent dashboardComponent : verticalContainer.getChildren()) {
            Component component = DashboardComponentConverter.convert(guildId, userId, dashboardComponent, dialog);
            if (component != null) {
                if (component instanceof HasSize) {
                    ((HasSize) component).setWidthFull();
                }
                if (component instanceof HasStyle) {
                    ((HasStyle) component).addClassName("dashboard-vertical-child");
                }
                add(component);
            }
        }
    }

}
