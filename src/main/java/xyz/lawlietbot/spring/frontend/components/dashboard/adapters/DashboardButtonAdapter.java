package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import dashboard.DashboardComponent;
import dashboard.component.DashboardButton;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardAdapter;

import java.util.Objects;

public class DashboardButtonAdapter extends Button implements DashboardAdapter<DashboardButton> {

    private DashboardButton dashboardButton;

    public DashboardButtonAdapter(DashboardButton dashboardButton) {
        Icon icon = extractIcon(dashboardButton);
        if (icon != null) {
            setIcon(icon);
        } else {
            setText(dashboardButton.getText());
        }

        update(dashboardButton);
        addClickListener(e -> this.dashboardButton.trigger());
    }

    private Icon extractIcon(DashboardButton dashboardButton) {
        switch (dashboardButton.getText()) {
            case "🡑":
                return VaadinIcon.CHEVRON_UP.create();
            case "🡓":
                return VaadinIcon.CHEVRON_DOWN.create();
            case "🖊️":
                return VaadinIcon.PENCIL.create();
            default:
                return null;
        }
    }

    @Override
    public void update(DashboardButton dashboardButton) {
        DashboardComponent previousDashboardComponent = this.dashboardButton;
        this.dashboardButton = dashboardButton;
        if (dashboardComponentsAreEqual(previousDashboardComponent, dashboardButton)) {
            return;
        }

        removeThemeName(getThemeName());
        switch (dashboardButton.getStyle()) {
            case PRIMARY:
                addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                break;

            case DANGER:
                addThemeVariants(ButtonVariant.LUMO_ERROR);
                break;

            case TERTIARY:
                addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                break;

            default:
        }
    }

    @Override
    public boolean equalsType(DashboardComponent dashboardComponent) {
        if (!(dashboardComponent instanceof DashboardButton)) {
            return false;
        }

        DashboardButton dashboardButton = (DashboardButton) dashboardComponent;
        return Objects.equals(this.dashboardButton.getText(), dashboardButton.getText());
    }

}
