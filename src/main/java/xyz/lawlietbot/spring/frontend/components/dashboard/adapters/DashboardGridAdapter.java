package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import dashboard.DashboardComponent;
import dashboard.component.DashboardGrid;
import dashboard.data.GridRow;
import xyz.lawlietbot.spring.frontend.components.dashboard.DashboardAdapter;

import java.util.Arrays;

public class DashboardGridAdapter extends FlexLayout implements DashboardAdapter<DashboardGrid> {

    private DashboardGrid dashboardGrid;
    private final Grid<GridRow> grid = new Grid<>(GridRow.class, false);

    public DashboardGridAdapter(DashboardGrid dashboardGrid) {
        setFlexDirection(FlexDirection.COLUMN);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        update(dashboardGrid);
    }

    @Override
    public void update(DashboardGrid dashboardGrid) {
        DashboardComponent previousDashboardComponent = this.dashboardGrid;
        this.dashboardGrid = dashboardGrid;
        if (dashboardComponentsAreEqual(previousDashboardComponent, dashboardGrid)) {
            return;
        }

        grid.setHeightByRows(dashboardGrid.getRows().size() <= 10);
        grid.setItems(dashboardGrid.getRows());

        removeAll();
        if (dashboardGrid.getRows().isEmpty()) {
            Span span = new Span(getTranslation("dash.norows"));
            span.getStyle().set("text-align", "center");
            add(span);
        } else {
            add(generateGrid(dashboardGrid));
        }
    }

    private Component generateGrid(DashboardGrid dashboardGrid) {
        grid.removeAllColumns();
        String[] header = dashboardGrid.getHeader();
        for (int i = 0; i < header.length; i++) {
            int finalI = i;
            grid.addColumn(row -> row.getRowValue(finalI))
                    .setHeader(header[i])
                    .setAutoWidth(true);
        }
        String rowButton = dashboardGrid.getRowButton();
        if (!rowButton.isEmpty()) {
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

    @Override
    public boolean equalsType(DashboardComponent dashboardComponent) {
        if (!(dashboardComponent instanceof DashboardGrid)) {
            return false;
        }

        DashboardGrid dashboardGrid = (DashboardGrid) dashboardComponent;
        return Arrays.equals(this.dashboardGrid.getHeader(), dashboardGrid.getHeader());
    }

}
