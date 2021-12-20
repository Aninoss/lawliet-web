package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.checkbox.Checkbox;
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
        if (!dashboardSwitch.isEnabled()) {
            label.getStyle().set("color", "var(--lumo-disabled-text-color)");
        }

        Div space = new Div();
        Checkbox checkbox = new Checkbox();
        checkbox.setClassName("toggle-button");
        checkbox.setEnabled(dashboardSwitch.isEnabled());
        checkbox.addValueChangeListener(e -> dashboardSwitch.trigger(e.getValue()));

        add(label);
        if (dashboardSwitch.getSubtitle() != null) {
            Icon infoIcon = VaadinIcon.QUESTION_CIRCLE.create();
            infoIcon.addClassName("dashboard-info-icon");
            infoIcon.getStyle().set("height", "16px")
                            .set("margin-left", "4px");
            infoIcon.getElement().setProperty("title", dashboardSwitch.getSubtitle());
            add(infoIcon);
        }
        add(space, checkbox);
        setFlexGrow(1, space);
    }

}
