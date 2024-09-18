package xyz.lawlietbot.spring.syncserver;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SendEvent {

    private enum ForwardType { ALL_CLUSTERS, ANY_CLUSTER, SPECIFIC_CLUSTER, SPECIFIC_GUILD }

    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();

    public static void sendToAllClusters(EventOut eventOut) {
        sendToAllClusters(eventOut, new JSONObject());
    }

    public static void sendToAllClusters(EventOut eventOut, Map<String, Object> requestMap) {
        JSONObject requestJson = new JSONObject();
        requestMap.forEach((k, v) -> {
            try {
                requestJson.put(k, v);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        sendToAllClusters(eventOut, requestJson);
    }

    public static void sendToAllClusters(EventOut eventOut, JSONObject requestJson) {
        try {
            requestJson.put("forward_type", ForwardType.ALL_CLUSTERS);
            requestJson.put("event", eventOut.name());
            send(EventOut.FORWARD, requestJson);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompletableFuture<JSONObject> sendToAnyCluster(EventOut eventOut) {
        return sendToAnyCluster(eventOut, new JSONObject());
    }

    public static CompletableFuture<JSONObject> sendToAnyCluster(EventOut eventOut, Map<String, Object> requestMap) {
        JSONObject requestJson = new JSONObject();
        requestMap.forEach((k, v) -> {
            try {
                requestJson.put(k, v);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        return sendToAnyCluster(eventOut, requestJson);
    }

    public static CompletableFuture<JSONObject> sendToAnyCluster(EventOut eventOut, JSONObject requestJson) {
        try {
            requestJson.put("forward_type", ForwardType.ANY_CLUSTER);
            requestJson.put("event", eventOut.name());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return send(EventOut.FORWARD, requestJson);
    }

    public static CompletableFuture<JSONObject> sendToCluster(EventOut eventOut, int clusterId) {
        return sendToCluster(eventOut, new JSONObject(), clusterId);
    }

    public static CompletableFuture<JSONObject> sendToCluster(EventOut eventOut, Map<String, Object> requestMap, int clusterId) {
        JSONObject requestJson = new JSONObject();
        requestMap.forEach((k, v) -> {
            try {
                requestJson.put(k, v);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        return sendToCluster(eventOut, requestJson, clusterId);
    }

    public static CompletableFuture<JSONObject> sendToCluster(EventOut eventOut, JSONObject requestJson, int clusterId) {
        try {
            requestJson.put("forward_type", ForwardType.SPECIFIC_CLUSTER);
            requestJson.put("event", eventOut.name());
            requestJson.put("cluster_id", clusterId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return send(EventOut.FORWARD, requestJson);
    }

    public static CompletableFuture<JSONObject> sendToGuild(EventOut eventOut, long guildId) {
        return sendToGuild(eventOut, new JSONObject(), guildId);
    }

    public static CompletableFuture<JSONObject> sendToGuild(EventOut eventOut, Map<String, Object> requestMap, long guildId) {
        JSONObject requestJson = new JSONObject();
        requestMap.forEach((k, v) -> {
            try {
                requestJson.put(k, v);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        return sendToGuild(eventOut, requestJson, guildId);
    }

    public static CompletableFuture<JSONObject> sendToGuild(EventOut eventOut, JSONObject requestJson, long guildId) {
        try {
            requestJson.put("forward_type", ForwardType.SPECIFIC_GUILD);
            requestJson.put("event", eventOut.name());
            requestJson.put("guild_id", guildId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return send(EventOut.FORWARD, requestJson);
    }

    public static CompletableFuture<JSONObject> send(EventOut eventOut) {
        return send(eventOut, new JSONObject());
    }

    public static CompletableFuture<JSONObject> send(EventOut eventOut, Map<String, Object> requestMap) {
        JSONObject requestJson = new JSONObject();
        requestMap.forEach((k, v) -> {
            try {
                requestJson.put(k, v);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        return send(eventOut, requestJson);
    }

    public static CompletableFuture<JSONObject> send(EventOut eventOut, JSONObject requestJson) {
        String url = "http://" + System.getenv("SYNC_HOST") + ":" + System.getenv("SYNC_PORT") + "/api/" + eventOut.name();
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(requestJson.toString(), MediaType.get("application/json")))
                .addHeader("Authorization", System.getenv("SYNC_AUTH"))
                .build();

        CompletableFuture<JSONObject> future = new CompletableFuture<>();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (ResponseBody responseBody = response.body()) {
                    JSONObject responseJson = new JSONObject(responseBody.string());
                    future.complete(responseJson);
                } catch (Throwable e) {
                    future.completeExceptionally(e);
                }
            }
        });

        return future;
    }

}
