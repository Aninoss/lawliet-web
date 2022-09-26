package xyz.lawlietbot.spring.syncserver;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SyncUtil {

    public static CompletableFuture<Boolean> sendRequestCanPost(long userId) {
        return SendEvent.send(EventOut.FR_CAN_POST, Map.of("user_id", userId))
                .thenApply(responseJson -> responseJson.getBoolean("success"));
    }

}
