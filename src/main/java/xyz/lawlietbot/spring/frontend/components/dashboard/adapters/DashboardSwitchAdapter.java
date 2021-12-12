package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import dashboard.component.DashboardSwitch;

public class DashboardSwitchAdapter extends HorizontalLayout {

    public DashboardSwitchAdapter(DashboardSwitch dashboardSwitch) {
        setPadding(false);
        setAlignItems(Alignment.CENTER);
        addClassName("dashboard-switch-layout");

        Label label = new Label(dashboardSwitch.getLabel());
        Div space = new Div();
        ToggleButton toggleButton = new ToggleButton(dashboardSwitch.isChecked());
        toggleButton.setEnabled(dashboardSwitch.isEnabled());
        toggleButton.addValueChangeListener(e -> dashboardSwitch.trigger(e.getValue()));

        add(label);
        if (dashboardSwitch.getSubtitle() != null) {
            Icon infoIcon = VaadinIcon.INFO_CIRCLE.create();
            infoIcon.addClassName("dashboard-info-icon");
            infoIcon.getStyle().set("height", "16px")
                            .set("margin-left", "8px");
            infoIcon.getElement().setProperty("title", dashboardSwitch.getSubtitle());
            add(infoIcon);
        }
        add(space, toggleButton);
        setFlexGrow(1, space);
    }

}
