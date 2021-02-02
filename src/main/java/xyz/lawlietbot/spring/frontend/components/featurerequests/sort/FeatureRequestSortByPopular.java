package xyz.lawlietbot.spring.frontend.components.featurerequests.sort;

import xyz.lawlietbot.spring.backend.featurerequests.FREntry;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class FeatureRequestSortByPopular extends FeatureRequestSort {

    @Override
    protected int compareDetailed(FREntry o1, FREntry o2) {
        int c = Double.compare(getScore(o2), getScore(o1));
        if (c != 0)
            return c;
        return Integer.compare(o2.getBoosts().orElse(-1), o1.getBoosts().orElse(-1));
    }

    private double getScore(FREntry entry) {
        return entry.getRecentBoosts().map(recentBoosts -> recentBoosts * Math.sqrt(8.0 / (getDaysBetweenEntry(entry) + 1))).orElse(-1.0);
    }

    private long getDaysBetweenEntry(FREntry entry) {
        return Math.min(7, ChronoUnit.DAYS.between(entry.getDate(), LocalDate.now()));
    }

    @Override
    public String getId() {
        return "popular";
    }

}
