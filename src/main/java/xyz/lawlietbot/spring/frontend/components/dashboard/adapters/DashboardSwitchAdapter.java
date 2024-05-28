package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import dashboard.DashboardComponent;
import dashboard.component.DashboardSwitch;
import xyz.lawlietbot.spring.frontend.components.SpanWithLinebreaks;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardAdapter;

import java.util.Objects;

public class DashboardSwitchAdapter extends HorizontalLayout implements DashboardAdapter<DashboardSwitch> {

    private DashboardSwitch dashboardSwitch;
    private final Label label = new Label();
    private final ToggleButton toggleButton = new ToggleButton();

    public DashboardSwitchAdapter(DashboardSwitch dashboardSwitch) {
        setPadding(false);
        setAlignItems(Alignment.CENTER);

        VerticalLayout labelLayout = new VerticalLayout(label);
        labelLayout.setPadding(false);
        labelLayout.setSpacing(false);
        label.setText(dashboardSwitch.getLabel());

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

        toggleButton.addValueChangeListener(e -> this.dashboardSwitch.trigger(e.getValue()));
        add(toggleButton);

        update(dashboardSwitch);
    }

    @Override
    public void update(DashboardSwitch dashboardSwitch) {
        DashboardComponent previousDashboardComponent = this.dashboardSwitch;
        this.dashboardSwitch = dashboardSwitch;
        if (dashboardComponentsAreEqual(previousDashboardComponent, dashboardSwitch) && toggleButton.getValue() == dashboardSwitch.isChecked()) {
            return;
        }

        label.getStyle().remove("color");
        if (!dashboardSwitch.isEnabled()) {
            label.getStyle().set("color", "var(--lumo-disabled-text-color)");
        }

        toggleButton.setValue(dashboardSwitch.isChecked());
    }

    @Override
    public boolean equalsType(DashboardComponent dashboardComponent) {
        if (!(dashboardComponent instanceof DashboardSwitch)) {
            return false;
        }

        DashboardSwitch dashboardSwitch = (DashboardSwitch) dashboardComponent;
        return Objects.equals(this.dashboardSwitch.getLabel(), dashboardSwitch.getLabel()) &&
                Objects.equals(this.dashboardSwitch.getSubtitle(), dashboardSwitch.getSubtitle());
    }

}
