package xyz.lawlietbot.spring.backend.payment;

public enum ProductPremium {

    BASIC_MONTH("BASIC", 30),
    PRO_MONTH("PRO", 30),
    BASIC_YEAR("BASIC", 365),
    MONTH_YEAR("PRO", 365);

    private final String plan;
    private final int days;

    ProductPremium(String plan, int days) {
        this.plan = plan;
        this.days = days;
    }

    public String getPriceId() {
        return System.getenv("PADDLE_PREMIUM_IDS").split(",")[ordinal()];
    }

    public String getPlan() {
        return plan;
    }

    public int getDays() {
        return days;
    }

    public static ProductPremium fromPriceId(String priceId) {
        for (ProductPremium value : values()) {
            if (value.getPriceId().equals(priceId)) {
                return value;
            }
        }
        return null;
    }

}
