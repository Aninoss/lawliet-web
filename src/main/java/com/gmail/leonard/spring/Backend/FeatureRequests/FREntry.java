package com.gmail.leonard.spring.Backend.FeatureRequests;

import java.util.ArrayList;
import java.util.Optional;

public class FREntry {

    private final String description;
    private Integer boosts;
    private boolean publicEntry;
    private BoostIncreaseListener listener;
    private final FRDynamicBean frDynamicBean;

    FREntry(FRDynamicBean frDynamicBean, String description, Integer boosts, boolean publicEntry, BoostIncreaseListener listener) {
        this.frDynamicBean = frDynamicBean;
        this.description = description;
        this.boosts = boosts;
        this.publicEntry = publicEntry;
        this.listener = listener;
    }

    public String getDescription() {
        return description;
    }

    public Optional<Integer> getBoosts() {
        return Optional.ofNullable(boosts);
    }

    public boolean boost() {
        if (boosts != null && frDynamicBean.getBoostsRemaining() > 0) {
            boosts++;
            listener.onBoostIncrease();
            return true;
        }
        return false;
    }

    public boolean isPublic() {
        return publicEntry;
    }

}
