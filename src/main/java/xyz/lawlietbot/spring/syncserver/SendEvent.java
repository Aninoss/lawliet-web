package xyz.lawlietbot.spring.syncserver;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.lawlietbot.spring.backend.featurerequests.FRDynamicBean;
import xyz.lawlietbot.spring.backend.featurerequests.FRPanelType;
import xyz.lawlietbot.spring.backend.premium.UserPremium;
import xyz.lawlietbot.spring.backend.serverstats.ServerStatsBean;
import xyz.lawlietbot.spring.backend.serverstats.ServerStatsSlot;
import xyz.lawlietbot.spring.backend.userdata.SessionData;

public class SendEvent {

    private SendEvent() {
    }

    public static CompletableFuture<JSONObject> sendRequestCommandList() {
        return SyncManager.getInstance().getClient().send("COMMAND_LIST", new JSONObject());
    }

    public static CompletableFuture<JSONObject> sendRequestFAQList() {
        return SyncManager.getInstance().getClient().send("FAQ_LIST", new JSONObject());
    }

    public static CompletableFuture<ServerStatsBean> sendRequestServerStats() {
        return process("SERVER_STATS", new JSONObject(), responseJson -> {
            JSONArray statsDataJson = responseJson.getJSONArray("data");

            ServerStatsSlot[] slots = new ServerStatsSlot[statsDataJson.length()];
            for (int i = 0; i < statsDataJson.length(); i++) {
                JSONObject statsSlotJson = statsDataJson.getJSONObject(i);
                slots[i] = new ServerStatsSlot(statsSlotJson.getInt("month"), statsSlotJson.getInt("year"), statsSlotJson.getInt("value"));
            }

            return new ServerStatsBean(
                    responseJson.isNull("servers") ? null : responseJson.getLong("servers"),
                    slots
            );
        });
    }

    public static CompletableFuture<JSONObject> sendTopGG(JSONObject dataJson) {
        return SyncManager.getInstance().getClient().sendSecure("TOPGG", dataJson);
    }

    public static CompletableFuture<JSONObject> sendTopGGAnicord(JSONObject dataJson) {
        return SyncManager.getInstance().getClient().sendSecure("TOPGG_ANICORD", dataJson);
    }

    public static CompletableFuture<FRDynamicBean> sendRequestFeatureRequestMainData(SessionData sessionData) {
        JSONObject jsonObject = new JSONObject();
        if (sessionData.isLoggedIn()) jsonObject.put("user_id", sessionData.getDiscordUser().get().getId());
        return process("FR_FETCH", jsonObject, responseJson -> {
            int boostsTotal = responseJson.getInt("boosts_total");
            int boostsRemaining = responseJson.getInt("boosts_remaining");

            FRDynamicBean frDynamicBean = new FRDynamicBean(boostsRemaining, boostsTotal);

            JSONArray jsonEntriesArray = responseJson.getJSONArray("data");
            for (int j = 0; j < jsonEntriesArray.length(); j++) {
                JSONObject jsonEntry = jsonEntriesArray.getJSONObject(j);
                FRPanelType type = FRPanelType.valueOf(jsonEntry.getString("type"));
                boolean pub = jsonEntry.getBoolean("public");
                frDynamicBean.addEntry(
                        jsonEntry.getInt("id"),
                        jsonEntry.getString("title"),
                        jsonEntry.getString("description"),
                        type == FRPanelType.PENDING && pub ? jsonEntry.getInt("boosts") : null,
                        type == FRPanelType.PENDING && pub ? jsonEntry.getInt("recent_boosts") : null,
                        pub,
                        type,
                        LocalDate.ofEpochDay(jsonEntry.getLong("date"))
                );
            }

            return frDynamicBean;
        });
    }

    public static CompletableFuture<JSONObject> sendBoost(int id, long userId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("entry_id", id);
        jsonObject.put("user_id", userId);
        return SyncManager.getInstance().getClient().send("FR_BOOST", jsonObject);
    }

    public static CompletableFuture<Boolean> sendRequestCanPost(long userId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", userId);
        return process("FR_CAN_POST", jsonObject, responseJson -> responseJson.getBoolean("success"));
    }

    public static CompletableFuture<JSONObject> sendNewFeatureRequest(long userId, String title, String description, boolean notify) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", userId);
        jsonObject.put("title", title);
        jsonObject.put("description", description);
        jsonObject.put("notify", notify);
        return SyncManager.getInstance().getClient().send("FR_POST", jsonObject);
    }

    public static CompletableFuture<JSONObject> sendInvite(String type) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        return SyncManager.getInstance().getClient().send("INVITE", jsonObject);
    }

    public static CompletableFuture<UserPremium> sendRequestUserPremium(long userId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", userId);
        return process("PREMIUM", jsonObject, jsonResponse -> {
            ArrayList<Long> slots = new ArrayList<>();
            JSONArray jsonSlots = jsonResponse.getJSONArray("slots");
            for (int i = 0; i < jsonSlots.length(); i++) {
                slots.add(jsonSlots.getLong(i));
            }

            return new UserPremium(userId, slots);
        });
    }

    public static CompletableFuture<Boolean> sendModifyPremium(long userId, int slot, long guildId) {
        JSONObject json = new JSONObject();
        json.put("user_id", userId);
        json.put("slot", slot);
        json.put("guild_id", guildId);

        return process(
                "PREMIUM_MODIFY",
                json,
                r -> r.getBoolean("success")
        );
    }

    private static <T> CompletableFuture<T> process(String event, JSONObject dataJson, Function<JSONObject, T> function) {
        CompletableFuture<T> future = new CompletableFuture<>();

        SyncManager.getInstance().getClient().send(event, dataJson)
                .exceptionally(e -> {
                    future.completeExceptionally(e);
                    return null;
                })
                .thenAccept(jsonResponse -> {
                    try {
                        T t = function.apply(jsonResponse);
                        future.complete(t);
                    } catch (Throwable e) {
                        future.completeExceptionally(e);
                    }
                });

        return future;
    }

}
