package xyz.lawlietbot.spring.frontend.components.featurerequests;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import xyz.lawlietbot.spring.backend.featurerequests.FRDynamicBean;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.featurerequests.sort.FeatureRequestSort;
import xyz.lawlietbot.spring.frontend.views.FeatureRequestsView;

public class FeatureRequestMain extends VerticalLayout {

    private final SessionData sessionData;
    private final FeatureRequestChangeSort featureRequestChangeSort;
    private final FeatureRequestPanel featureRequestPanel;
    private final FeatureRequestPages featureRequestPages;

    private FeatureRequestSort sort;
    private int page;
    private String search;

    public FeatureRequestMain(SessionData sessionData, UIData uiData, FRDynamicBean frDynamicBean,
                              FeatureRequestSort[] comparators, int page, FeatureRequestSort sort, String search) {
        this.sessionData = sessionData;
        this.page = page;
        this.sort = sort;
        this.search = search;

        setWidthFull();
        setPadding(true);
        addClassName(Styles.APP_WIDTH);
        getStyle().set("margin-top", "16px")
                .set("margin-bottom", "-16px");

        featureRequestChangeSort = new FeatureRequestChangeSort(this::onSortChange, this::onPageChangePrevious,
                this::onPageChangeNext, this::onSearch, comparators, sort, search
        );
        add(featureRequestChangeSort);

        Hr hr = new Hr();
        hr.getStyle().set("margin-bottom", "0");
        add(hr);

        ConfirmationDialog confirmationDialog = new ConfirmationDialog();
        add(confirmationDialog);

        featureRequestPanel = new FeatureRequestPanel(sessionData, uiData, frDynamicBean, confirmationDialog);
        page = checkPageBounds();
        featureRequestPanel.updateEntries(page, sort, search);
        featureRequestChangeSort.onPageChanged(page, featureRequestPanel.getPageSize(search));
        add(featureRequestPanel);

        featureRequestPages = new FeatureRequestPages(this::onPageChange);
        featureRequestPages.setPage(page, featureRequestPanel.getPageSize(search));
        add(featureRequestPages);
    }

    private int checkPageBounds() {
        if (page < 0) {
            page = 0;
        } else if (page >= featureRequestPanel.getPageSize(search)) {
            page = featureRequestPanel.getPageSize(search) - 1;
        }

        return page;
    }

    private void onPageChangePrevious() {
        page--;
        if (page < 0) {
            page = featureRequestPanel.getPageSize(search) - 1;
        }

        onPageChange(page);
    }

    private void onPageChangeNext() {
        page++;
        if (page >= featureRequestPanel.getPageSize(search)) {
            page = 0;
        }

        onPageChange(page);
    }

    private void onPageChange(int page) {
        this.page = page;

        featureRequestChangeSort.onPageChanged(page, featureRequestPanel.getPageSize(search));
        featureRequestPanel.updateEntries(page, sort, search);
        featureRequestPages.setPage(page, featureRequestPanel.getPageSize(search));
        sessionData.pushUri(getUri());
    }

    private void onSortChange(FeatureRequestSort sort) {
        this.sort = sort;
        this.page = 0;

        featureRequestChangeSort.onPageChanged(0, featureRequestPanel.getPageSize(search));
        featureRequestPanel.updateEntries(0, sort, search);
        featureRequestPages.setPage(0, featureRequestPanel.getPageSize(search));
        sessionData.pushUri(getUri());
    }

    private void onSearch(String search) {
        this.search = search;
        this.page = 0;

        featureRequestChangeSort.onPageChanged(0, featureRequestPanel.getPageSize(search));
        featureRequestPanel.updateEntries(page, sort, search);
        featureRequestPages.setPage(0, featureRequestPanel.getPageSize(search));
        sessionData.pushUri(getUri());
    }

    private String getUri() {
        return FeatureRequestsView.getRouteStatic(FeatureRequestsView.class) +
                "?page=" + (page + 1) +
                "&sortby=" + sort.getId() +
                "&search=" + URLEncoder.encode(search, StandardCharsets.UTF_8);
    }

}
