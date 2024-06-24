package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.Scroller;
import dashboard.DashboardComponent;
import dashboard.container.DashboardListContainer;
import xyz.lawlietbot.spring.backend.FileCache;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardAdapter;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardComponentConverter;

public class DashboardListContainerAdapter extends Scroller implements DashboardAdapter<DashboardListContainer> {

    private final long guildId;
    private final long userId;
    private final ConfirmationDialog dialog;
    private final Div content = new Div();
    private final FileCache fileCache;

    public DashboardListContainerAdapter(DashboardListContainer listContainer, long guildId, long userId, ConfirmationDialog dialog, FileCache fileCache) {
        this.guildId = guildId;
        this.userId = userId;
        this.dialog = dialog;
        this.fileCache = fileCache;

        content.addClassName("dashboard-list");
        setContent(content);
        setWidthFull();
        setScrollDirection(ScrollDirection.VERTICAL);

        update(listContainer);
    }

    @Override
    public void update(DashboardListContainer listContainer) {
        content.removeAll();
        for (DashboardComponent dashboardComponent : listContainer.getChildren()) {
            Component component = DashboardComponentConverter.convert(guildId, userId, dashboardComponent, dialog, fileCache);
            if (component != null) {
                Div div = new Div(component);
                div.setWidthFull();
                div.addClassName("dashboard-list-item");
                content.add(div);
            }
        }
    }

    @Override
    public boolean equalsType(DashboardComponent dashboardComponent) {
        return dashboardComponent instanceof DashboardListContainer;
    }

}
