package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.value.ValueChangeMode;
import dashboard.component.DashboardNumberField;

public class DashboardNumberFieldAdapter extends FlexLayout {

    private long defaultValue;

    public DashboardNumberFieldAdapter(DashboardNumberField dashboardNumberField) {
        defaultValue = dashboardNumberField.getValue();
        setFlexDirection(FlexDirection.ROW);

        NumberField numberField = new NumberField();
        numberField.getStyle().set("margin-top", "-16px");
        numberField.setHasControls(true);
        numberField.setStep(1.0);
        numberField.setLabel(dashboardNumberField.getLabel());
        numberField.setPlaceholder(dashboardNumberField.getPlaceholder());
        numberField.setReadOnly(dashboardNumberField.getEditButton());
        numberField.setEnabled(dashboardNumberField.isEnabled());
        numberField.setMin(dashboardNumberField.getMin());
        numberField.setMax(dashboardNumberField.getMax());
        numberField.setErrorMessage(getTranslation("dash.numberfield.minmax", dashboardNumberField.getMin(), dashboardNumberField.getMax()));
        numberField.setValue((double) dashboardNumberField.getValue());
        add(numberField);
        setFlexGrow(1, numberField);

        if (dashboardNumberField.isEnabled()) {
            if (dashboardNumberField.getEditButton()) {
                DashboardTextFieldButtons dashboardTextFieldButtons = new DashboardTextFieldButtons(true);
                dashboardTextFieldButtons.setModeChangeListener(editMode -> numberField.setReadOnly(!editMode));
                dashboardTextFieldButtons.setCancelListener(() -> numberField.setValue((double) defaultValue));
                dashboardTextFieldButtons.setConfirmListener(() -> trigger(dashboardNumberField, numberField));
                add(dashboardTextFieldButtons);
            } else {
                numberField.setValueChangeMode(ValueChangeMode.ON_BLUR);
                numberField.addValueChangeListener(event -> trigger(dashboardNumberField, numberField));
            }
        }
    }

    private boolean trigger(DashboardNumberField dashboardNumberField, NumberField numberField) {
        if (dashboardNumberField.isEnabled() &&
                numberField.getValue() >= dashboardNumberField.getMin() &&
                numberField.getValue() <= dashboardNumberField.getMax() &&
                numberField.getValue() == Math.floor(numberField.getValue())
        ) {
            if (defaultValue != numberField.getValue()) {
                long value = numberField.getValue().longValue();
                defaultValue = value;
                dashboardNumberField.trigger(value);
            }
            return true;
        } else {
            return false;
        }
    }

}
