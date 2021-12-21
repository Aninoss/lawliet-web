package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import java.util.ArrayList;
import java.util.HashSet;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import dashboard.component.DashboardComboBox;
import dashboard.data.DiscordEntity;
import org.vaadin.gatanaso.MultiselectComboBox;
import xyz.lawlietbot.spring.syncserver.SendEvent;

public class DashboardComboBoxAdapter extends Div {

    public DashboardComboBoxAdapter(long guildId, long userId, DashboardComboBox dashboardComboBox) {
        if (dashboardComboBox.getMax() > 1) {
            MultiselectComboBox<DiscordEntity> multiselectComboBox = new MultiselectComboBox<>();
            multiselectComboBox.setWidthFull();
            multiselectComboBox.setPlaceholder(getTranslation("dash.select." + dashboardComboBox.getDataType().name(), true));
            multiselectComboBox.setLabel(dashboardComboBox.getLabel());
            multiselectComboBox.setItemLabelGenerator(DiscordEntity::getName);
            multiselectComboBox.setRenderer(new ComponentRenderer<>(discordEntity -> new Text(discordEntity.getName())));
            multiselectComboBox.setOrdered(true);
            multiselectComboBox.setAllowCustomValues(dashboardComboBox.getAllowCustomValues());
            multiselectComboBox.setEnabled(dashboardComboBox.isEnabled());
            if (dashboardComboBox.getDataType() == DashboardComboBox.DataType.CUSTOM) {
                multiselectComboBox.setItems(dashboardComboBox.getValues());
            } else {
                multiselectComboBox.setDataProvider(generateDataProvider(guildId, userId, dashboardComboBox));
            }
            if (dashboardComboBox.getSelectedValues().size() > 0) {
                multiselectComboBox.setValue(new HashSet<>(dashboardComboBox.getSelectedValues()));
            }
            multiselectComboBox.addValueChangeListener(e -> {
                if (e.getValue().size() > dashboardComboBox.getMax()) {
                    multiselectComboBox.setValue(e.getOldValue());
                } else if (e.getOldValue().size() <= dashboardComboBox.getMax()) {
                    if (e.getValue().size() > e.getOldValue().size()) {
                        ArrayList<DiscordEntity> tempEntityList = new ArrayList<>(e.getValue());
                        tempEntityList.removeAll(e.getOldValue());
                        dashboardComboBox.triggerAdd(tempEntityList.get(0).getId());
                    } else if (e.getValue().size() < e.getOldValue().size()) {
                        ArrayList<DiscordEntity> tempEntityList = new ArrayList<>(e.getOldValue());
                        tempEntityList.removeAll(e.getValue());
                        dashboardComboBox.triggerRemove(tempEntityList.get(0).getId());
                    }
                }
            });
            multiselectComboBox.addCustomValuesSetListener(e -> {
                if (multiselectComboBox.getSelectedItems().size() < dashboardComboBox.getMax()) {
                    multiselectComboBox.select(new DiscordEntity(e.getDetail(), e.getDetail()));
                }
            });
            add(multiselectComboBox);
        } else {
            ComboBox<DiscordEntity> comboBox = new ComboBox<>();
            comboBox.setWidthFull();
            comboBox.setPlaceholder(getTranslation("dash.select." + dashboardComboBox.getDataType().name(), false));
            comboBox.setLabel(dashboardComboBox.getLabel());
            comboBox.setClearButtonVisible(dashboardComboBox.getCanBeEmpty());
            comboBox.setItemLabelGenerator(DiscordEntity::getName);
            comboBox.setRenderer(new ComponentRenderer<>(discordEntity -> new Text(discordEntity.getName())));
            comboBox.setAllowCustomValue(dashboardComboBox.getAllowCustomValues());
            comboBox.setEnabled(dashboardComboBox.isEnabled());
            if (dashboardComboBox.getDataType() == DashboardComboBox.DataType.CUSTOM) {
                comboBox.setItems(dashboardComboBox.getValues());
            } else {
                comboBox.setDataProvider(generateDataProvider(guildId, userId, dashboardComboBox));
            }
            if (dashboardComboBox.getSelectedValues().size() > 0) {
                comboBox.setValue(new ArrayList<>(dashboardComboBox.getSelectedValues()).get(0));
            }
            comboBox.addValueChangeListener(e -> {
                if (e.getValue() != null) {
                    dashboardComboBox.triggerSet(e.getValue().getId());
                } else if (dashboardComboBox.getCanBeEmpty()) {
                    dashboardComboBox.triggerSet(null);
                } else {
                    comboBox.setValue(e.getOldValue());
                }
            });
            add(comboBox);
        }

    }

    private DataProvider<DiscordEntity, String> generateDataProvider(long guildId, long userId, DashboardComboBox dashboardComboBox) {
        return DataProvider.fromFilteringCallbacks(query -> {
            String filter = query.getFilter().orElse("");
            return SendEvent.sendDashboardListDiscordEntities(
                    dashboardComboBox.getDataType(),
                    guildId,
                    userId,
                    query.getOffset(),
                    query.getLimit(),
                    filter
            ).join().stream();
        }, query -> {
            String filter = query.getFilter().orElse("");
            return SendEvent.sendDashboardCountDiscordEntities(
                    dashboardComboBox.getDataType(),
                    guildId,
                    userId,
                    filter
            ).join().intValue();
        });
    }

}
