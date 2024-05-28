package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import dashboard.DashboardComponent;
import dashboard.component.DashboardText;
import dashboard.container.HorizontalContainer;
import dashboard.container.HorizontalPusher;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardAdapter;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardComponentConverter;

import java.util.Objects;

public class HorizontalContainerAdapter extends FlexLayout implements DashboardAdapter<HorizontalContainer> {

    private HorizontalContainer horizontalContainer;
    private final long guildId;
    private final long userId;
    private final ConfirmationDialog dialog;

    public HorizontalContainerAdapter(HorizontalContainer horizontalContainer, long guildId, long userId, ConfirmationDialog dialog) {
        this.horizontalContainer = horizontalContainer;
        this.guildId = guildId;
        this.userId = userId;
        this.dialog = dialog;

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
            Component component = DashboardComponentConverter.convert(guildId, userId, dashboardComponent, dialog);
            if (component == null) {
                continue;
            }

            add(component);
            if ((dashboardComponent instanceof HorizontalPusher || noPusher) &&
                    !(dashboardComponent instanceof DashboardText) &&
                    dashboardComponent.canExpand()
            ) {
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

    @Override
    public void update(HorizontalContainer horizontalContainer) {
        this.horizontalContainer = horizontalContainer;
        DashboardComponentConverter.addAndRemove(this, horizontalContainer, guildId, userId, dialog);

        boolean noPusher = horizontalContainer.getChildren().stream().noneMatch(c -> c instanceof HorizontalPusher);
        for (int i = 0; i < getComponentCount(); i++) {
            Component component = getComponentAt(i);
            DashboardComponent dashboardComponent = horizontalContainer.getChildren().get(i);
            ((DashboardAdapter) component).update(dashboardComponent);

            if ((dashboardComponent instanceof HorizontalPusher || noPusher) &&
                    !(dashboardComponent instanceof DashboardText) &&
                    dashboardComponent.canExpand()
            ) {
                setFlexGrow(1, component);
            } else {
                setFlexGrow(0, component);
            }
            if (component instanceof HasStyle && !(dashboardComponent instanceof HorizontalPusher)) {
                ((HasStyle) component).removeClassNames("dashboard-horizontal-child-wrap", "dashboard-horizontal-child");
                if (horizontalContainer.getAllowWrap()) {
                    ((HasStyle) component).addClassName("dashboard-horizontal-child-wrap");
                } else {
                    ((HasStyle) component).addClassName("dashboard-horizontal-child");
                }
            }
        }
    }

    @Override
    public boolean equalsType(DashboardComponent dashboardComponent) {
        if (!(dashboardComponent instanceof HorizontalContainer)) {
            return false;
        }

        HorizontalContainer horizontalContainer = (HorizontalContainer) dashboardComponent;
        return Objects.equals(this.horizontalContainer.isCard(), horizontalContainer.isCard()) &&
                Objects.equals(this.horizontalContainer.getAlignment(), horizontalContainer.getAlignment()) &&
                Objects.equals(this.horizontalContainer.getAllowWrap(), horizontalContainer.getAllowWrap());
    }

}
