package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import dashboard.DashboardComponent;
import dashboard.component.DashboardTextField;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardAdapter;

import java.util.Objects;

public class DashboardTextFieldAdapter extends FlexLayout implements DashboardAdapter<DashboardTextField> {

    private DashboardTextField dashboardTextField;
    private String defaultValue;
    private final TextField textField = new TextField();

    public DashboardTextFieldAdapter(DashboardTextField dashboardTextField) {
        setFlexDirection(FlexDirection.ROW);

        textField.setLabel(dashboardTextField.getLabel());
        textField.setReadOnly(dashboardTextField.getEditButton());
        textField.getStyle().set("margin-top", "-16px");
        add(textField);
        setFlexGrow(1, textField);

        if (dashboardTextField.getEditButton()) {
            DashboardTextFieldButtons dashboardTextFieldButtons = new DashboardTextFieldButtons(true);
            dashboardTextFieldButtons.setModeChangeListener(editMode -> textField.setReadOnly(!editMode));
            dashboardTextFieldButtons.setCancelListener(() -> textField.setValue(defaultValue));
            dashboardTextFieldButtons.setConfirmListener(this::trigger);
            add(dashboardTextFieldButtons);
        } else {
            textField.setValueChangeMode(ValueChangeMode.ON_BLUR);
            textField.addValueChangeListener(event -> trigger());
        }

        update(dashboardTextField);
    }

    private boolean trigger() {
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

    @Override
    public void update(DashboardTextField dashboardTextField) {
        DashboardComponent previousDashboardComponent = this.dashboardTextField;
        this.dashboardTextField = dashboardTextField;
        if (dashboardComponentsAreEqual(previousDashboardComponent, dashboardTextField) && Objects.equals(defaultValue, dashboardTextField.getValue())) {
            return;
        }

        defaultValue = dashboardTextField.getValue();
        textField.setPlaceholder(dashboardTextField.getPlaceholder());
        textField.setMinLength((int) dashboardTextField.getMin());
        textField.setMaxLength((int) dashboardTextField.getMax());
        textField.setValue(dashboardTextField.getValue());
        if (dashboardTextField.getMin() <= 0) {
            textField.setErrorMessage(getTranslation("dash.textfield.max", dashboardTextField.getMax()));
        } else {
            textField.setErrorMessage(getTranslation("dash.textfield.minmax", dashboardTextField.getMin(), dashboardTextField.getMax()));
        }
    }

    @Override
    public boolean equalsType(DashboardComponent dashboardComponent) {
        if (!(dashboardComponent instanceof DashboardTextField)) {
            return false;
        }

        DashboardTextField dashboardTextField = (DashboardTextField) dashboardComponent;
        return Objects.equals(this.dashboardTextField.getLabel(), dashboardTextField.getLabel()) &&
                Objects.equals(this.dashboardTextField.getEditButton(), dashboardTextField.getEditButton());
    }

}
