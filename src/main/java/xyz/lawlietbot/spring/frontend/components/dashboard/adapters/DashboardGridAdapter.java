package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import dashboard.component.DashboardGrid;
import dashboard.data.GridRow;

public class DashboardGridAdapter extends FlexLayout {

    public DashboardGridAdapter(DashboardGrid dashboardGrid) {
        setFlexDirection(FlexDirection.COLUMN);
        add(generateGrid(dashboardGrid));
    }

    private Component generateGrid(DashboardGrid dashboardGrid) {
        Grid<GridRow> grid = new Grid<>(GridRow.class, false);
        grid.setEnabled(dashboardGrid.isEnabled());
        grid.setHeightByRows(dashboardGrid.getRows().size() <= 10);
        grid.setItems(dashboardGrid.getRows());
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        String[] header = dashboardGrid.getHeader();
        for (int i = 0; i < header.length; i++) {
            int finalI = i;
            grid.addColumn(row -> row.getRowValue(finalI))
                    .setHeader(header[i])
                    .setAutoWidth(true);
        }
        String rowButton = dashboardGrid.getRowButton();
        if (rowButton.length() > 0) {
            grid.addComponentColumn(gridRow -> generateRowButton(dashboardGrid, gridRow, rowButton))
                    .setTextAlign(ColumnTextAlign.END)
                    .setAutoWidth(true);
        }
        return grid;
    }

    private Component generateRowButton(DashboardGrid dashboardGrid, GridRow gridRow, String buttonText) {
        Button rowButton = new Button(buttonText);
        rowButton.setEnabled(dashboardGrid.isEnabled());
        rowButton.addClickListener(e -> dashboardGrid.triggerRow(gridRow.getId()));
        rowButton.setWidthFull();
        return rowButton;
    }

}
