package xyz.lawlietbot.spring.frontend.components.featurerequests.sort;

import xyz.lawlietbot.spring.backend.featurerequests.FREntry;

public class FeatureRequestSortByTitle extends FeatureRequestSort {

    @Override
    protected int compareDetailed(FREntry o1, FREntry o2) {
        return o1.getTitle().compareToIgnoreCase(o2.getTitle());
    }

    @Override
    public String getId() {
        return "title";
    }

}
