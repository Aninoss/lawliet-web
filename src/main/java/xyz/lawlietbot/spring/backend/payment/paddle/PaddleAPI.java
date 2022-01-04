package xyz.lawlietbot.spring.backend.payment.paddle;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.*;
import org.json.JSONObject;

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
                .url("https://sandbox-checkout.paddle.com/api/1.0/order?checkout_id=" + checkoutId) //TODO
                .addHeader("User-Agent", USER_AGENT)
                .build();

        return run(request);
    }

    private static JSONObject run(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            return new JSONObject(response.body().string());
        }
    }

}
