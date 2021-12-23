package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
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
        textField.setReadOnly(true);
        textField.setEnabled(dashboardTextField.isEnabled());
        textField.setMinLength(dashboardTextField.getMin());
        textField.setMaxLength(dashboardTextField.getMax());
        textField.setValue(dashboardTextField.getValue());
        if (dashboardTextField.getMin() <= 0) {
            textField.setErrorMessage(getTranslation("dash.textfield.max", dashboardTextField.getMax()));
        } else {
            textField.setErrorMessage(getTranslation("dash.textfield.minmax", dashboardTextField.getMin(), dashboardTextField.getMax()));
        }
        add(textField);
        setFlexGrow(1, textField);

        if (dashboardTextField.isEnabled()) {
            DashboardTextFieldButtons dashboardTextFieldButtons = new DashboardTextFieldButtons(true);
            dashboardTextFieldButtons.setModeChangeListener(editMode -> textField.setReadOnly(!editMode));
            dashboardTextFieldButtons.setCancelListener(() -> textField.setValue(defaultValue));
            dashboardTextFieldButtons.setConfirmListener(() -> {
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
                    return false;
                }
            });
            add(dashboardTextFieldButtons);
        }
    }

}
