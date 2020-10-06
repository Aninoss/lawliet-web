package com.gmail.leonard.spring.frontend.components.featurerequests.sort;

import com.gmail.leonard.spring.backend.featurerequests.FREntry;

import java.util.Comparator;

public abstract class FeatureRequestSort implements Comparator<FREntry> {

    @Override
    public int compare(FREntry o1, FREntry o2) {
        //if (o1.getBoosts().isPresent() && !o2.getBoosts().isPresent()) return -1;
        //if (!o1.getBoosts().isPresent() && o2.getBoosts().isPresent()) return 1;
        return compareDetailed(o1, o2);
    }

    public abstract String getId();

    protected abstract int compareDetailed(FREntry o1, FREntry o2);

}
