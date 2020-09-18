package com.gmail.leonard.spring.frontend.components.home.botstats;

import com.gmail.leonard.spring.Application;
import com.gmail.leonard.spring.backend.StringUtil;
import com.gmail.leonard.spring.backend.serverstats.ServerStatsContainer;
import com.gmail.leonard.spring.backend.serverstats.ServerStatsSlot;
import com.gmail.leonard.spring.frontend.Styles;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotStatsLayout extends VerticalLayout {

    private final static Logger LOGGER = LoggerFactory.getLogger(BotStatsLayout.class);

    private final VerticalLayout mainLayout = new VerticalLayout();

    public BotStatsLayout() {
        getStyle().set("padding-bottom", "48px");
        setPadding(false);
        mainLayout.addClassName(Styles.APP_WIDTH);
        getStyle().set("background", "var(--lumo-secondary)");

        try {
            ServerStatsSlot[] slots = ServerStatsContainer.getInstance().getSlots();
            addTitle(slots[slots.length - 1].getServerCount() / 1000 * 1000);
            mainLayout.add(new BotStatsChart(slots));
        } catch (Throwable e) {
            LOGGER.error("Error in fetching bot server stats", e);
        }

        add(mainLayout);
    }

    private void addTitle(int serverCount) {
        H2 title = new H2(getTranslation("bot.stat.title", StringUtil.numToString(getLocale(), serverCount)));
        title.getStyle().set("margin-top", "2em");
        title.setWidthFull();
        title.addClassName(Styles.CENTER_TEXT);
        mainLayout.add(title);
    }

}
