package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import dashboard.component.DashboardSwitch;
import xyz.lawlietbot.spring.frontend.components.SpanWithLinebreaks;

public class DashboardSwitchAdapter extends HorizontalLayout {

    public DashboardSwitchAdapter(DashboardSwitch dashboardSwitch) {
        setPadding(false);
        setAlignItems(Alignment.CENTER);

        VerticalLayout labelLayout = new VerticalLayout();
        labelLayout.setPadding(false);
        labelLayout.setSpacing(false);

        Label label = new Label(dashboardSwitch.getLabel());
        if (!dashboardSwitch.isEnabled()) {
            label.getStyle().set("color", "var(--lumo-disabled-text-color)");
        }
        labelLayout.add(label);

        if (dashboardSwitch.getSubtitle() != null) {
            SpanWithLinebreaks span = new SpanWithLinebreaks(dashboardSwitch.getSubtitle());
            span.addClassName("dashboard-text-hint");
            if (dashboardSwitch.getSubtitle().contains("\n")) {
                span.getStyle().set("margin-top", "0.5rem");
            }
            labelLayout.add(span);
        }
        add(labelLayout);

        Div space = new Div();
        add(space);
        setFlexGrow(1, space);

        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setEnabled(dashboardSwitch.isEnabled());
        toggleButton.setValue(dashboardSwitch.isChecked());
        toggleButton.addValueChangeListener(e -> dashboardSwitch.trigger(e.getValue()));
        add(toggleButton);
    }

}
