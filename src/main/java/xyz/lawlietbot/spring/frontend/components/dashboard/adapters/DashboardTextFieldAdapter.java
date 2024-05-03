package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import dashboard.component.DashboardTextField;

public class DashboardTextFieldAdapter extends FlexLayout {

    private String defaultValue;

    public DashboardTextFieldAdapter(DashboardTextField dashboardTextField) {
        defaultValue = dashboardTextField.getValue();
        setFlexDirection(FlexDirection.ROW);

        TextField textField = new TextField();
        textField.getStyle().set("margin-top", "-16px");
        textField.setLabel(dashboardTextField.getLabel());
        textField.setPlaceholder(dashboardTextField.getPlaceholder());
        textField.setReadOnly(dashboardTextField.getEditButton());
        textField.setEnabled(dashboardTextField.isEnabled());
        textField.setMinLength((int) dashboardTextField.getMin());
        textField.setMaxLength((int) dashboardTextField.getMax());
        textField.setValue(dashboardTextField.getValue());
        if (dashboardTextField.getMin() <= 0) {
            textField.setErrorMessage(getTranslation("dash.textfield.max", dashboardTextField.getMax()));
        } else {
            textField.setErrorMessage(getTranslation("dash.textfield.minmax", dashboardTextField.getMin(), dashboardTextField.getMax()));
        }
        add(textField);
        setFlexGrow(1, textField);

        if (dashboardTextField.isEnabled()) {
            if (dashboardTextField.getEditButton()) {
                DashboardTextFieldButtons dashboardTextFieldButtons = new DashboardTextFieldButtons(true);
                dashboardTextFieldButtons.setModeChangeListener(editMode -> textField.setReadOnly(!editMode));
                dashboardTextFieldButtons.setCancelListener(() -> textField.setValue(defaultValue));
                dashboardTextFieldButtons.setConfirmListener(() -> trigger(dashboardTextField, textField));
                add(dashboardTextFieldButtons);
            } else {
                textField.setValueChangeMode(ValueChangeMode.ON_BLUR);
                textField.addValueChangeListener(event -> trigger(dashboardTextField, textField));
            }
        }
    }

    private boolean trigger(DashboardTextField dashboardTextField, TextField textField) {
        if (dashboardTextField.isEnabled() &&
                textField.getValue().length() >= dashboardTextField.getMin() &&
                textField.getValue().length() <= dashboardTextField.getMax()
        ) {
            if (!defaultValue.equals(textField.getValue())) {
                defaultValue = textField.getValue();
                dashboardTextField.trigger(textField.getValue());
            }
            return true;
        } else {
            textField.setValue(defaultValue);
            return false;
        }
    }

}
