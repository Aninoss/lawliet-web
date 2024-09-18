package xyz.lawlietbot.spring.frontend.components.home.botpros;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Article;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.addons.componentfactory.css.grid.GridLayoutComponent;
import org.vaadin.addons.componentfactory.css.grid.sizes.Flex;
import org.vaadin.addons.componentfactory.css.grid.sizes.Length;
import org.vaadin.addons.componentfactory.css.grid.sizes.MinMax;
import org.vaadin.addons.componentfactory.css.grid.sizes.Repeat;
import org.vaadin.addons.componentfactory.layout.FlexibleGridLayout;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.views.CommandsView;

import java.util.ArrayList;

public class BotProsPanelsLayout extends VerticalLayout {

    public BotProsPanelsLayout(UIData uiData) {
        BotProPanelInfo[] botProPanelInfos = getBotProPanelInfos(uiData);
        int entries = (botProPanelInfos.length / 3) * 3;

        ArrayList<Article> articles = new ArrayList<>();
        for(int i = 0; i < botProPanelInfos.length && articles.size() < entries; i++) {
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
        Button allFeaturesButton = new Button(
                getTranslation("bot.card.allfeatures.button"),
                VaadinIcon.ARROW_RIGHT.create(),
                click -> UI.getCurrent().navigate(CommandsView.class)
        );
        allFeaturesButton.setWidthFull();
        allFeaturesButton.setIconAfterText(true);

        return new BotProPanelInfo[]{
                new BotProPanelInfo("fishery", true, VaadinIcon.TROPHY.create(), 150),
                new BotProPanelInfo("alerts", true, VaadinIcon.BELL.create()),
                new BotProPanelInfo("txt2img", true, VaadinIcon.PENCIL.create()),
                new BotProPanelInfo("nsfw", false, VaadinIcon.MOON_O.create()),
                new BotProPanelInfo("mod", true, VaadinIcon.SHIELD.create()),
                new BotProPanelInfo("invitetracking", true, VaadinIcon.ENVELOPE.create()),
                new BotProPanelInfo("giveaways", true, VaadinIcon.GIFT.create()),
                new BotProPanelInfo("reactionroles", true, VaadinIcon.TAG.create()),
                new BotProPanelInfo("autoroles", true, VaadinIcon.AUTOMATION.create()),
                new BotProPanelInfo("welcome", true, VaadinIcon.HAND.create()),
                new BotProPanelInfo("tickets", true, VaadinIcon.TICKET.create()),
                new BotProPanelInfo("birthday", true, VaadinIcon.STAR.create()),
                new BotProPanelInfo("sugg", true, VaadinIcon.COMMENT_ELLIPSIS.create()),
                new BotProPanelInfo("autochannel", true, VaadinIcon.VOLUME.create()),
                new BotProPanelInfo("roleplay", true, VaadinIcon.GROUP.create()),
                new BotProPanelInfo("reminders", true, VaadinIcon.CLOCK.create()),
                new BotProPanelInfo("mcdisplays", false, VaadinIcon.TRENDING_UP.create()),
                new BotProPanelInfo("allfeatures", false, VaadinIcon.LINK.create(), allFeaturesButton)
        };
    }
}
