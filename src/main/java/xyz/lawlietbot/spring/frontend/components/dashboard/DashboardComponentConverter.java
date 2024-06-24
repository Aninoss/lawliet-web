package xyz.lawlietbot.spring.frontend.components.dashboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasOrderedComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.dom.Style;
import dashboard.DashboardComponent;
import dashboard.component.*;
import dashboard.container.*;
import xyz.lawlietbot.spring.backend.FileCache;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.dashboard.adapters.*;

import java.util.List;
import java.util.stream.Collectors;

public class DashboardComponentConverter {

    public static Component convert(long guildId, long userId, DashboardComponent dashboardComponent, ConfirmationDialog dialog, FileCache fileCache) {
        Component component;
        switch (dashboardComponent.getType()) {
            case HorizontalContainer.TYPE:
                component = new HorizontalContainerAdapter((HorizontalContainer) dashboardComponent, guildId, userId, dialog, fileCache);
                break;

            case VerticalContainer.TYPE:
                component = new VerticalContainerAdapter((VerticalContainer) dashboardComponent, guildId, userId, dialog, fileCache);
                break;

            case ExpandableContainer.TYPE:
                component = new ExpandableContainerAdapter((ExpandableContainer) dashboardComponent, guildId, userId, dialog, fileCache);
                break;

            case DashboardListContainer.TYPE:
                component = new DashboardListContainerAdapter((DashboardListContainer) dashboardComponent, guildId, userId, dialog, fileCache);
                break;

            case DashboardButton.TYPE:
                component = new DashboardButtonAdapter((DashboardButton) dashboardComponent);
                break;

            case DashboardSeparator.TYPE:
                component = new DashboardSeparatorAdapter((DashboardSeparator) dashboardComponent);
                break;

            case DashboardText.TYPE:
                component = new DashboardTextAdapter((DashboardText) dashboardComponent);
                break;

            case DashboardTitle.TYPE:
                component = new DashboardTitleAdapter((DashboardTitle) dashboardComponent);
                break;

            case HorizontalPusher.TYPE:
                component = new HorizontalPusherAdapter();
                break;

            case DashboardImage.TYPE:
                component = new DashboardImageAdapter((DashboardImage) dashboardComponent);
                break;

            case DashboardImageUpload.TYPE:
                component = new DashboardImageUploadAdapter((DashboardImageUpload) dashboardComponent, fileCache);
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

        component.setVisible(dashboardComponent.isVisible());
        if (component instanceof HasEnabled && dashboardComponent instanceof ActionComponent) {
            ((HasEnabled) component).setEnabled(((ActionComponent<?>) dashboardComponent).isEnabled());
        }

        Style style = ((HasStyle) component).getStyle();
        dashboardComponent.getCssProperties().forEach(style::set);
        return component;
    }

    public static void addAndRemove(HasOrderedComponents<?> layout, DashboardContainer dashboardContainer, long guildId, long userId, ConfirmationDialog dialog, FileCache fileCache) {
        /* removed components */
        List<Component> componentChildren = layout.getChildren().collect(Collectors.toList());
        for (int i = 0; i < componentChildren.size(); i++) {
            Component component = componentChildren.get(i);
            DashboardAdapter<?> dashboardAdapter = (DashboardAdapter<?>) component;

            if (i < dashboardContainer.getChildren().size()) {
                DashboardComponent dashboardComponent = dashboardContainer.getChildren().get(i);
                if (!dashboardAdapter.equalsType(dashboardComponent)) {
                    layout.remove(component);
                }
            } else {
                layout.remove(component);
            }
        }

        /* added components */
        for (int i = 0; i < dashboardContainer.getChildren().size(); i++) {
            DashboardComponent dashboardComponent = dashboardContainer.getChildren().get(i);

            if (i < layout.getComponentCount()) {
                DashboardAdapter<?> dashboardAdapter = (DashboardAdapter<?>) layout.getComponentAt(i);
                if (!dashboardAdapter.equalsType(dashboardComponent)) {
                    Component newComponent = convert(guildId, userId, dashboardComponent, dialog, fileCache);
                    layout.addComponentAtIndex(i, newComponent);
                }
            } else {
                Component newComponent = convert(guildId, userId, dashboardComponent, dialog, fileCache);
                layout.add(newComponent);
            }
        }

        /* adjust general properties */
        for (int i = 0; i < layout.getComponentCount(); i++) {
            Component component = layout.getComponentAt(i);
            DashboardComponent dashboardComponent = dashboardContainer.getChildren().get(i);

            component.setVisible(dashboardComponent.isVisible());
            if (component instanceof HasEnabled && dashboardComponent instanceof ActionComponent) {
                ((HasEnabled) component).setEnabled(((ActionComponent<?>) dashboardComponent).isEnabled());
            }

            Style style = ((HasStyle) component).getStyle();
            style.remove("margin-left");
            style.remove("margin-top");
            style.remove("margin-right");
            style.remove("margin-bottom");
            dashboardComponent.getCssProperties().forEach(style::set);
        }
    }

}
