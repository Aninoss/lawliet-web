package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import dashboard.component.DashboardGrid;
import dashboard.data.GridRow;

public class DashboardGridAdapter extends FlexLayout {

    public DashboardGridAdapter(DashboardGrid dashboardGrid) {
        setFlexDirection(FlexDirection.COLUMN);
        add(generateGrid(dashboardGrid));
        if (dashboardGrid.isWithAddButton()) {
            add(generateAddButton(dashboardGrid));
        }
    }

    private Component generateGrid(DashboardGrid dashboardGrid) {
        Grid<GridRow> grid = new Grid<>(GridRow.class, false);
        grid.setEnabled(dashboardGrid.isEnabled());
        grid.setHeightByRows(true);
        grid.setItems(dashboardGrid.getRows());
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        String[] header = dashboardGrid.getHeader();
        for (int i = 0; i < header.length; i++) {
            int finalI = i;
            grid.addColumn(row -> row.getRowValue(finalI))
                    .setHeader(header[i])
                    .setAutoWidth(true);
        }
        if (dashboardGrid.isWithEditButton()) {
            grid.addComponentColumn(gridRow -> generateEditButton(dashboardGrid, gridRow))
                    .setTextAlign(ColumnTextAlign.END)
                    .setWidth("50px");
        }
        return grid;
    }

    private Component generateEditButton(DashboardGrid dashboardGrid, GridRow gridRow) {
        Button editButton = new Button(VaadinIcon.PENCIL.create());
        editButton.setEnabled(dashboardGrid.isEnabled());
        editButton.addClickListener(e -> dashboardGrid.triggerEdit(gridRow.getId()));
        return editButton;
    }

    private Component generateAddButton(DashboardGrid dashboardGrid) {
        Button addButton = new Button(getTranslation("dash.grid.add"), VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.setEnabled(dashboardGrid.isEnabled());
        addButton.setWidthFull();
        addButton.addClickListener(e -> dashboardGrid.triggerAdd());
        addButton.getStyle().set("margin-top", "16px");
        return addButton;
    }

}
