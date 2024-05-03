package xyz.lawlietbot.spring.frontend.components.dashboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.dom.Style;
import dashboard.DashboardComponent;
import dashboard.component.*;
import dashboard.container.*;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.dashboard.adapters.*;

public class DashboardComponentConverter {

    public static Component convert(long guildId, long userId, DashboardComponent dashboardComponent, ConfirmationDialog dialog, boolean titleText) {
        Component component;
        switch (dashboardComponent.getType()) {
            case HorizontalContainer.TYPE:
                component = new HorizontalContainerAdapter((HorizontalContainer) dashboardComponent, guildId, userId, dialog);
                break;

            case VerticalContainer.TYPE:
                component = new VerticalContainerAdapter((VerticalContainer) dashboardComponent, guildId, userId, dialog);
                break;

            case ExpandableContainer.TYPE:
                component = new ExpandableContainerAdapter((ExpandableContainer) dashboardComponent, guildId, userId, dialog);
                break;

            case DashboardListContainer.TYPE:
                component = new DashboardListContainerAdapter((DashboardListContainer) dashboardComponent, guildId, userId, dialog);
                break;

            case DashboardButton.TYPE:
                component = new DashboardButtonAdapter((DashboardButton) dashboardComponent);
                break;

            case DashboardSeparator.TYPE:
                Hr hr = new Hr();
                if (((DashboardSeparator) dashboardComponent).getLargeGap()) {
                    hr.getStyle().set("margin", "2em 0 0.75em 0");
                }
                component = hr;
                break;

            case DashboardText.TYPE:
                component = new DashboardTextAdapter((DashboardText) dashboardComponent, titleText);
                break;

            case DashboardTitle.TYPE:
                DashboardTitle dashboardTitle = (DashboardTitle) dashboardComponent;
                H3 h3 = new H3(dashboardTitle.getText());
                h3.setClassName("dashboard-header");
                component = h3;
                break;

            case HorizontalPusher.TYPE:
                component = new Div();
                break;

            case DashboardImage.TYPE:
                component = new DashboardImageAdapter((DashboardImage) dashboardComponent);
                break;

            case DashboardImageUpload.TYPE:
                component = new DashboardImageUploadAdapter((DashboardImageUpload) dashboardComponent);
                break;

            case DashboardSwitch.TYPE:
                component = new DashboardSwitchAdapter((DashboardSwitch) dashboardComponent);
                break;

            case DashboardTextField.TYPE:
                component = new DashboardTextFieldAdapter((DashboardTextField) dashboardComponent);
                break;

            case DashboardMultiLineTextField.TYPE:
                component = new DashboardMultiLineTextFieldAdapter((DashboardMultiLineTextField) dashboardComponent);
                break;

            case DashboardNumberField.TYPE:
                component = new DashboardNumberFieldAdapter((DashboardNumberField) dashboardComponent);
                break;

            case DashboardFloatingNumberField.TYPE:
                component = new DashboardFloatingNumberFieldAdapter((DashboardFloatingNumberField) dashboardComponent);
                break;

            case DashboardComboBox.TYPE:
                component = new DashboardComboBoxAdapter(guildId, userId, (DashboardComboBox) dashboardComponent);
                break;

            case DashboardSelect.TYPE:
                component = new DashboardSelectAdapter((DashboardSelect) dashboardComponent);
                break;

            case DashboardDurationField.TYPE:
                component = new DashboardDurationFieldAdapter((DashboardDurationField) dashboardComponent);
                break;

            case DashboardGrid.TYPE:
                component = new DashboardGridAdapter((DashboardGrid) dashboardComponent);
                break;

            default:
                return null;
        }

        if (component instanceof HasStyle) {
            Style style = ((HasStyle) component).getStyle();
            dashboardComponent.getCssProperties().forEach(style::set);
        }

        component.setVisible(dashboardComponent.isVisible());
        return component;
    }

}
