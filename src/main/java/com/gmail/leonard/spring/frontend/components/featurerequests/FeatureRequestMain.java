package com.gmail.leonard.spring.frontend.components.featurerequests;

import com.gmail.leonard.spring.backend.featurerequests.FRDynamicBean;
import com.gmail.leonard.spring.backend.userdata.SessionData;
import com.gmail.leonard.spring.backend.userdata.UIData;
import com.gmail.leonard.spring.frontend.Styles;
import com.gmail.leonard.spring.frontend.components.featurerequests.sort.FeatureRequestSort;
import com.gmail.leonard.spring.frontend.components.featurerequests.sort.FeatureRequestSortByPopular;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class FeatureRequestMain extends VerticalLayout {

    private final FeatureRequestChangeSort featureRequestChangeSort;
    private final FeatureRequestPanel featureRequestPanel;
    private final FeatureRequestPages featureRequestPages;

    private FeatureRequestSort sort = new FeatureRequestSortByPopular();
    private int page = 0;

    public FeatureRequestMain(SessionData sessionData, UIData uiData, FRDynamicBean frDynamicBean) {
        setWidthFull();
        setPadding(false);
        addClassName(Styles.APP_WIDTH);
        getStyle().set("margin-top", "16px");

        featureRequestChangeSort = new FeatureRequestChangeSort(this::onSortChange, this::onPageChangePrevious, this::onPageChangeNext);
        add(featureRequestChangeSort);

        featureRequestPanel = new FeatureRequestPanel(sessionData, uiData, frDynamicBean);
        featureRequestPanel.updateEntries(0, sort);
        featureRequestChangeSort.onPageChanged(0, featureRequestPanel.getPageSize());
        add(featureRequestPanel);

        featureRequestPages = new FeatureRequestPages(this::onPageChange);
        featureRequestPages.setPage(0, featureRequestPanel.getPageSize());
        add(featureRequestPages);
    }

    private void onPageChangePrevious() {
        page--;
        if (page < 0)
            page = featureRequestPanel.getPageSize() - 1;

        onPageChange(page);
    }

    private void onPageChangeNext() {
        page++;
        if (page >= featureRequestPanel.getPageSize())
            page = 0;

        onPageChange(page);
    }

    private void onPageChange(int page) {
        this.page = page;
        featureRequestChangeSort.onPageChanged(page, featureRequestPanel.getPageSize());
        featureRequestPanel.updateEntries(page, sort);
        featureRequestPages.setPage(page, featureRequestPanel.getPageSize());
    }

    private void onSortChange(FeatureRequestSort sort) {
        this.sort = sort;
        featureRequestChangeSort.onPageChanged(0, featureRequestPanel.getPageSize());
        featureRequestPanel.updateEntries(0, sort);
        featureRequestPages.setPage(0, featureRequestPanel.getPageSize());
    }

}
