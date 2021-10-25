package xyz.lawlietbot.spring.backend.payment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public enum SubCurrency {

    USD('$'),
    EUR('€'),
    GBP('£');

    public static HashMap<String, SubCurrency> currencyHashMap = new HashMap<>();

    private final char symbol;

    SubCurrency(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

    public static synchronized SubCurrency retrieveDefaultCurrency(String ipAddress) throws IOException {
        if (currencyHashMap.containsKey(ipAddress)) {
            return currencyHashMap.get(ipAddress);
        }

        URL ipapi = new URL("http://ipapi.co/" + ipAddress + "/currency/");
        URLConnection c = ipapi.openConnection();
        c.setRequestProperty("User-Agent", "java-ipapi-client");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(c.getInputStream())
        );
        String currency = reader.readLine();
        reader.close();

        for (SubCurrency value : SubCurrency.values()) {
            if (value.name().equals(currency)) {
                currencyHashMap.put(ipAddress, value);
                return value;
            }
        }
        currencyHashMap.put(ipAddress, USD);
        return USD;
    }

}
