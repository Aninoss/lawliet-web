package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.Scroller;
import dashboard.DashboardComponent;
import dashboard.container.DashboardListContainer;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardComponentConverter;

public class DashboardListContainerAdapter extends Scroller {

    public DashboardListContainerAdapter(DashboardListContainer listContainer, long guildId, long userId, ConfirmationDialog dialog) {
        Div content = new Div();
        content.addClassName("dashboard-list");

        for (DashboardComponent dashboardComponent : listContainer.getChildren()) {
            Component component = DashboardComponentConverter.convert(guildId, userId, dashboardComponent, dialog, false);
            if (component != null) {
                Div div = new Div(component);
                div.setWidthFull();
                div.addClassName("dashboard-list-item");
                content.add(div);
            }
        }

        setContent(content);
        setWidthFull();
        setScrollDirection(ScrollDirection.VERTICAL);
    }

}
