package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import dashboard.container.ExpandableContainer;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardComponentConverter;

public class ExpandableContainerAdapter extends Details {

    public ExpandableContainerAdapter(ExpandableContainer expandableContainer, long guildId, long userId, ConfirmationDialog dialog) {
        addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);

        Component header = DashboardComponentConverter.convert(guildId, userId, expandableContainer.getChildren().get(0), dialog);
        setSummary(header);

        Component bodyHidden = DashboardComponentConverter.convert(guildId, userId, expandableContainer.getChildren().get(1), dialog);
        setContent(bodyHidden);
    }

}
