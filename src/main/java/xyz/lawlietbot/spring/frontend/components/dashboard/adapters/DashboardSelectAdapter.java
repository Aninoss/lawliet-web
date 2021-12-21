package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import dashboard.component.DashboardSelect;
import dashboard.data.DiscordEntity;

public class DashboardSelectAdapter extends Select<DiscordEntity> {

    public DashboardSelectAdapter(DashboardSelect dashboardSelect) {
        setPlaceholder(getTranslation("dash.select.empty"));
        setLabel(dashboardSelect.getLabel());
        setEmptySelectionAllowed(dashboardSelect.getCanBeEmpty());
        setRenderer(new ComponentRenderer<>(discordEntity -> new Text(discordEntity.getName())));
        setItems(dashboardSelect.getValues());
        setEmptySelectionCaption(getTranslation("dash.select.empty"));
        if (dashboardSelect.getSelectedValue() != null) {
            setValue(dashboardSelect.getSelectedValue());
        }
        addValueChangeListener(e -> {
            if (e.getValue() != null) {
                dashboardSelect.trigger(e.getValue().getId());
            } else {
                dashboardSelect.trigger(null);
            }
        });
    }

}
