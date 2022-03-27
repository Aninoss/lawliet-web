package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import dashboard.component.DashboardMultiLineTextField;

public class DashboardMultiLineTextFieldAdapter extends FlexLayout {

    private String defaultValue;

    public DashboardMultiLineTextFieldAdapter(DashboardMultiLineTextField dashboardTextField) {
        defaultValue = dashboardTextField.getValue();
        setFlexDirection(FlexDirection.ROW);

        TextArea textArea = new TextArea();
        textArea.getStyle().set("margin-top", "-16px");
        textArea.setLabel(dashboardTextField.getLabel());
        textArea.setPlaceholder(dashboardTextField.getPlaceholder());
        textArea.setReadOnly(dashboardTextField.getEditButton());
        textArea.setEnabled(dashboardTextField.isEnabled());
        textArea.setMinLength((int) dashboardTextField.getMin());
        textArea.setMaxLength((int) dashboardTextField.getMax());
        textArea.setValue(dashboardTextField.getValue());
        if (dashboardTextField.getMin() <= 0) {
            textArea.setErrorMessage(getTranslation("dash.textfield.max", dashboardTextField.getMax()));
        } else {
            textArea.setErrorMessage(getTranslation("dash.textfield.minmax", dashboardTextField.getMin(), dashboardTextField.getMax()));
        }
        add(textArea);
        setFlexGrow(1, textArea);

        if (dashboardTextField.isEnabled()) {
            if (dashboardTextField.getEditButton()) {
                DashboardTextFieldButtons dashboardTextFieldButtons = new DashboardTextFieldButtons(false);
                dashboardTextFieldButtons.setModeChangeListener(editMode -> textArea.setReadOnly(!editMode));
                dashboardTextFieldButtons.setCancelListener(() -> textArea.setValue(defaultValue));
                dashboardTextFieldButtons.setConfirmListener(() -> trigger(dashboardTextField, textArea));
                add(dashboardTextFieldButtons);
            } else {
                textArea.setValueChangeMode(ValueChangeMode.ON_BLUR);
                textArea.addValueChangeListener(event -> trigger(dashboardTextField, textArea));
            }
        }
    }

    private boolean trigger(DashboardMultiLineTextField dashboardTextField, TextArea textArea) {
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
    }

}
