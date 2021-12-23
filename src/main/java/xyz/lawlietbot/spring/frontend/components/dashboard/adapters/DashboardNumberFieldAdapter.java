package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.NumberField;
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
        numberField.setReadOnly(true);
        numberField.setEnabled(dashboardNumberField.isEnabled());
        numberField.setMin(dashboardNumberField.getMin());
        numberField.setMax(dashboardNumberField.getMax());
        numberField.setErrorMessage(getTranslation("dash.numberfield.minmax", dashboardNumberField.getMin(), dashboardNumberField.getMax()));
        numberField.setValue((double) dashboardNumberField.getValue());
        add(numberField);
        setFlexGrow(1, numberField);

        if (dashboardNumberField.isEnabled()) {
            DashboardTextFieldButtons dashboardTextFieldButtons = new DashboardTextFieldButtons(true);
            dashboardTextFieldButtons.setModeChangeListener(editMode -> numberField.setReadOnly(!editMode));
            dashboardTextFieldButtons.setCancelListener(() -> numberField.setValue((double) defaultValue));
            dashboardTextFieldButtons.setConfirmListener(() -> {
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
            });
            add(dashboardTextFieldButtons);
        }
    }

}
