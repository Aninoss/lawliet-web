package com.gmail.leonard.spring.Backend.FeatureRequests;

import java.util.ArrayList;
import java.util.HashMap;

public class FRDynamicBean {

    private int boostsRemaining;
    private final int boostsTotal;
    private final HashMap<String, ArrayList<FREntry>> entryCategoryMap = new HashMap<>();
    private BoostIncreaseListener boostIncreaseListener = null;

    public FRDynamicBean(int boostsRemaining, int boostsTotal) {
        this.boostsRemaining = boostsRemaining;
        this.boostsTotal = boostsTotal;
    }

    public int getBoostsRemaining() {
        return boostsRemaining;
    }

    public int getBoostsTotal() {
        return boostsTotal;
    }

    public ArrayList<FREntry> getEntryCategoryMap(String type) {
        return entryCategoryMap.computeIfAbsent(type, k -> new ArrayList<>());
    }

    public FREntry generateEntry(String type, String description, Integer boosts, boolean publicEntry) {
        FREntry frEntry = new FREntry(this, description, boosts, publicEntry, this::onBoost);
        getEntryCategoryMap(type).add(frEntry);
        return frEntry;
    }

    private void onBoost() {
        boostsRemaining--;
        if (boostIncreaseListener != null)
            boostIncreaseListener.onBoostIncrease();
    }

    public void setBoostIncreaseListener(BoostIncreaseListener boostIncreaseListener) {
        this.boostIncreaseListener = boostIncreaseListener;
    }

}
