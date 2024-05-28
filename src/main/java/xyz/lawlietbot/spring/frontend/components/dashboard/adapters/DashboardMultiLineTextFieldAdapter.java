package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import dashboard.DashboardComponent;
import dashboard.component.DashboardMultiLineTextField;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardAdapter;

import java.util.Objects;

public class DashboardMultiLineTextFieldAdapter extends FlexLayout implements DashboardAdapter<DashboardMultiLineTextField> {

    private DashboardMultiLineTextField dashboardMultiLineTextField;
    private String defaultValue;
    private final TextArea textArea = new TextArea();

    public DashboardMultiLineTextFieldAdapter(DashboardMultiLineTextField dashboardMultiLineTextField) {
        setFlexDirection(FlexDirection.ROW);

        textArea.getStyle().set("margin-top", "-16px");
        textArea.setLabel(dashboardMultiLineTextField.getLabel());
        textArea.setReadOnly(dashboardMultiLineTextField.getEditButton());
        add(textArea);
        setFlexGrow(1, textArea);

        if (dashboardMultiLineTextField.getEditButton()) {
            DashboardTextFieldButtons dashboardTextFieldButtons = new DashboardTextFieldButtons(false);
            dashboardTextFieldButtons.setModeChangeListener(editMode -> textArea.setReadOnly(!editMode));
            dashboardTextFieldButtons.setCancelListener(() -> textArea.setValue(defaultValue));
            dashboardTextFieldButtons.setConfirmListener(this::trigger);
            add(dashboardTextFieldButtons);
        } else {
            textArea.setValueChangeMode(ValueChangeMode.ON_BLUR);
            textArea.addValueChangeListener(event -> trigger());
        }

        update(dashboardMultiLineTextField);
    }

    private boolean trigger() {
        if (dashboardMultiLineTextField.isEnabled() &&
                textArea.getValue().length() >= dashboardMultiLineTextField.getMin() &&
                textArea.getValue().length() <= dashboardMultiLineTextField.getMax()
        ) {
            if (!defaultValue.equals(textArea.getValue())) {
                defaultValue = textArea.getValue();
                dashboardMultiLineTextField.trigger(textArea.getValue());
            }
            return true;
        } else {
            textArea.setValue(defaultValue);
            return false;
        }
    }

    @Override
    public void update(DashboardMultiLineTextField dashboardMultiLineTextField) {
        DashboardComponent previousDashboardComponent = this.dashboardMultiLineTextField;
        this.dashboardMultiLineTextField = dashboardMultiLineTextField;
        if (dashboardComponentsAreEqual(previousDashboardComponent, dashboardMultiLineTextField) && Objects.equals(defaultValue, dashboardMultiLineTextField.getValue())) {
            return;
        }

        defaultValue = dashboardMultiLineTextField.getValue();
        textArea.setPlaceholder(dashboardMultiLineTextField.getPlaceholder());
        textArea.setMinLength((int) dashboardMultiLineTextField.getMin());
        textArea.setMaxLength((int) dashboardMultiLineTextField.getMax());
        textArea.setValue(dashboardMultiLineTextField.getValue());
        if (dashboardMultiLineTextField.getMin() <= 0) {
            textArea.setErrorMessage(getTranslation("dash.textfield.max", dashboardMultiLineTextField.getMax()));
        } else {
            textArea.setErrorMessage(getTranslation("dash.textfield.minmax", dashboardMultiLineTextField.getMin(), dashboardMultiLineTextField.getMax()));
        }
    }

    @Override
    public boolean equalsType(DashboardComponent dashboardComponent) {
        if (!(dashboardComponent instanceof DashboardMultiLineTextField)) {
            return false;
        }

        DashboardMultiLineTextField dashboardMultiLineTextField = (DashboardMultiLineTextField) dashboardComponent;
        return Objects.equals(this.dashboardMultiLineTextField.getLabel(), dashboardMultiLineTextField.getLabel()) &&
                Objects.equals(this.dashboardMultiLineTextField.getEditButton(), dashboardMultiLineTextField.getEditButton());
    }

}
