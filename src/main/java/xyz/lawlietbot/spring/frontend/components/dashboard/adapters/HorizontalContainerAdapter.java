package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import dashboard.DashboardComponent;
import dashboard.container.HorizontalContainer;
import dashboard.container.HorizontalPusher;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardComponentConverter;

public class HorizontalContainerAdapter extends FlexLayout {

    public HorizontalContainerAdapter(long guildId, long userId, HorizontalContainer horizontalContainer) {
        addClassName("dashboard-horizontal");
        if (horizontalContainer.isCard()) {
            addClassName("dashboard-card");
        }
        if (horizontalContainer.getAllowWrap()) {
            addClassName("dashboard-horizontal-wrap");
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
            Component component = DashboardComponentConverter.convert(guildId, userId, dashboardComponent);
            if (component != null) {
                add(component);
                if (dashboardComponent instanceof HorizontalPusher || noPusher) {
                    setFlexGrow(1, component);
                }
                if (component instanceof HasStyle && !(dashboardComponent instanceof HorizontalPusher)) {
                    if (horizontalContainer.getAllowWrap()) {
                        ((HasStyle) component).addClassName("dashboard-horizontal-child-wrap");
                    } else {
                        ((HasStyle) component).addClassName("dashboard-horizontal-child");
                    }
                }
            }
        }
    }

}
