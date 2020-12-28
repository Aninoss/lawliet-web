package com.gmail.leonard.spring.frontend.components.featurerequests;

import com.github.appreciated.css.grid.GridLayoutComponent;
import com.github.appreciated.css.grid.sizes.Flex;
import com.github.appreciated.css.grid.sizes.Length;
import com.github.appreciated.css.grid.sizes.MinMax;
import com.github.appreciated.css.grid.sizes.Repeat;
import com.github.appreciated.layout.FlexibleGridLayout;
import com.gmail.leonard.spring.backend.featurerequests.FRDynamicBean;
import com.gmail.leonard.spring.backend.featurerequests.FREntry;
import com.gmail.leonard.spring.backend.featurerequests.FRPanelType;
import com.gmail.leonard.spring.backend.userdata.SessionData;
import com.gmail.leonard.spring.backend.userdata.UIData;
import com.gmail.leonard.spring.frontend.Styles;
import com.gmail.leonard.spring.frontend.components.featurerequests.sort.FeatureRequestSort;
import com.gmail.leonard.spring.frontend.components.featurerequests.sort.FeatureRequestSortByBoosts;
import com.gmail.leonard.spring.frontend.components.featurerequests.sort.FeatureRequestSortByNewest;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Article;
import com.vaadin.flow.component.html.Div;
import java.util.ArrayList;
import java.util.Comparator;

public class FeatureRequestPanel extends Div {

    private static final int ENTRIES_PER_PAGE = 12;

    private final SessionData sessionData;
    private final UIData uiData;
    private final FRDynamicBean frDynamicBean;
    private FlexibleGridLayout gridLayout = null;

    public FeatureRequestPanel(SessionData sessionData, UIData uiData, FRDynamicBean frDynamicBean) {
        this.sessionData = sessionData;
        this.uiData = uiData;
        this.frDynamicBean = frDynamicBean;
        setWidthFull();
    }

    public void updateEntries(int page, FeatureRequestSort comparator) {
        ArrayList<FREntry> entryList = new ArrayList<>(frDynamicBean.getEntryList());
        entryList.sort(comparator);

        ArrayList<Article> articles = new ArrayList<>();
        for (int i = ENTRIES_PER_PAGE * page; i < Math.min(entryList.size(), ENTRIES_PER_PAGE * (page + 1)); i++) {
            FREntry entry = entryList.get(i);
            articles.add(new Article(new FeatureRequestCard(entry, sessionData, uiData)));
        }

        if (gridLayout != null) remove(gridLayout);
        gridLayout = new FlexibleGridLayout()
                .withColumns(Repeat.RepeatMode.AUTO_FILL, new MinMax(new Length("270px"), new Flex(1)))
                .withItems(articles.toArray(new Article[0]))
                .withPadding(false)
                .withSpacing(true)
                .withAutoFlow(GridLayoutComponent.AutoFlow.ROW_DENSE)
                .withOverflow(GridLayoutComponent.Overflow.AUTO);

        gridLayout.setSizeFull();
        gridLayout.getStyle().set("overflow", "visible");
        add(gridLayout);
    }

    public int getPageSize() {
        return (frDynamicBean.getEntryList().size() - 1) / ENTRIES_PER_PAGE + 1;
    }

}
