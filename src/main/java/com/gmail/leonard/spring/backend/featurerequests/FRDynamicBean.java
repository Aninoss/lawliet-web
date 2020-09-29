package com.gmail.leonard.spring.backend.featurerequests;

import java.time.LocalDate;
import java.util.ArrayList;

public class FRDynamicBean {

    private int boostsRemaining, boostsTotal;
    private final ArrayList<FREntry> entryList = new ArrayList<>();
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

    public ArrayList<FREntry> getEntryList() {
        return entryList;
    }

    public void addEntry(int id, String title, String description, Integer boosts, boolean publicEntry, FRPanelType type, LocalDate date) {
        FREntry frEntry = new FREntry(this, id, title, description, boosts, publicEntry, type, date);
        entryList.add(frEntry);
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
