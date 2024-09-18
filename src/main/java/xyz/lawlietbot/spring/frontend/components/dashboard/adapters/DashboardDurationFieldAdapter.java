package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.value.ValueChangeMode;
import dashboard.DashboardComponent;
import dashboard.component.DashboardDurationField;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardAdapter;

import java.util.Objects;

public class DashboardDurationFieldAdapter extends FlexLayout implements DashboardAdapter<DashboardDurationField> {

    private DashboardDurationField dashboardDurationField;
    private long defaultValue;
    private final NumberField daysField;
    private final NumberField hoursField;
    private final NumberField minutesField;

    public DashboardDurationFieldAdapter(DashboardDurationField dashboardDurationField) {
        setFlexDirection(FlexDirection.ROW);

        daysField = generateNumberField(
                getTranslation("dash.duration.days"),
                999,
                false,
                dashboardDurationField.getEditButton()
        );
        hoursField = generateNumberField(
                getTranslation("dash.duration.hours"),
                23,
                true,
                dashboardDurationField.getEditButton()
        );
        if (dashboardDurationField.getIncludeMinutes()) {
            minutesField = generateNumberField(
                    getTranslation("dash.duration.minutes"),
                    59,
                    true,
                    dashboardDurationField.getEditButton()
            );
        } else {
            minutesField = null;
        }

        if (dashboardDurationField.getEditButton()) {
            DashboardTextFieldButtons dashboardTextFieldButtons = new DashboardTextFieldButtons(true);
            dashboardTextFieldButtons.setModeChangeListener(editMode -> {
                daysField.setReadOnly(!editMode);
                hoursField.setReadOnly(!editMode);
                if (minutesField != null) {
                    minutesField.setReadOnly(!editMode);
                }
            });
            dashboardTextFieldButtons.setCancelListener(() -> {
                daysField.setValue((double) extractValueDays(defaultValue));
                hoursField.setValue((double) extractValueHours(defaultValue));
                if (minutesField != null) {
                    minutesField.setValue((double) extractValueMinutes(defaultValue));
                }
            });
            dashboardTextFieldButtons.setConfirmListener(this::trigger);
            add(dashboardTextFieldButtons);
        } else {
            daysField.setValueChangeMode(ValueChangeMode.ON_BLUR);
            hoursField.setValueChangeMode(ValueChangeMode.ON_BLUR);
            daysField.addValueChangeListener(event -> trigger());
            hoursField.addValueChangeListener(event -> trigger());
            if (minutesField != null) {
                minutesField.setValueChangeMode(ValueChangeMode.ON_BLUR);
                minutesField.addValueChangeListener(event -> trigger());
            }
        }

        update(dashboardDurationField);
    }

    private boolean trigger() {
        if (dashboardDurationField.isEnabled() &&
                checkNumberField(daysField, 999) &&
                checkNumberField(hoursField, 23) &&
                (minutesField == null || checkNumberField(minutesField, 59))
        ) {
            long minutesValue = minutesField != null ? minutesField.getValue().longValue() : 0L;
            long value = minutesValue + hoursField.getValue().longValue() * 60 + daysField.getValue().longValue() * 1440;
            if (value == 0) {
                if (minutesField != null) {
                    value = 1;
                    minutesField.setValue(1.0);
                } else {
                    value = 60;
                    hoursField.setValue(1.0);
                }
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
        return numberField.getValue() != null &&
                numberField.getValue() <= max &&
                numberField.getValue() >= 0 &&
                numberField.getValue() == Math.floor(numberField.getValue());
    }

    private NumberField generateNumberField(String label, int max, boolean marginLeft, boolean readOnly) {
        NumberField numberField = new NumberField();
        numberField.getStyle().set("margin-top", "-16px");
        numberField.setStepButtonsVisible(false);
        numberField.setStep(1.0);
        numberField.setLabel(label);
        numberField.setReadOnly(readOnly);
        numberField.setMin(0);
        numberField.setMax(max);
        numberField.setErrorMessage(getTranslation("dash.numberfield.minmax", 0, max));
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
        return (int) ((value / 60) % 24);
    }

    private int extractValueDays(long value) {
        return (int) (value / 1440);
    }

    @Override
    public void update(DashboardDurationField dashboardDurationField) {
        DashboardComponent previousDashboardComponent = this.dashboardDurationField;
        this.dashboardDurationField = dashboardDurationField;
        if (dashboardComponentsAreEqual(previousDashboardComponent, dashboardDurationField) && defaultValue == dashboardDurationField.getValue()) {
            return;
        }

        defaultValue = dashboardDurationField.getValue();
        daysField.setValue((double) extractValueDays(dashboardDurationField.getValue()));
        hoursField.setValue((double) extractValueHours(dashboardDurationField.getValue()));
        if (minutesField != null) {
            minutesField.setValue((double) extractValueMinutes(dashboardDurationField.getValue()));
        }
    }

    @Override
    public boolean equalsType(DashboardComponent dashboardComponent) {
        if (!(dashboardComponent instanceof DashboardDurationField)) {
            return false;
        }

        DashboardDurationField dashboardDurationField = (DashboardDurationField) dashboardComponent;
        return Objects.equals(this.dashboardDurationField.getLabel(), dashboardDurationField.getLabel()) &&
                Objects.equals(this.dashboardDurationField.getEditButton(), dashboardDurationField.getEditButton()) &&
                Objects.equals(this.dashboardDurationField.getIncludeMinutes(), dashboardDurationField.getIncludeMinutes());
    }

}
