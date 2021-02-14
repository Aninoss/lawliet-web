package xyz.lawlietbot.spring.frontend.components.featurerequests;

import xyz.lawlietbot.spring.backend.featurerequests.FRDynamicBean;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.featurerequests.sort.FeatureRequestSort;
import xyz.lawlietbot.spring.frontend.views.FeatureRequestsView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class FeatureRequestMain extends VerticalLayout {

    private final FeatureRequestChangeSort featureRequestChangeSort;
    private final FeatureRequestPanel featureRequestPanel;
    private final FeatureRequestPages featureRequestPages;

    private FeatureRequestSort sort;
    private int page;

    public FeatureRequestMain(SessionData sessionData, UIData uiData, FRDynamicBean frDynamicBean, FeatureRequestSort[] comparators, int page, FeatureRequestSort sort) {
        this.page = page;
        this.sort = sort;

        setWidthFull();
        setPadding(true);
        addClassName(Styles.APP_WIDTH);
        getStyle().set("margin-top", "16px")
                .set("margin-bottom", "-16px");

        featureRequestChangeSort = new FeatureRequestChangeSort(this::onSortChange, this::onPageChangePrevious, this::onPageChangeNext, comparators, sort);
        add(featureRequestChangeSort);

        Hr hr = new Hr();
        hr.getStyle().set("margin-bottom", "0");
        add(hr);

        featureRequestPanel = new FeatureRequestPanel(sessionData, uiData, frDynamicBean);
        page = checkPageBounds();
        featureRequestPanel.updateEntries(page, sort);
        featureRequestChangeSort.onPageChanged(page, featureRequestPanel.getPageSize());
        add(featureRequestPanel);

        featureRequestPages = new FeatureRequestPages(this::onPageChange);
        featureRequestPages.setPage(page, featureRequestPanel.getPageSize());
        add(featureRequestPages);
    }

    private int checkPageBounds() {
        if (page < 0)
            page = 0;
        else if (page >= featureRequestPanel.getPageSize())
            page = featureRequestPanel.getPageSize() - 1;

        return page;
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
        UI.getCurrent().getPage().getHistory().pushState(null, getUri());
    }

    private void onSortChange(FeatureRequestSort sort) {
        this.sort = sort;
        this.page = 0;

        featureRequestChangeSort.onPageChanged(0, featureRequestPanel.getPageSize());
        featureRequestPanel.updateEntries(0, sort);
        featureRequestPages.setPage(0, featureRequestPanel.getPageSize());
        UI.getCurrent().getPage().getHistory().pushState(null, getUri());
    }

    private String getUri() {
        return FeatureRequestsView.getRouteStatic(FeatureRequestsView.class) +
                "?page=" + (page + 1) +
                "&sortby=" + sort.getId();
    }

}
