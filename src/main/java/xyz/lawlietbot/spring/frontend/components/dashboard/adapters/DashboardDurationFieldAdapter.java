package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.value.ValueChangeMode;
import dashboard.component.DashboardDurationField;

public class DashboardDurationFieldAdapter extends FlexLayout {

    private long defaultValue;

    public DashboardDurationFieldAdapter(DashboardDurationField dashboardDurationField) {
        defaultValue = dashboardDurationField.getValue();
        setFlexDirection(FlexDirection.ROW);

        NumberField daysField = generateNumberField(
                getTranslation("dash.duration.days"),
                dashboardDurationField.isEnabled(),
                999,
                extractValueDays(defaultValue),
                false,
                dashboardDurationField.getEditButton()
        );
        NumberField hoursField = generateNumberField(
                getTranslation("dash.duration.hours"),
                dashboardDurationField.isEnabled(),
                23,
                extractValueHours(defaultValue),
                true,
                dashboardDurationField.getEditButton()
        );
        NumberField minutesField = generateNumberField(
                getTranslation("dash.duration.minutes"),
                dashboardDurationField.isEnabled(),
                59,
                extractValueMinutes(defaultValue),
                true,
                dashboardDurationField.getEditButton()
        );

        if (dashboardDurationField.isEnabled()) {
            if (dashboardDurationField.getEditButton()) {
                DashboardTextFieldButtons dashboardTextFieldButtons = new DashboardTextFieldButtons(true);
                dashboardTextFieldButtons.setModeChangeListener(editMode -> {
                    daysField.setReadOnly(!editMode);
                    hoursField.setReadOnly(!editMode);
                    minutesField.setReadOnly(!editMode);
                });
                dashboardTextFieldButtons.setCancelListener(() -> {
                    daysField.setValue((double) extractValueDays(defaultValue));
                    hoursField.setValue((double) extractValueHours(defaultValue));
                    minutesField.setValue((double) extractValueMinutes(defaultValue));
                });
                dashboardTextFieldButtons.setConfirmListener(() -> trigger(dashboardDurationField, daysField, hoursField, minutesField));
                add(dashboardTextFieldButtons);
            } else {
                daysField.setValueChangeMode(ValueChangeMode.ON_BLUR);
                hoursField.setValueChangeMode(ValueChangeMode.ON_BLUR);
                minutesField.setValueChangeMode(ValueChangeMode.ON_BLUR);
                daysField.addValueChangeListener(event -> trigger(dashboardDurationField, daysField, hoursField, minutesField));
                hoursField.addValueChangeListener(event -> trigger(dashboardDurationField, daysField, hoursField, minutesField));
                minutesField.addValueChangeListener(event -> trigger(dashboardDurationField, daysField, hoursField, minutesField));
            }
        }
    }

    private boolean trigger(DashboardDurationField dashboardDurationField, NumberField daysField, NumberField hoursField, NumberField minutesField) {
        if (dashboardDurationField.isEnabled() &&
                checkNumberField(daysField, 999) &&
                checkNumberField(hoursField, 23) &&
                checkNumberField(minutesField, 59)
        ) {
            long value = minutesField.getValue().longValue() + hoursField.getValue().longValue() * 60 + daysField.getValue().longValue() * 1440;
            if (value == 0) {
                value = 1;
                minutesField.setValue(1.0);
            }
            if (value != defaultValue) {
                defaultValue = value;
                dashboardDurationField.trigger(value);
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean checkNumberField(NumberField numberField, int max) {
        return numberField.getValue() <= max &&
                numberField.getValue() >= 0 &&
                numberField.getValue() == Math.floor(numberField.getValue());
    }

    private NumberField generateNumberField(String label, boolean enabled, int max, int value, boolean marginLeft, boolean readOnly) {
        NumberField numberField = new NumberField();
        numberField.getStyle().set("margin-top", "-16px");
        numberField.setHasControls(false);
        numberField.setStep(1.0);
        numberField.setLabel(label);
        numberField.setReadOnly(readOnly);
        numberField.setEnabled(enabled);
        numberField.setMin(0);
        numberField.setMax(max);
        numberField.setErrorMessage(getTranslation("dash.numberfield.minmax", 0, max));
        numberField.setValue((double) value);
        numberField.setMinWidth("50px");
        if (marginLeft) {
            numberField.getStyle().set("margin-left", "8px");
        }

        add(numberField);
        setFlexGrow(1, numberField);
        return numberField;
    }

    private int extractValueMinutes(long value) {
        return (int) (value % 60);
    }

    private int extractValueHours(long value) {
        return (int) ((value / 60) % 1440);
    }

    private int extractValueDays(long value) {
        return (int) (value / 1440);
    }

}
