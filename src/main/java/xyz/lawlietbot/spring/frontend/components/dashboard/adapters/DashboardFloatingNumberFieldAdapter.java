package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.value.ValueChangeMode;
import dashboard.component.DashboardFloatingNumberField;

import java.math.BigDecimal;

public class DashboardFloatingNumberFieldAdapter extends FlexLayout {

    private double defaultValue;

    public DashboardFloatingNumberFieldAdapter(DashboardFloatingNumberField dashboardFloatingNumberField) {
        defaultValue = dashboardFloatingNumberField.getValue();
        setFlexDirection(FlexDirection.ROW);

        NumberField numberField = new NumberField();
        numberField.getStyle().set("margin-top", "-16px");
        numberField.setHasControls(true);
        numberField.setStep(0.01);
        numberField.setLabel(dashboardFloatingNumberField.getLabel());
        numberField.setPlaceholder(dashboardFloatingNumberField.getPlaceholder());
        numberField.setReadOnly(dashboardFloatingNumberField.getEditButton());
        numberField.setEnabled(dashboardFloatingNumberField.isEnabled());
        numberField.setMin(dashboardFloatingNumberField.getMin());
        numberField.setMax(dashboardFloatingNumberField.getMax());
        numberField.setErrorMessage(getTranslation("dash.floatingnumberfield.minmax", dashboardFloatingNumberField.getMin(), dashboardFloatingNumberField.getMax()));
        numberField.setValue(dashboardFloatingNumberField.getValue());
        add(numberField);
        setFlexGrow(1, numberField);

        if (dashboardFloatingNumberField.isEnabled()) {
            if (dashboardFloatingNumberField.getEditButton()) {
                DashboardTextFieldButtons dashboardTextFieldButtons = new DashboardTextFieldButtons(true);
                dashboardTextFieldButtons.setModeChangeListener(editMode -> numberField.setReadOnly(!editMode));
                dashboardTextFieldButtons.setCancelListener(() -> numberField.setValue(defaultValue));
                dashboardTextFieldButtons.setConfirmListener(() -> trigger(dashboardFloatingNumberField, numberField));
                add(dashboardTextFieldButtons);
            } else {
                numberField.setValueChangeMode(ValueChangeMode.ON_BLUR);
                numberField.addValueChangeListener(event -> trigger(dashboardFloatingNumberField, numberField));
            }
        }
    }

    private boolean trigger(DashboardFloatingNumberField dashboardFloatingNumberField, NumberField numberField) {
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

}
