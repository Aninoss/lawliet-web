package com.gmail.leonard.spring.Backend.FeatureRequests;

import java.util.ArrayList;
import java.util.HashMap;

public class FRDynamicBean {

    private int boostsRemaining, boostsTotal;
    private final HashMap<FRPanelType, ArrayList<FREntry>> entryCategoryMap = new HashMap<>();
    private BoostIncreaseListener boostChangeListener = null;

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

    public ArrayList<FREntry> getEntryCategoryMap(FRPanelType type) {
        return entryCategoryMap.computeIfAbsent(type, k -> new ArrayList<>());
    }

    public void addEntry(FRPanelType type, int id, String title, String description, Integer boosts, boolean publicEntry) {
        FREntry frEntry = new FREntry(this, id, title, description, boosts, publicEntry);
        getEntryCategoryMap(type).add(frEntry);
    }

    public void update(int boostsRemaining, int boostsTotal) {
        this.boostsRemaining = boostsRemaining;
        this.boostsTotal = boostsTotal;
        setChanged();
    }

    private void setChanged() {
        if (boostChangeListener != null)
            boostChangeListener.onBoostChange(boostsRemaining, boostsTotal);
    }

    public void setBoostChangeListener(BoostIncreaseListener boostChangeListener) {
        this.boostChangeListener = boostChangeListener;
    }

}
