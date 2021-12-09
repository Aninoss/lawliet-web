package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import dashboard.DashboardComponent;
import dashboard.container.HorizontalContainer;
import dashboard.container.HorizontalPusher;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardComponentConverter;

public class HorizontalContainerAdapter extends HorizontalLayout {

    public HorizontalContainerAdapter(HorizontalContainer horizontalContainer) {
        if (horizontalContainer.isCard()) {
            addClassName("dashboard-card");
        } else {
            setPadding(false);
        }

        switch (horizontalContainer.getAlignment()) {
            case TOP:
                setAlignItems(FlexComponent.Alignment.START);
                break;

            case CENTER:
                setAlignItems(FlexComponent.Alignment.CENTER);
                break;

            case BOTTOM:
                setAlignItems(FlexComponent.Alignment.END);
                break;

            default:
        }

        boolean noPusher = horizontalContainer.getChildren().stream().noneMatch(c -> c instanceof HorizontalPusher);
        for (DashboardComponent dashboardComponent : horizontalContainer.getChildren()) {
            Component component = DashboardComponentConverter.convert(dashboardComponent);
            if (component != null) {
                add(component);
                if (dashboardComponent instanceof HorizontalPusher || noPusher) {
                    setFlexGrow(1, component);
                }
            }
        }
    }

}
