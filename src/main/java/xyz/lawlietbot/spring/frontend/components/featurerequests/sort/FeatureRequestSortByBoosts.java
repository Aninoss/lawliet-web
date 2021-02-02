package xyz.lawlietbot.spring.frontend.components.featurerequests.sort;

import xyz.lawlietbot.spring.backend.featurerequests.FREntry;

public class FeatureRequestSortByBoosts extends FeatureRequestSort {

    @Override
    protected int compareDetailed(FREntry o1, FREntry o2) {
        return Integer.compare(o2.getBoosts().orElse(0), o1.getBoosts().orElse(0));
    }

    @Override
    public String getId() {
        return "boosts";
    }

}
