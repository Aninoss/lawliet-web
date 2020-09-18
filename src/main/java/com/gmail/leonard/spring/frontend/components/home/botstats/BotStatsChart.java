package com.gmail.leonard.spring.frontend.components.home.botstats;

import com.gmail.leonard.spring.backend.StringUtil;
import com.gmail.leonard.spring.backend.serverstats.ServerStatsSlot;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;

import java.util.Arrays;

@CssImport(value = "./styles/charts.css", themeFor = "vaadin-chart", include = "vaadin-chart-default-theme")
public class BotStatsChart extends Div {

    public BotStatsChart(ServerStatsSlot[] slots) {
        final Chart chart = new Chart(ChartType.AREASPLINE);
        setWidthFull();
        getStyle().set("border-radius", "5px")
            .set("background", "var(--lumo-base-color)")
            .set("padding-top", "16px");

        final Configuration configuration = chart.getConfiguration();
        configuration.setTitle(getTranslation("bot.stat.servercount"));

        XAxis xAxis = configuration.getxAxis();
        xAxis.setCategories(getMonths(slots));
        xAxis.setTickmarkPlacement(TickmarkPlacement.ON);

        YAxis yAxis = configuration.getyAxis();
        yAxis.setTitle(getTranslation("bot.stat.servers"));

        configuration.getTooltip().setValueSuffix(" " + getTranslation("bot.stat.servers"));

        PlotOptionsArea plotOptionsArea = new PlotOptionsArea();
        plotOptionsArea.setStacking(Stacking.NORMAL);
        configuration.setPlotOptions(plotOptionsArea);

        configuration.addSeries(new ListSeries(getTranslation("bot.stat.servers"), getValues(slots)));
        add(chart);
    }

    private String[] getMonths(ServerStatsSlot[] slots) {
        String[] months = new String[slots.length - 1];
        for (int i = 0; i < slots.length - 1; i++) {
            ServerStatsSlot slot = slots[i];
            months[i] = getTranslation("bot.stat.month" + slot.getMonth()) + " " + slot.getYear();
        }

        return months;
    }

    private Integer[] getValues(ServerStatsSlot[] slots) {
        Integer[] values = new Integer[slots.length - 1];
        for (int i = 0; i < slots.length - 1; i++) {
            ServerStatsSlot slot = slots[i];
            values[i] = slot.getServerCount();
        }

        return values;
    }

}
