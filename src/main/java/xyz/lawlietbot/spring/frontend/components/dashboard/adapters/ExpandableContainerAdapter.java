package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import dashboard.DashboardComponent;
import dashboard.container.ExpandableContainer;
import xyz.lawlietbot.spring.backend.FileCache;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardAdapter;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardComponentConverter;

public class ExpandableContainerAdapter extends Details implements DashboardAdapter<ExpandableContainer> {

    public ExpandableContainerAdapter(ExpandableContainer expandableContainer, long guildId, long userId, ConfirmationDialog dialog, FileCache fileCache) {
        addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);

        Component header = DashboardComponentConverter.convert(guildId, userId, expandableContainer.getChildren().get(0), dialog, fileCache);
        setSummary(header);

        Component bodyHidden = DashboardComponentConverter.convert(guildId, userId, expandableContainer.getChildren().get(1), dialog, fileCache);
        setContent(bodyHidden);
    }

    @Override
    public void update(ExpandableContainer expandableContainer) {
        ((DashboardAdapter) getSummary()).update(expandableContainer.getChildren().get(0));
        ((DashboardAdapter) getContent().findFirst().get()).update(expandableContainer.getChildren().get(1));
    }

    @Override
    public boolean equalsType(DashboardComponent dashboardComponent) {
        return dashboardComponent instanceof ExpandableContainer;
    }

}
