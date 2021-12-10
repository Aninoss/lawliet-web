package xyz.lawlietbot.spring.frontend.components.dashboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.dom.Style;
import dashboard.DashboardComponent;
import dashboard.component.ActionComponent;
import dashboard.component.DashboardButton;
import dashboard.component.DashboardText;
import dashboard.container.HorizontalContainer;
import dashboard.container.HorizontalPusher;
import dashboard.container.VerticalContainer;
import xyz.lawlietbot.spring.frontend.components.dashboard.adapters.DashboardButtonAdapter;
import xyz.lawlietbot.spring.frontend.components.dashboard.adapters.HorizontalContainerAdapter;
import xyz.lawlietbot.spring.frontend.components.dashboard.adapters.VerticalContainerAdapter;

public class DashboardComponentConverter {

    public static Component convert(DashboardComponent dashboardComponent) {
        Component component;
        switch (dashboardComponent.getType()) {
            case HorizontalContainer.TYPE:
                component = new HorizontalContainerAdapter((HorizontalContainer) dashboardComponent);
                break;

            case VerticalContainer.TYPE:
                component = new VerticalContainerAdapter((VerticalContainer) dashboardComponent);
                break;

            case DashboardText.TYPE:
                component = convertText((DashboardText) dashboardComponent);
                break;

            case DashboardButton.TYPE:
                component = new DashboardButtonAdapter((DashboardButton) dashboardComponent);
                break;

            case HorizontalPusher.TYPE:
                component = new Div();
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

    private static Component convertText(DashboardText dashboardText) {
        return dashboardText.getStyle() == DashboardText.Style.TITLE
                ? new H3(dashboardText.getText())
                : new Div(new Text(dashboardText.getText()));
    }

}
