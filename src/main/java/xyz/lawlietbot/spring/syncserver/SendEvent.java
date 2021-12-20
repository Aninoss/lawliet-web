package xyz.lawlietbot.spring.syncserver;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import dashboard.ActionResult;
import dashboard.DashboardComponent;
import dashboard.component.DashboardDiscordEntitySelection;
import dashboard.container.DashboardContainer;
import dashboard.data.DiscordEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.lawlietbot.spring.backend.dashboard.DashboardCategoryInitData;
import xyz.lawlietbot.spring.backend.dashboard.DashboardInitData;
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

    public static CompletableFuture<Void> sendReport(String url, String reason, String ip) {
        JSONObject json = new JSONObject();
        json.put("url", url);
        json.put("text", reason);
        json.put("ip_hash", ip.hashCode());

        return process(
                "REPORT",
                json,
                r -> null
        );
    }

    public static CompletableFuture<Void> sendStripe(long userId, String title, String desc) {
        JSONObject json = new JSONObject();
        json.put("user_id", userId);
        json.put("title", title);
        json.put("desc", desc);

        return process(
                "STRIPE",
                json,
                r -> null
        );
    }

    public static CompletableFuture<DashboardInitData> sendDashboardInit(long guildId, long userId, Locale locale) {
        JSONObject json = new JSONObject();
        json.put("guild_id", guildId);
        json.put("user_id", userId);
        json.put("locale", locale);

        return process(
                "DASH_INIT",
                json,
                r -> {
                    if (r.getBoolean("ok")) {
                        ArrayList<DashboardInitData.Category> categories = new ArrayList<>();
                        JSONArray titlesJson = r.getJSONArray("titles");
                        for (int i = 0; i < titlesJson.length(); i++) {
                            JSONObject data = titlesJson.getJSONObject(i);
                            DashboardInitData.Category category = new DashboardInitData.Category(
                                    data.getString("id"),
                                    data.getString("title")
                            );
                            categories.add(category);
                        }
                        return new DashboardInitData(categories);
                    } else {
                        return null;
                    }
                }
        );
    }

    public static CompletableFuture<DashboardCategoryInitData> sendDashboardCategoryInit(String categoryId, long guildId, long userId, Locale locale) {
        JSONObject json = new JSONObject();
        json.put("category", categoryId);
        json.put("guild_id", guildId);
        json.put("user_id", userId);
        json.put("locale", locale);

        return process(
                "DASH_CAT_INIT",
                json,
                r -> {
                    if (r.getBoolean("ok")) {
                        ArrayList<String> missingBotPermissions = new ArrayList<>();
                        JSONArray missingBotPermissionsJson = r.getJSONArray("missing_bot_permissions");
                        for (int i = 0; i < missingBotPermissionsJson.length(); i++) {
                            missingBotPermissions.add(missingBotPermissionsJson.getString(i));
                        }

                        ArrayList<String> missingUserPermissions = new ArrayList<>();
                        JSONArray missingUserPermissionsJson = r.getJSONArray("missing_user_permissions");
                        for (int i = 0; i < missingUserPermissionsJson.length(); i++) {
                            missingUserPermissions.add(missingUserPermissionsJson.getString(i));
                        }

                        DashboardContainer components = null;
                        if (missingBotPermissions.isEmpty() && missingUserPermissions.isEmpty()) {
                            components = (DashboardContainer) DashboardComponent.generate(r.getJSONObject("components"));
                        }

                        return new DashboardCategoryInitData(missingBotPermissions, missingUserPermissions, components);
                    } else {
                        return null;
                    }
                }
        );
    }

    public static CompletableFuture<ActionResult> sendDashboardAction(long guildId, long userId, JSONObject actionJson) {
        JSONObject json = new JSONObject();
        json.put("guild_id", guildId);
        json.put("user_id", userId);
        json.put("action", actionJson);

        return process(
                "DASH_ACTION",
                json,
                r -> {
                    if (r.getBoolean("ok")) {
                        ActionResult actionResult = new ActionResult(r.getBoolean("redraw"));
                        if (r.has("success_message")) {
                            actionResult = actionResult.withSuccessMessage(r.getString("success_message"));
                        }
                        if (r.has("error_message")) {
                            actionResult = actionResult.withErrorMessage(r.getString("error_message"));
                        }
                        return actionResult;
                    } else {
                        throw new RuntimeException();
                    }
                }
        );
    }

    public static CompletableFuture<List<DiscordEntity>> sendDashboardListDiscordEntities(DashboardDiscordEntitySelection.DataType type, long guildId, long userId, int offset, int limit, String filterText) {
        JSONObject json = new JSONObject();
        json.put("type", type.name());
        json.put("guild_id", guildId);
        json.put("user_id", userId);
        json.put("offset", offset);
        json.put("limit", limit);
        json.put("filter_text", filterText);

        return process(
                "DASH_LIST_DISCORD_ENTITIES",
                json,
                r -> {
                    ArrayList<DiscordEntity> discordEntities = new ArrayList<>();
                    JSONArray entitiesJson = r.getJSONArray("entities");
                    for (int i = 0; i < entitiesJson.length(); i++) {
                        JSONObject entityJson = entitiesJson.getJSONObject(i);
                        DiscordEntity discordEntity = new DiscordEntity(
                                entityJson.getLong("id"),
                                entityJson.getString("name")
                        );
                        discordEntities.add(discordEntity);
                    }
                    return Collections.unmodifiableList(discordEntities);
                }
        );
    }

    public static CompletableFuture<Long> sendDashboardCountDiscordEntities(DashboardDiscordEntitySelection.DataType type, long guildId, long userId, String filterText) {
        JSONObject json = new JSONObject();
        json.put("type", type.name());
        json.put("user_id", userId);
        json.put("guild_id", guildId);
        json.put("filter_text", filterText);

        return process(
                "DASH_COUNT_DISCORD_ENTITIES",
                json,
                r -> r.getLong("count")
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
