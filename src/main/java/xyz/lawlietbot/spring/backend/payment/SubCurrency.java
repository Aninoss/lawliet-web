package xyz.lawlietbot.spring.backend.payment;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum SubCurrency {

    USD('$'),
    EUR('€'),
    GBP('£');

    private final static Logger LOGGER = LoggerFactory.getLogger(SubCurrency.class);
    private final static OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .cache(null)
            .build();

    public static HashMap<String, SubCurrency> currencyHashMap = new HashMap<>();

    private final char symbol;

    SubCurrency(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

    public static synchronized SubCurrency retrieveDefaultCurrency(String ipAddress) {
        if (currencyHashMap.containsKey(ipAddress)) {
            return currencyHashMap.get(ipAddress);
        }

        Request request = new Request.Builder()
                .url("https://ipapi.co/" + ipAddress + "/currency/")
                .header("User-Agent", "lawlietbot.xyz")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String currency = response.body().string();

            for (SubCurrency value : SubCurrency.values()) {
                if (value.name().equals(currency)) {
                    currencyHashMap.put(ipAddress, value);
                    return value;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error on currency retrieval", e);
        }

        currencyHashMap.put(ipAddress, USD);
        return USD;
    }

}
