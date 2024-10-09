package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.value.ValueChangeMode;
import dashboard.DashboardComponent;
import dashboard.component.DashboardNumberField;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardAdapter;

import java.util.Objects;

public class DashboardNumberFieldAdapter extends FlexLayout implements DashboardAdapter<DashboardNumberField> {

    private DashboardNumberField dashboardNumberField;
    private long defaultValue;
    private final NumberField numberField = new NumberField();

    public DashboardNumberFieldAdapter(DashboardNumberField dashboardNumberField) {
        setFlexDirection(FlexDirection.ROW);

        numberField.getStyle().set("margin-top", "-16px");
        numberField.setStepButtonsVisible(true);
        numberField.setStep(1.0);
        numberField.setLabel(dashboardNumberField.getLabel());
        numberField.setReadOnly(dashboardNumberField.getEditButton());
        add(numberField);
        setFlexGrow(1, numberField);

        if (dashboardNumberField.getEditButton()) {
            DashboardTextFieldButtons dashboardTextFieldButtons = new DashboardTextFieldButtons(true);
            dashboardTextFieldButtons.setModeChangeListener(editMode -> numberField.setReadOnly(!editMode));
            dashboardTextFieldButtons.setCancelListener(() -> numberField.setValue((double) defaultValue));
            dashboardTextFieldButtons.setConfirmListener(this::trigger);
            add(dashboardTextFieldButtons);
        } else {
            numberField.setValueChangeMode(ValueChangeMode.ON_CHANGE);
            numberField.addValueChangeListener(event -> trigger());
        }

        update(dashboardNumberField);
    }

    private boolean trigger() {
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

    @Override
    public void update(DashboardNumberField dashboardNumberField) {
        DashboardComponent previousDashboardComponent = this.dashboardNumberField;
        this.dashboardNumberField = dashboardNumberField;
        if (dashboardComponentsAreEqual(previousDashboardComponent, dashboardNumberField) && defaultValue == dashboardNumberField.getValue()) {
            return;
        }

        defaultValue = dashboardNumberField.getValue();
        numberField.setPlaceholder(dashboardNumberField.getPlaceholder());
        numberField.setMin(dashboardNumberField.getMin());
        numberField.setMax(dashboardNumberField.getMax());
        numberField.setErrorMessage(getTranslation("dash.numberfield.minmax", dashboardNumberField.getMin(), dashboardNumberField.getMax()));
        numberField.setValue((double) dashboardNumberField.getValue());
    }

    @Override
    public boolean equalsType(DashboardComponent dashboardComponent) {
        if (!(dashboardComponent instanceof DashboardNumberField)) {
            return false;
        }

        DashboardNumberField dashboardNumberField = (DashboardNumberField) dashboardComponent;
        return Objects.equals(this.dashboardNumberField.getLabel(), dashboardNumberField.getLabel()) &&
                Objects.equals(this.dashboardNumberField.getEditButton(), dashboardNumberField.getEditButton());
    }

}
