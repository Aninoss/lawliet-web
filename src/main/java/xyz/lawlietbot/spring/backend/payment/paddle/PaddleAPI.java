package xyz.lawlietbot.spring.backend.payment.paddle;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.lawlietbot.spring.backend.payment.ProductTxt2Img;
import xyz.lawlietbot.spring.backend.payment.SubDuration;
import xyz.lawlietbot.spring.backend.payment.SubLevel;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PaddleAPI {

    private static final String USER_AGENT = "Lawliet Discord Bot made by Aninoss#7220";

    private static final OkHttpClient client;

    static {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(25);
        ConnectionPool connectionPool = new ConnectionPool(5, 10, TimeUnit.SECONDS);
        client = new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .dispatcher(dispatcher)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .cache(null)
                .build();
    }

    public static JSONObject retrieveCheckout(String checkoutId) throws IOException {
        Request request = new Request.Builder()
                .url("https://" + System.getenv("PADDLE_SUBDOMAIN_PREFIX") + "checkout.paddle.com/api/1.0/order?checkout_id=" + checkoutId)
                .addHeader("User-Agent", USER_AGENT)
                .build();

        return run(request);
    }

    public static JSONObject retrieveSubscriptionPrices(String customerIpAddress, int group) throws IOException {
        StringBuilder productIds = new StringBuilder();
        for (SubDuration duration : SubDuration.values()) {
            for (SubLevel level : SubLevel.values()) {
                if (productIds.length() > 0) {
                    productIds.append(",");
                }
                productIds.append(PaddleManager.getPlanId(duration, level, group));
            }
        }

        Request request = new Request.Builder()
                .url("https://" + System.getenv("PADDLE_SUBDOMAIN_PREFIX") + "checkout.paddle.com/api/2.0/prices?customer_ip=" + customerIpAddress + "&product_ids=" + productIds)
                .addHeader("User-Agent", USER_AGENT)
                .build();

        return run(request);
    }

    public static JSONObject retrieveProductPrices(String customerIpAddress) throws IOException {
        JSONObject requestJson = new JSONObject();
        requestJson.put("customer_ip_address", customerIpAddress);

        JSONArray itemsArray = new JSONArray();
        for (ProductTxt2Img product : ProductTxt2Img.values()) {
            JSONObject itemJson = new JSONObject();
            itemJson.put("price_id", product.getPriceId());
            itemJson.put("quantity", 1);
            itemsArray.put(itemJson);
        }
        requestJson.put("items", itemsArray);

        Request request = new Request.Builder()
                .url("https://" + System.getenv("PADDLE_SUBDOMAIN_BILLING") + ".paddle.com/pricing-preview")
                .post(RequestBody.create(requestJson.toString(), MediaType.get("application/json")))
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Authorization", "Bearer " + System.getenv("PADDLE_AUTH"))
                .build();

        return run(request);
    }

    public static boolean subscriptionSetPaused(long subId, boolean paused) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("vendor_id", System.getenv("PADDLE_VENDOR_ID"))
                .add("vendor_auth_code", System.getenv("PADDLE_AUTH"))
                .add("subscription_id", String.valueOf(subId))
                .add("pause", String.valueOf(paused))
                .build();

        Request request = new Request.Builder()
                .url("https://" + System.getenv("PADDLE_SUBDOMAIN_PREFIX") + "vendors.paddle.com/api/2.0/subscription/users/update")
                .post(formBody)
                .addHeader("User-Agent", USER_AGENT)
                .build();

        JSONObject responseJson = run(request);
        return responseJson.getBoolean("success");
    }

    public static boolean subscriptionCancel(long subId) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("vendor_id", System.getenv("PADDLE_VENDOR_ID"))
                .add("vendor_auth_code", System.getenv("PADDLE_AUTH"))
                .add("subscription_id", String.valueOf(subId))
                .build();

        Request request = new Request.Builder()
                .url("https://" + System.getenv("PADDLE_SUBDOMAIN_PREFIX") + "vendors.paddle.com/api/2.0/subscription/users_cancel")
                .post(formBody)
                .addHeader("User-Agent", USER_AGENT)
                .build();

        JSONObject responseJson = run(request);
        return responseJson.getBoolean("success");
    }

    private static JSONObject run(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            return new JSONObject(response.body().string());
        }
    }

}
