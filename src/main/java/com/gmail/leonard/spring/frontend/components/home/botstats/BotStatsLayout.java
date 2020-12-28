package com.gmail.leonard.spring.frontend.components.home.botstats;

import com.gmail.leonard.spring.backend.util.StringUtil;
import com.gmail.leonard.spring.backend.serverstats.ServerStatsBean;
import com.gmail.leonard.spring.backend.serverstats.ServerStatsContainer;
import com.gmail.leonard.spring.frontend.Styles;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class BotStatsLayout extends VerticalLayout {

    private final static Logger LOGGER = LoggerFactory.getLogger(BotStatsLayout.class);

    private final VerticalLayout mainLayout = new VerticalLayout();

    public BotStatsLayout() {
        getStyle().set("padding-bottom", "48px");
        setPadding(false);
        mainLayout.addClassName(Styles.APP_WIDTH);
        getStyle().set("background", "var(--lumo-secondary)");

        try {
            ServerStatsBean bean = ServerStatsContainer.getInstance().getBean();
            Optional<Long> serverSizeOpt = bean.getServers();

            if (serverSizeOpt.isPresent()) addTitle(roundDownStat(serverSizeOpt.get()));
            else getStyle().set("padding-top", "3em");

            mainLayout.add(new BotStatsChart(bean.getSlots()));
        } catch (Throwable e) {
            LOGGER.error("Error in fetching bot server stats", e);
        }

        add(mainLayout);
    }

    private long roundDownStat(long value) {
        return value / 1000 * 1000;
    }

    private void addTitle(long serverCount) {
        H2 title = new H2(getTranslation("bot.stat.title", StringUtil.numToString(serverCount)));
        title.getStyle().set("margin-top", "2em");
        title.setWidthFull();
        title.addClassName(Styles.CENTER_TEXT);
        mainLayout.add(title);
    }

}
