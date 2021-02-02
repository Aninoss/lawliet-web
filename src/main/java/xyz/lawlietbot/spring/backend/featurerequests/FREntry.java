package xyz.lawlietbot.spring.backend.featurerequests;

import xyz.lawlietbot.spring.syncserver.SendEvent;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FREntry {

    private final static Logger LOGGER = LoggerFactory.getLogger(FREntry.class);

    private final int id;
    private final String title;
    private final String description;
    private Integer boosts;
    private Integer recentBoosts;
    private final boolean publicEntry;
    private final FRPanelType type;
    private final FRDynamicBean frDynamicBean;
    private final LocalDate date;

    FREntry(FRDynamicBean frDynamicBean, int id, String title, String description, Integer boosts, Integer recentBoosts, boolean publicEntry, FRPanelType type, LocalDate date) {
        this.id = id;
        this.frDynamicBean = frDynamicBean;
        this.title = title;
        this.description = description;
        this.boosts = boosts;
        this.recentBoosts = recentBoosts;
        this.publicEntry = publicEntry;
        this.type = type;
        this.date = date;
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

    public Optional<Integer> getRecentBoosts() {
        return Optional.ofNullable(recentBoosts);
    }

    public FRPanelType getType() {
        return type;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean boost(long userId) {
        if (boosts != null && recentBoosts != null && frDynamicBean.getBoostsRemaining() > 0) {
            CompletableFuture<JSONObject> responseJsonFut = SendEvent.sendBoost(getId(), userId);
            try {
                if (checkBoost(responseJsonFut.get())) {
                    boosts++;
                    recentBoosts++;
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
