package com.gmail.leonard.spring.Frontend.Components.Dashboard;

import com.github.appreciated.css.grid.GridLayoutComponent;
import com.github.appreciated.css.grid.sizes.Flex;
import com.github.appreciated.css.grid.sizes.Length;
import com.github.appreciated.css.grid.sizes.MinMax;
import com.github.appreciated.css.grid.sizes.Repeat;
import com.github.appreciated.layout.FlexibleGridLayout;
import com.gmail.leonard.spring.Backend.UserData.DiscordServerData;
import com.gmail.leonard.spring.Backend.UserData.ServerListData;
import com.gmail.leonard.spring.Frontend.Views.DashboardView;
import com.vaadin.flow.component.html.Article;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.Optional;

public class DashboardServerListLayout extends VerticalLayout {

    ArrayList<DashboardServerClickListener> serverClickListeners = new ArrayList<>();

    public DashboardServerListLayout(ServerListData serverListData) {
        Article[] serverCards = new Article[serverListData.size()];

        for(int i = 0; i < serverListData.size(); i++) {
            DiscordServerData discordServerData = serverListData.getServers().get(i);

            String iconAlt = VaadinServletService.getCurrent()
                    .resolveResource("/styles/img/empty_servericon.png",
                            VaadinSession.getCurrent().getBrowser());

            DashboardServerCard dashboardServerCard = new DashboardServerCard(
                    discordServerData.getIcon().orElse(iconAlt),
                    discordServerData.getName()
            );
            serverCards[i] = new Article(dashboardServerCard);
            serverCards[i].addClassName("dashboard-card");
            serverCards[i].addClickListener(click -> serverClickListeners.forEach(listener -> listener.onServerClick(discordServerData.getId())));
        }

        FlexibleGridLayout layout = new FlexibleGridLayout()
                .withColumns(Repeat.RepeatMode.AUTO_FILL, new MinMax(new Length("130px"), new Flex(0.5)))
                .withItems(serverCards)
                .withPadding(false)
                .withSpacing(true)
                .withAutoFlow(GridLayoutComponent.AutoFlow.ROW_DENSE)
                .withOverflow(GridLayoutComponent.Overflow.AUTO);

        layout.setSizeFull();
        layout.getStyle().set("overflow", "visible");
        setSizeFull();
        setPadding(false);
        add(layout);
    }

    public void addServerClickListener(DashboardServerClickListener listener) { serverClickListeners.add(listener); }

    public void removeServerClickListener(DashboardServerClickListener listener) { serverClickListeners.remove(listener); }

}
