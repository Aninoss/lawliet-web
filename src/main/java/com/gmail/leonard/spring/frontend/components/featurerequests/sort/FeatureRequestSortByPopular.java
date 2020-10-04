package com.gmail.leonard.spring.frontend.components.featurerequests.sort;

import com.gmail.leonard.spring.backend.featurerequests.FREntry;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class FeatureRequestSortByPopular extends FeatureRequestSort {

    @Override
    protected int compareDetailed(FREntry o1, FREntry o2) {
        return Double.compare(getScore(o2), getScore(o1));
    }

    private double getScore(FREntry entry) {
        return Math.pow(entry.getBoosts().map(i -> (double)i).orElse(-0.25) + 0.25, 1.5) / (double)(ChronoUnit.DAYS.between(entry.getDate(), LocalDate.now()) + 1);
    }

}
