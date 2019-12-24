package com.gmail.leonard.spring.Frontend.Components.Dashboard;

import com.github.appreciated.css.grid.GridLayoutComponent;
import com.github.appreciated.css.grid.sizes.Flex;
import com.github.appreciated.css.grid.sizes.Length;
import com.github.appreciated.css.grid.sizes.MinMax;
import com.github.appreciated.css.grid.sizes.Repeat;
import com.github.appreciated.layout.FlexibleGridLayout;
import com.gmail.leonard.spring.Backend.UserData.DiscordServerData;
import com.gmail.leonard.spring.Backend.UserData.ServerListData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.ComponentChanger;
import com.gmail.leonard.spring.Frontend.Components.CustomButton;
import com.gmail.leonard.spring.Frontend.Components.Home.BotPros.WIP;
import com.gmail.leonard.spring.Frontend.Components.InfoCard;
import com.gmail.leonard.spring.Frontend.Views.CommandsView;
import com.gmail.leonard.spring.Frontend.Views.DashboardServerView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Article;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;

public class DashboardServerListLayout extends VerticalLayout {

    public DashboardServerListLayout(ServerListData serverListData, DashboardServerView dashboardServerView) {
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
            serverCards[i].addClickListener(listener -> dashboardServerView.build(this, discordServerData));
        }

        FlexibleGridLayout layout = new FlexibleGridLayout()
                .withColumns(Repeat.RepeatMode.AUTO_FILL, new MinMax(new Length("160px"), new Flex(1)))
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
}
