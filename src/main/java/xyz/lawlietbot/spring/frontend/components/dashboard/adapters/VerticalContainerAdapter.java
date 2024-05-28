package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import dashboard.DashboardComponent;
import dashboard.container.VerticalContainer;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardAdapter;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardComponentConverter;

import java.util.Objects;

public class VerticalContainerAdapter extends FlexLayout implements DashboardAdapter<VerticalContainer> {

    private VerticalContainer verticalContainer;
    private final long guildId;
    private final long userId;
    private final ConfirmationDialog dialog;

    public VerticalContainerAdapter(VerticalContainer verticalContainer, long guildId, long userId, ConfirmationDialog dialog) {
        this.verticalContainer = verticalContainer;
        this.guildId = guildId;
        this.userId = userId;
        this.dialog = dialog;

        addClassName("dashboard-vertical");
        if (verticalContainer.isCard()) {
            addClassName("dashboard-card");
        }

        for (DashboardComponent dashboardComponent : verticalContainer.getChildren()) {
            Component component = DashboardComponentConverter.convert(guildId, userId, dashboardComponent, dialog);
            if (component == null) {
                continue;
            }

            if (component instanceof HasSize) {
                ((HasSize) component).setWidthFull();
            }
            if (component instanceof HasStyle) {
                ((HasStyle) component).addClassName("dashboard-vertical-child");
            }
            add(component);
        }
    }

    @Override
    public void update(VerticalContainer verticalContainer) {
        this.verticalContainer = verticalContainer;
        DashboardComponentConverter.addAndRemove(this, verticalContainer, guildId, userId, dialog);

        for (int i = 0; i < getComponentCount(); i++) {
            Component component = getComponentAt(i);
            DashboardComponent newChild = verticalContainer.getChildren().get(i);
            ((DashboardAdapter) component).update(newChild);

            if (component instanceof HasSize) {
                ((HasSize) component).setWidthFull();
            }
            if (component instanceof HasStyle) {
                ((HasStyle) component).addClassName("dashboard-vertical-child");
            }
        }
    }

    @Override
    public boolean equalsType(DashboardComponent dashboardComponent) {
        if (!(dashboardComponent instanceof VerticalContainer)) {
            return false;
        }

        VerticalContainer verticalContainer = (VerticalContainer) dashboardComponent;
        return Objects.equals(this.verticalContainer.isCard(), verticalContainer.isCard());
    }
}
