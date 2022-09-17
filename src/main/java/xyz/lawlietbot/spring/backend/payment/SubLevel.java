package xyz.lawlietbot.spring.backend.payment;

import java.util.Map;

public enum SubLevel {

    BASIC(Map.of(SubCurrency.USD, 300, SubCurrency.EUR, 300, SubCurrency.GBP, 250), false),
    PRO(Map.of(SubCurrency.USD, 500, SubCurrency.EUR, 450, SubCurrency.GBP, 400), true);

    private final boolean recommended;
    private final Map<SubCurrency, Integer> priceMap;

    SubLevel(Map<SubCurrency, Integer> priceMap, boolean recommended) {
        this.priceMap = priceMap;
        this.recommended = recommended;
    }

    public int getPrice(SubCurrency currency) {
        return priceMap.get(currency);
    }

    public boolean isRecommended() {
        return recommended;
    }

}
