package xyz.lawlietbot.spring.backend.payment.paddle;

import xyz.lawlietbot.spring.backend.payment.Currency;

import java.util.Map;

public class PaddlePriceOverview {

    private final Currency currency;
    private final Map<String, Price> prices;

    public PaddlePriceOverview(Currency currency, Map<String, Price> prices) {
        this.currency = currency;
        this.prices = prices;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Map<String, Price> getPrices() {
        return prices;
    }


    public static class Price {

        private final double currentPrice;
        private final Double previousPrice;
        private final boolean includesVat;

        public Price(double currentPrice, double previousPrice, boolean includesVat) {
            this.currentPrice = currentPrice;
            this.previousPrice = previousPrice;
            this.includesVat = includesVat;
        }

        public double getCurrentPrice() {
            return currentPrice;
        }

        public double getPreviousPrice() {
            return previousPrice;
        }

        public boolean getIncludesVat() {
            return includesVat;
        }

    }
}
