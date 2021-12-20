package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import java.util.ArrayList;
import java.util.HashSet;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import dashboard.component.DashboardDiscordEntitySelection;
import dashboard.data.DiscordEntity;
import org.vaadin.gatanaso.MultiselectComboBox;
import xyz.lawlietbot.spring.syncserver.SendEvent;

public class DashboardDiscordEntitySelectionAdapter extends Div {

    public DashboardDiscordEntitySelectionAdapter(long guildId, long userId, DashboardDiscordEntitySelection dashboardDiscordEntitySelection) {
        if (dashboardDiscordEntitySelection.getMax() > 1) {
            MultiselectComboBox<DiscordEntity> multiselectComboBox = new MultiselectComboBox<>();
            multiselectComboBox.setWidthFull();
            multiselectComboBox.setPlaceholder(getTranslation("dash.select." + dashboardDiscordEntitySelection.getDataType().name(), true));
            multiselectComboBox.setLabel(dashboardDiscordEntitySelection.getLabel());
            multiselectComboBox.setClearButtonVisible(true);
            multiselectComboBox.setItemLabelGenerator(DiscordEntity::getName);
            multiselectComboBox.setRenderer(new ComponentRenderer<>(discordEntity -> new Text(discordEntity.getName())));
            multiselectComboBox.setDataProvider(generateDataProvider(guildId, userId, dashboardDiscordEntitySelection));
            multiselectComboBox.setOrdered(true);
            multiselectComboBox.setEnabled(dashboardDiscordEntitySelection.isEnabled());
            if (dashboardDiscordEntitySelection.getSelectedData().size() > 0) {
                multiselectComboBox.setValue(new HashSet<>(dashboardDiscordEntitySelection.getSelectedData()));
            }
            multiselectComboBox.addValueChangeListener(e -> {
                if (e.getValue().size() > dashboardDiscordEntitySelection.getMax()) {
                    multiselectComboBox.setValue(e.getOldValue());
                } else if (e.getOldValue().size() <= dashboardDiscordEntitySelection.getMax()) {
                    if (e.getValue().size() > e.getOldValue().size()) {
                        ArrayList<DiscordEntity> tempEntityList = new ArrayList<>(e.getValue());
                        tempEntityList.removeAll(e.getOldValue());
                        dashboardDiscordEntitySelection.triggerAdd(tempEntityList.get(0).getId());
                    } else if (e.getValue().size() < e.getOldValue().size()) {
                        ArrayList<DiscordEntity> tempEntityList = new ArrayList<>(e.getOldValue());
                        tempEntityList.removeAll(e.getValue());
                        dashboardDiscordEntitySelection.triggerRemove(tempEntityList.get(0).getId());
                    }
                }
            });
            add(multiselectComboBox);
        } else {
            ComboBox<DiscordEntity> comboBox = new ComboBox<>();
            comboBox.setWidthFull();
            comboBox.setPlaceholder(getTranslation("dash.select." + dashboardDiscordEntitySelection.getDataType().name(), false));
            comboBox.setLabel(dashboardDiscordEntitySelection.getLabel());
            comboBox.setClearButtonVisible(dashboardDiscordEntitySelection.canBeEmpty());
            comboBox.setItemLabelGenerator(DiscordEntity::getName);
            comboBox.setRenderer(new ComponentRenderer<>(discordEntity -> new Text(discordEntity.getName())));
            comboBox.setDataProvider(generateDataProvider(guildId, userId, dashboardDiscordEntitySelection));
            comboBox.setEnabled(dashboardDiscordEntitySelection.isEnabled());
            if (dashboardDiscordEntitySelection.getSelectedData().size() > 0) {
                comboBox.setValue(new ArrayList<>(dashboardDiscordEntitySelection.getSelectedData()).get(0));
            }
            comboBox.addValueChangeListener(e -> {
                if (e.getValue() != null) {
                    dashboardDiscordEntitySelection.triggerSet(e.getValue().getId());
                } else if (dashboardDiscordEntitySelection.canBeEmpty()) {
                    dashboardDiscordEntitySelection.triggerRemove(e.getOldValue().getId());
                } else {
                    comboBox.setValue(e.getOldValue());
                }
            });
            add(comboBox);
        }

    }

    private DataProvider<DiscordEntity, String> generateDataProvider(long guildId, long userId, DashboardDiscordEntitySelection dashboardDiscordEntitySelection) {
        return DataProvider.fromFilteringCallbacks(query -> {
            String filter = query.getFilter().orElse("");
            return SendEvent.sendDashboardListDiscordEntities(
                    dashboardDiscordEntitySelection.getDataType(),
                    guildId,
                    userId,
                    query.getOffset(),
                    query.getLimit(),
                    filter
            ).join().stream();
        }, query -> {
            String filter = query.getFilter().orElse("");
            return SendEvent.sendDashboardCountDiscordEntities(
                    dashboardDiscordEntitySelection.getDataType(),
                    guildId,
                    userId,
                    filter
            ).join().intValue();
        });
    }

}
