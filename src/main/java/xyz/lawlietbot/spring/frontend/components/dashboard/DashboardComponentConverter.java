package xyz.lawlietbot.spring.frontend.components.dashboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.dom.Style;
import dashboard.DashboardComponent;
import dashboard.component.*;
import dashboard.container.HorizontalContainer;
import dashboard.container.HorizontalPusher;
import dashboard.container.VerticalContainer;
import xyz.lawlietbot.spring.frontend.components.dashboard.adapters.*;

public class DashboardComponentConverter {

    public static Component convert(long guildId, long userId, DashboardComponent dashboardComponent) {
        Component component;
        switch (dashboardComponent.getType()) {
            case HorizontalContainer.TYPE:
                component = new HorizontalContainerAdapter(guildId, userId, (HorizontalContainer) dashboardComponent);
                break;

            case VerticalContainer.TYPE:
                component = new VerticalContainerAdapter(guildId, userId, (VerticalContainer) dashboardComponent);
                break;

            case DashboardButton.TYPE:
                component = new DashboardButtonAdapter((DashboardButton) dashboardComponent);
                break;

            case DashboardSeparator.TYPE:
                component = new Hr();
                break;

            case DashboardText.TYPE:
                DashboardText dashboardText = (DashboardText) dashboardComponent;
                component = new Div(new Text(dashboardText.getText()));
                break;

            case DashboardTitle.TYPE:
                DashboardTitle dashboardTitle = (DashboardTitle) dashboardComponent;
                component = new H3(dashboardTitle.getText());
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

            case DashboardDiscordEntitySelection.TYPE:
                component = new DashboardDiscordEntitySelectionAdapter(guildId, userId, (DashboardDiscordEntitySelection) dashboardComponent);
                break;

            default:
                return null;
        }

        if (component instanceof HasStyle) {
            Style style = ((HasStyle) component).getStyle();
            dashboardComponent.getCssProperties().forEach(style::set);
        }

        if (dashboardComponent instanceof ActionComponent<?> && component instanceof HasEnabled) {
            boolean enabled = ((ActionComponent<?>) dashboardComponent).isEnabled();
            ((HasEnabled) component).setEnabled(enabled);
        }

        component.setVisible(dashboardComponent.isVisible());
        return component;
    }

}
