package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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

        setText(dashboardButton.getText());
        setEnabled(dashboardButton.isEnabled());
        addClickListener(e -> dashboardButton.trigger());
    }

}
