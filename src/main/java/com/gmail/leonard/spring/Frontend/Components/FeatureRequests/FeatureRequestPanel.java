package com.gmail.leonard.spring.Frontend.Components.FeatureRequests;

import com.github.appreciated.css.grid.GridLayoutComponent;
import com.github.appreciated.css.grid.sizes.Flex;
import com.github.appreciated.css.grid.sizes.Length;
import com.github.appreciated.css.grid.sizes.MinMax;
import com.github.appreciated.css.grid.sizes.Repeat;
import com.github.appreciated.layout.FlexibleGridLayout;
import com.gmail.leonard.spring.Backend.FeatureRequests.FRDynamicBean;
import com.gmail.leonard.spring.Backend.FeatureRequests.FREntry;
import com.gmail.leonard.spring.Backend.FeatureRequests.FRPanelType;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Styles;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Article;
import com.vaadin.flow.component.html.Div;
import java.util.ArrayList;

public class FeatureRequestPanel extends Div {

    private final SessionData sessionData;
    private final UIData uiData;
    private final ArrayList<FREntry> entries;
    private final FRPanelType type;

    public FeatureRequestPanel(SessionData sessionData, UIData uiData, FRDynamicBean frDynamicBean, ArrayList<FREntry> entries, FRPanelType type) {
        this.sessionData = sessionData;
        this.uiData = uiData;
        this.entries = entries;
        this.type = type;

        if (entries.isEmpty()) {
            addEmptyText();
        } else {
            addEntries();
        }
    }

    private void addEntries() {
        ArrayList<Article> articles = new ArrayList<>();
        for (FREntry entry : entries) {
            articles.add(new Article(new FeatureRequestCard(type, entry, sessionData, uiData)));
        }

        FlexibleGridLayout layout = new FlexibleGridLayout()
                .withColumns(Repeat.RepeatMode.AUTO_FILL, new MinMax(new Length("270px"), new Flex(1)))
                .withItems(articles.toArray(new Article[0]))
                .withPadding(false)
                .withSpacing(true)
                .withAutoFlow(GridLayoutComponent.AutoFlow.ROW_DENSE)
                .withOverflow(GridLayoutComponent.Overflow.AUTO);

        layout.setSizeFull();
        add(layout);
    }

    private void addEmptyText() {
        Div emptyText = new Div(new Text(getTranslation("fr.empty")));
        emptyText.setWidthFull();
        emptyText.addClassName(Styles.CENTER_TEXT);
        add(emptyText);
    }

}
