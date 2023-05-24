package xyz.lawlietbot.spring.syncserver;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.lawlietbot.spring.backend.payment.Subscription;

public class SyncUtil {

    public static CompletableFuture<Boolean> sendRequestCanPost(long userId) {
        return SendEvent.send(EventOut.FR_CAN_POST, Map.of("user_id", userId))
                .thenApply(responseJson -> responseJson.getBoolean("success"));
    }

    public static CompletableFuture<List<Subscription>> retrievePaddleSubscriptions(long userId, long reloadSubId) {
        JSONObject json = new JSONObject();
        json.put("user_id", userId);
        if (reloadSubId > 0) {
            json.put("reload_sub_id", reloadSubId);
        }

        return SendEvent.send(EventOut.PADDLE_SUBS, json)
                .thenApply(r -> {
                    JSONArray subsJson = r.getJSONArray("subscriptions");
                    ArrayList<Subscription> subscriptions = new ArrayList<>();
                    for (int i = 0; i < subsJson.length(); i++) {
                        JSONObject subJson = subsJson.getJSONObject(i);
                        subscriptions.add(new Subscription(
                                subJson.getLong("sub_id"),
                                subJson.getLong("plan_id"),
                                subJson.getInt("quantity"),
                                subJson.getString("total_price"),
                                subJson.has("next_payment") ? LocalDate.parse(subJson.getString("next_payment")) : null,
                                subJson.getString("update_url"),
                                subJson.getString("email")
                        ));
                    }
                    return Collections.unmodifiableList(subscriptions);
                });
    }

}
