package xyz.lawlietbot.spring.frontend.components.featurerequests.sort;

import xyz.lawlietbot.spring.backend.featurerequests.FREntry;

public class FeatureRequestSortByNewest extends FeatureRequestSort {

    @Override
    protected int compareDetailed(FREntry o1, FREntry o2) {
        if (o1.getDate().isBefore(o2.getDate())) return 1;
        if (o2.getDate().isBefore(o1.getDate())) return -1;
        return 0;
    }

    @Override
    public String getId() {
        return "newest";
    }

}
