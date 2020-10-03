package com.gmail.leonard.spring.frontend.components.home.botpros;

import com.github.appreciated.css.grid.GridLayoutComponent;
import com.github.appreciated.css.grid.sizes.Flex;
import com.github.appreciated.css.grid.sizes.Length;
import com.github.appreciated.css.grid.sizes.MinMax;
import com.github.appreciated.css.grid.sizes.Repeat;
import com.github.appreciated.layout.FlexibleGridLayout;
import com.gmail.leonard.spring.backend.userdata.UIData;
import com.gmail.leonard.spring.frontend.views.CommandsView;
import com.gmail.leonard.spring.frontend.views.DashboardView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Article;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;

public class BotProsPanelsLayout extends VerticalLayout {

    public BotProsPanelsLayout(UIData uiData) {
        BotProPanelInfo[] botProPanelInfos = getBotProPanelInfos(uiData);

        ArrayList<Article> articles = new ArrayList<>();
        for(int i = 0; i < botProPanelInfos.length && articles.size() < 9; i++) {
            BotProPanelInfo botProPanelInfo = botProPanelInfos[i];
            if (botProPanelInfo.isVisible()) {
                String id = botProPanelInfo.getId();
                articles.add(new Article(new BotProsInfoCard(
                        getTranslation("bot.card." + id + ".title"),
                        getTranslation("bot.card." + id + ".subtitle"),
                        getTranslation("bot.card." + id + ".desc"),
                        botProPanelInfo.getIcon(),
                        botProPanelInfo.getCharacterLimit(),
                        botProPanelInfo.getComponents()
                )));
            }
        }

        FlexibleGridLayout layout = new FlexibleGridLayout()
                .withColumns(Repeat.RepeatMode.AUTO_FILL, new MinMax(new Length("270px"), new Flex(1)))
                .withItems(articles.toArray(new Article[0]))
                .withPadding(false)
                .withSpacing(true)
                .withAutoFlow(GridLayoutComponent.AutoFlow.ROW_DENSE)
                .withOverflow(GridLayoutComponent.Overflow.AUTO);

        layout.getStyle().set("overflow", "hidden");

        layout.setSizeFull();
        setSizeFull();
        setPadding(false);
        add(layout);

    }

    private BotProPanelInfo[] getBotProPanelInfos(UIData uiData) {
        WIP wip = new WIP(getTranslation("bot.card.wip"));
        Button dashboardButton = null;
        if (!uiData.isLite()) {
            dashboardButton = new Button(getTranslation("bot.card.dashboard.button"), click -> UI.getCurrent().navigate(DashboardView.class));
            dashboardButton.setWidthFull();
        }
        Button allFeaturesButton = new Button(getTranslation("bot.card.allfeatures.button"), VaadinIcon.ARROW_RIGHT.create(), click -> UI.getCurrent().navigate(CommandsView.class));
        allFeaturesButton.setWidthFull();
        allFeaturesButton.setIconAfterText(true);

        return new BotProPanelInfo[]{
                new BotProPanelInfo("fishery", true, VaadinIcon.TROPHY.create(), 200),
                new BotProPanelInfo("gambling", true, VaadinIcon.COINS.create()),
                new BotProPanelInfo("dashboard", false, VaadinIcon.STAR.create(), wip, dashboardButton),
                new BotProPanelInfo("interactive", true, VaadinIcon.BOAT.create()),
                new BotProPanelInfo("tracker", true, VaadinIcon.BELL.create()),
                new BotProPanelInfo("autochannel", true, VaadinIcon.VOLUME.create()),
                new BotProPanelInfo("nsfw", !uiData.isNSFWDisabled(), VaadinIcon.MOON_O.create()),
                new BotProPanelInfo("reactionroles", true, VaadinIcon.TAG.create()),
                new BotProPanelInfo("emotesinteractions", true, VaadinIcon.GROUP.create()),
                new BotProPanelInfo("gimmicks", true, VaadinIcon.GAMEPAD.create()),
                new BotProPanelInfo("allfeatures", true, VaadinIcon.LINK.create(), allFeaturesButton)
        };
    }
}
