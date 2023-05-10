package xyz.lawlietbot.spring.backend.payment;

import java.util.Map;

public enum SubLevel {

    BASIC(Map.of(SubCurrency.USD, 300, SubCurrency.EUR, 300, SubCurrency.GBP, 250), false, true),
    PRO(Map.of(SubCurrency.USD, 500, SubCurrency.EUR, 450, SubCurrency.GBP, 400), true, true),
    ULTIMATE(Map.of(SubCurrency.USD, 1500, SubCurrency.EUR, 1350, SubCurrency.GBP, 1200), false, false);

    private final Map<SubCurrency, Integer> priceMap;
    private final boolean recommended;
    private final boolean buyDirectly;

    SubLevel(Map<SubCurrency, Integer> priceMap, boolean recommended, boolean buyDirectly) {
        this.priceMap = priceMap;
        this.recommended = recommended;
        this.buyDirectly = buyDirectly;
    }

    public int getPrice(SubCurrency currency) {
        return priceMap.get(currency);
    }

    public boolean isRecommended() {
        return recommended;
    }

    public boolean buyDirectly() {
        return buyDirectly;
    }
}
