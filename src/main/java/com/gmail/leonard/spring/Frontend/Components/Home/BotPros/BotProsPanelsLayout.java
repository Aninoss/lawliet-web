package com.gmail.leonard.spring.Frontend.Components.Home.BotPros;

import com.github.appreciated.css.grid.GridLayoutComponent;
import com.github.appreciated.css.grid.sizes.Flex;
import com.github.appreciated.css.grid.sizes.Length;
import com.github.appreciated.css.grid.sizes.MinMax;
import com.github.appreciated.css.grid.sizes.Repeat;
import com.github.appreciated.layout.FlexibleGridLayout;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.CustomButton;
import com.gmail.leonard.spring.Frontend.Components.InfoCard;
import com.gmail.leonard.spring.Frontend.Views.CommandsView;
import com.gmail.leonard.spring.Frontend.Views.DashboardServerView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Article;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class BotProsPanelsLayout extends VerticalLayout {

    public BotProsPanelsLayout(UIData uiData) {

        int n = 9;

        Article[] infoCards = new Article[n];
        Icon[] icons = {
                VaadinIcon.TROPHY.create(),
                VaadinIcon.STAR.create(),
                VaadinIcon.BOAT.create(),
                VaadinIcon.PIN.create(),
                VaadinIcon.VOLUME.create(),
                VaadinIcon.TAG.create(),
                VaadinIcon.GROUP.create(),
                VaadinIcon.GAMEPAD.create(),
                VaadinIcon.LINK.create()
        };

        for(int i = 0; i < n; i++) {
            CustomButton customButton = null;
            WIP wip = null;
            if (i == 1) {
                if (!uiData.isLite()) {
                    customButton = new CustomButton(getTranslation("bot.card.1.button"), click -> UI.getCurrent().navigate(DashboardServerView.class));
                    customButton.setWidthFull();
                }
                wip = new WIP();
            }

            if (i == 8) {
                customButton = new CustomButton(getTranslation("bot.card.8.button"), VaadinIcon.ARROW_RIGHT.create(), click -> UI.getCurrent().navigate(CommandsView.class));
                customButton.setWidthFull();
                customButton.setIconAfterText(true);
            }

            infoCards[i] = new Article(new InfoCard(
                    getTranslation("bot.card." + i + ".title"),
                    getTranslation("bot.card." + i + ".subtitle"),
                    getTranslation("bot.card." + i + ".desc"),
                    icons[i],
                    wip,
                    customButton
            ));
        }

        FlexibleGridLayout layout = new FlexibleGridLayout()
                .withColumns(Repeat.RepeatMode.AUTO_FILL, new MinMax(new Length("270px"), new Flex(1)))
                .withItems(infoCards)
                .withPadding(false)
                .withSpacing(true)
                .withAutoFlow(GridLayoutComponent.AutoFlow.ROW_DENSE)
                .withOverflow(GridLayoutComponent.Overflow.AUTO);

        layout.setSizeFull();
        setSizeFull();
        setPadding(false);
        add(layout);

    }
}
