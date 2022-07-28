package xyz.lawlietbot.spring.syncserver;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.json.JSONObject;

public class SyncUtil {

    public static CompletableFuture<Boolean> sendRequestCanPost(long userId) {
        return SendEvent.send(EventOut.FR_CAN_POST, Map.of("user_id", userId))
                .thenApply(responseJson -> responseJson.getBoolean("success"));
    }

    public static CompletableFuture<Void> sendStripe(long userId, String title, String desc, int subId, boolean unlocksServer) {
        JSONObject json = new JSONObject();
        json.put("user_id", userId);
        json.put("title", title);
        json.put("desc", desc);
        json.put("sub_id", subId);
        json.put("unlocks_server", unlocksServer);

        return SendEvent.send(EventOut.STRIPE, json)
                .thenApply(r -> null);
    }

}
