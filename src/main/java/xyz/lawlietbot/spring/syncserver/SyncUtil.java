package xyz.lawlietbot.spring.syncserver;

import org.json.JSONException;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SyncUtil {

    public static CompletableFuture<Boolean> sendRequestCanPost(long userId) {
        return SendEvent.send(EventOut.FR_CAN_POST, Map.of("user_id", userId))
                .thenApply(responseJson -> {
                    try {
                        return responseJson.getBoolean("success");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}
