package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.value.ValueChangeMode;
import dashboard.DashboardComponent;
import dashboard.component.DashboardFloatingNumberField;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardAdapter;

import java.math.BigDecimal;
import java.util.Objects;

public class DashboardFloatingNumberFieldAdapter extends FlexLayout implements DashboardAdapter<DashboardFloatingNumberField> {

    private DashboardFloatingNumberField dashboardFloatingNumberField;
    private double defaultValue;
    private final NumberField numberField = new NumberField();

    public DashboardFloatingNumberFieldAdapter(DashboardFloatingNumberField dashboardFloatingNumberField) {
        setFlexDirection(FlexDirection.ROW);

        numberField.getStyle().set("margin-top", "-16px");
        numberField.setHasControls(true);
        numberField.setStep(0.01);
        numberField.setLabel(dashboardFloatingNumberField.getLabel());
        numberField.setReadOnly(dashboardFloatingNumberField.getEditButton());
        add(numberField);
        setFlexGrow(1, numberField);

        if (dashboardFloatingNumberField.getEditButton()) {
            DashboardTextFieldButtons dashboardTextFieldButtons = new DashboardTextFieldButtons(true);
            dashboardTextFieldButtons.setModeChangeListener(editMode -> numberField.setReadOnly(!editMode));
            dashboardTextFieldButtons.setCancelListener(() -> numberField.setValue(defaultValue));
            dashboardTextFieldButtons.setConfirmListener(this::trigger);
            add(dashboardTextFieldButtons);
        } else {
            numberField.setValueChangeMode(ValueChangeMode.ON_BLUR);
            numberField.addValueChangeListener(event -> trigger());
        }

        update(dashboardFloatingNumberField);
    }

    private boolean trigger() {
        if (dashboardFloatingNumberField.isEnabled() &&
                numberField.getValue() >= dashboardFloatingNumberField.getMin() &&
                numberField.getValue() <= dashboardFloatingNumberField.getMax() &&
                BigDecimal.valueOf(numberField.getValue()).scale() <= 2
        ) {
            if (defaultValue != numberField.getValue()) {
                defaultValue = numberField.getValue();
                dashboardFloatingNumberField.trigger(numberField.getValue());
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void update(DashboardFloatingNumberField dashboardFloatingNumberField) {
        DashboardComponent previousDashboardComponent = this.dashboardFloatingNumberField;
        this.dashboardFloatingNumberField = dashboardFloatingNumberField;
        if (dashboardComponentsAreEqual(previousDashboardComponent, dashboardFloatingNumberField) && defaultValue == dashboardFloatingNumberField.getValue()) {
            return;
        }

        defaultValue = dashboardFloatingNumberField.getValue();
        numberField.setPlaceholder(dashboardFloatingNumberField.getPlaceholder());
        numberField.setMin(dashboardFloatingNumberField.getMin());
        numberField.setMax(dashboardFloatingNumberField.getMax());
        numberField.setErrorMessage(getTranslation("dash.floatingnumberfield.minmax", dashboardFloatingNumberField.getMin(), dashboardFloatingNumberField.getMax()));
        numberField.setValue(dashboardFloatingNumberField.getValue());
    }

    @Override
    public boolean equalsType(DashboardComponent dashboardComponent) {
        if (!(dashboardComponent instanceof DashboardFloatingNumberField)) {
            return false;
        }

        DashboardFloatingNumberField dashboardFloatingNumberField = (DashboardFloatingNumberField) dashboardComponent;
        return Objects.equals(this.dashboardFloatingNumberField.getLabel(), dashboardFloatingNumberField.getLabel()) &&
                Objects.equals(this.dashboardFloatingNumberField.getEditButton(), dashboardFloatingNumberField.getEditButton());
    }

}
