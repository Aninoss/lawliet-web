package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextArea;
import dashboard.component.DashboardMultiLineTextField;

public class DashboardMultiLineTextFieldAdapter extends FlexLayout {

    private String defaultValue;

    public DashboardMultiLineTextFieldAdapter(DashboardMultiLineTextField dashboardTextField) {
        defaultValue = dashboardTextField.getValue();
        setFlexDirection(FlexDirection.ROW);

        TextArea textArea = new TextArea();
        textArea.setLabel(dashboardTextField.getLabel());
        textArea.setPlaceholder(dashboardTextField.getPlaceholder());
        textArea.setReadOnly(true);
        textArea.setEnabled(dashboardTextField.isEnabled());
        textArea.setMinLength(dashboardTextField.getMin());
        textArea.setMaxLength(dashboardTextField.getMax());
        textArea.setValue(dashboardTextField.getValue());
        if (dashboardTextField.getMin() <= 0) {
            textArea.setErrorMessage(getTranslation("dash.textfield.max", dashboardTextField.getMax()));
        } else {
            textArea.setErrorMessage(getTranslation("dash.textfield.minmax", dashboardTextField.getMin(), dashboardTextField.getMax()));
        }
        add(textArea);
        setFlexGrow(1, textArea);

        if (dashboardTextField.isEnabled()) {
            DashboardTextFieldButtons dashboardTextFieldButtons = new DashboardTextFieldButtons(false);
            dashboardTextFieldButtons.setModeChangeListener(editMode -> textArea.setReadOnly(!editMode));
            dashboardTextFieldButtons.setCancelListener(() -> textArea.setValue(defaultValue));
            dashboardTextFieldButtons.setConfirmListener(() -> {
                if (dashboardTextField.isEnabled() &&
                        textArea.getValue().length() >= dashboardTextField.getMin() &&
                        textArea.getValue().length() <= dashboardTextField.getMax()
                ) {
                    if (!defaultValue.equals(textArea.getValue())) {
                        defaultValue = textArea.getValue();
                        dashboardTextField.trigger(textArea.getValue());
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
