package xyz.lawlietbot.spring.backend.payment.paddle;

import xyz.lawlietbot.spring.backend.payment.Currency;

import java.util.Map;

public class PaddleSubscriptionPrices {

    private final Currency currency;
    private final Map<Long, Double> prices;
    private final boolean includesVat;

    public PaddleSubscriptionPrices(Currency currency, Map<Long, Double> prices, boolean includesVat) {
        this.currency = currency;
        this.prices = prices;
        this.includesVat = includesVat;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Map<Long, Double> getPrices() {
        return prices;
    }

    public boolean getIncludesVat() {
        return includesVat;
    }

}
