package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import dashboard.component.DashboardButton;

public class DashboardButtonAdapter extends Button {

    public DashboardButtonAdapter(DashboardButton dashboardButton) {
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

        Icon icon = extractIcon(dashboardButton);
        if (icon != null) {
            setIcon(icon);
        } else {
            setText(dashboardButton.getText());
        }

        setEnabled(dashboardButton.isEnabled());
        addClickListener(e -> dashboardButton.trigger());
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

}
