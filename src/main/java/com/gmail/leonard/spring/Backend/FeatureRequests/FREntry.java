package com.gmail.leonard.spring.Backend.FeatureRequests;

import com.gmail.leonard.spring.Backend.CommandList.CommandListContainer;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.Modules.FeatureRequests;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FREntry {

    private final static Logger LOGGER = LoggerFactory.getLogger(FREntry.class);

    private final int id;
    private final String title, description;
    private Integer boosts;
    private final boolean publicEntry;
    private final FRDynamicBean frDynamicBean;

    FREntry(FRDynamicBean frDynamicBean, int id, String title, String description, Integer boosts, boolean publicEntry) {
        this.id = id;
        this.frDynamicBean = frDynamicBean;
        this.title = title;
        this.description = description;
        this.boosts = boosts;
        this.publicEntry = publicEntry;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Optional<Integer> getBoosts() {
        return Optional.ofNullable(boosts);
    }

    public boolean boost(long userId) {
        if (boosts != null && frDynamicBean.getBoostsRemaining() > 0) {
            CompletableFuture<JSONObject> responseJsonFut = FeatureRequests.sendBoost(frDynamicBean, getId(), userId);
            try {
                if (checkBoost(responseJsonFut.get())) {
                    boosts++;
                    return true;
                }
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Error when submitting boost", e);
            }
        }

        return false;
    }

    public boolean isPublic() {
        return publicEntry;
    }

    private boolean checkBoost(JSONObject responseJson) {
        int boostsTotal = responseJson.getInt("boosts_total");
        int boostsRemaining = responseJson.getInt("boosts_remaining");
        frDynamicBean.update(boostsRemaining, boostsTotal);

        return responseJson.getBoolean("success");
    }

}
