package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import dashboard.DashboardComponent;
import dashboard.component.DashboardSelect;
import dashboard.data.DiscordEntity;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardAdapter;

import java.util.Objects;

public class DashboardSelectAdapter extends Div implements DashboardAdapter<DashboardSelect> {

    private DashboardSelect dashboardSelect;
    private final Select<DiscordEntity> select = new Select<>();

    public DashboardSelectAdapter(DashboardSelect dashboardSelect) {
        select.setWidthFull();
        select.getStyle().set("margin-top", "-16px");
        select.setPlaceholder(getTranslation("dash.select.empty"));
        select.setLabel(dashboardSelect.getLabel());
        select.setRenderer(new ComponentRenderer<>(discordEntity -> new Text(discordEntity.getName())));
        select.setEmptySelectionCaption(getTranslation("dash.select.empty"));
        select.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                this.dashboardSelect.trigger(e.getValue().getId());
            } else {
                this.dashboardSelect.trigger(null);
            }
        });
        add(select);

        update(dashboardSelect);
    }

    @Override
    public void update(DashboardSelect dashboardSelect) {
        DashboardComponent previousDashboardComponent = this.dashboardSelect;
        this.dashboardSelect = dashboardSelect;
        if (dashboardComponentsAreEqual(previousDashboardComponent, dashboardSelect) && Objects.equals(select.getValue(), dashboardSelect.getSelectedValue())) {
            return;
        }

        select.setEmptySelectionAllowed(dashboardSelect.getCanBeEmpty());
        select.setItems(dashboardSelect.getValues());
        select.setValue(dashboardSelect.getSelectedValue());
    }

    @Override
    public boolean equalsType(DashboardComponent dashboardComponent) {
        if (!(dashboardComponent instanceof DashboardSelect)) {
            return false;
        }

        DashboardSelect dashboardSelect = (DashboardSelect) dashboardComponent;
        return Objects.equals(this.dashboardSelect.getLabel(), dashboardSelect.getLabel());
    }

}
