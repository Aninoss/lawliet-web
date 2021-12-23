package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import dashboard.component.DashboardSelect;
import dashboard.data.DiscordEntity;

public class DashboardSelectAdapter extends Div {

    public DashboardSelectAdapter(DashboardSelect dashboardSelect) {
        Select<DiscordEntity> select = new Select<>();
        select.setWidthFull();
        select.setEnabled(dashboardSelect.isEnabled());
        select.getStyle().set("margin-top", "-16px");
        select.setPlaceholder(getTranslation("dash.select.empty"));
        select.setLabel(dashboardSelect.getLabel());
        select.setEmptySelectionAllowed(dashboardSelect.getCanBeEmpty());
        select.setRenderer(new ComponentRenderer<>(discordEntity -> new Text(discordEntity.getName())));
        select.setItems(dashboardSelect.getValues());
        select.setEmptySelectionCaption(getTranslation("dash.select.empty"));
        if (dashboardSelect.getSelectedValue() != null) {
            select.setValue(dashboardSelect.getSelectedValue());
        }
        select.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                dashboardSelect.trigger(e.getValue().getId());
            } else {
                dashboardSelect.trigger(null);
            }
        });
        add(select);
    }

}
