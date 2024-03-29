package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import dashboard.component.DashboardSwitch;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;

public class DashboardSwitchAdapter extends HorizontalLayout {

    public DashboardSwitchAdapter(DashboardSwitch dashboardSwitch, ConfirmationDialog dialog) {
        setPadding(false);
        setAlignItems(Alignment.CENTER);

        Label label = new Label(dashboardSwitch.getLabel());
        if (!dashboardSwitch.isEnabled()) {
            label.getStyle().set("color", "var(--lumo-disabled-text-color)");
        }

        Div space = new Div();
        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setEnabled(dashboardSwitch.isEnabled());
        toggleButton.setValue(dashboardSwitch.isChecked());
        toggleButton.addValueChangeListener(e -> dashboardSwitch.trigger(e.getValue()));

        add(label);
        if (dashboardSwitch.getSubtitle() != null) {
            Icon infoIcon = VaadinIcon.QUESTION_CIRCLE.create();
            infoIcon.addClassName("dashboard-info-icon");
            infoIcon.getElement().setProperty("title", dashboardSwitch.getSubtitle());
            infoIcon.addClickListener(e -> dialog.open(dashboardSwitch.getSubtitle(), () -> {
            }));
            add(infoIcon);
        }
        add(space, toggleButton);
        setFlexGrow(1, space);
    }

}
