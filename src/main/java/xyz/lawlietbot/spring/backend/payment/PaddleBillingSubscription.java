package xyz.lawlietbot.spring.backend.payment;

import org.json.JSONObject;

public class PaddleBillingSubscription {

    private final String subscriptionId;
    private final String customerId;
    private final int quantity;
    private final String status;
    private final boolean unlocksGuilds;

    public PaddleBillingSubscription(String subscriptionId, String customerId, int quantity, String status, boolean unlocksGuilds) {
        this.subscriptionId = subscriptionId;
        this.customerId = customerId;
        this.quantity = quantity;
        this.status = status;
        this.unlocksGuilds = unlocksGuilds;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public boolean isUnlocksGuilds() {
        return unlocksGuilds;
    }

    public static PaddleBillingSubscription fromJson(JSONObject jsonObject) {
        return new PaddleBillingSubscription(
                jsonObject.getString("subscription_id"),
                jsonObject.getString("customer_id"),
                jsonObject.getInt("quantity"),
                jsonObject.getString("status"),
                jsonObject.getBoolean("unlocks_guilds")
        );
    }

}
