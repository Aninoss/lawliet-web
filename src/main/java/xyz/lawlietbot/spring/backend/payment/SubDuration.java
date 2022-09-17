package xyz.lawlietbot.spring.backend.payment;

import com.stripe.param.checkout.SessionCreateParams;

public enum SubDuration {

    MONTHLY(SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH, 1),
    YEARLY(SessionCreateParams.LineItem.PriceData.Recurring.Interval.YEAR, 8);


    private final SessionCreateParams.LineItem.PriceData.Recurring.Interval interval;
    private final int priceFactor;

    SubDuration(SessionCreateParams.LineItem.PriceData.Recurring.Interval interval, int priceFactor) {
        this.interval = interval;
        this.priceFactor = priceFactor;
    }

    public SessionCreateParams.LineItem.PriceData.Recurring.Interval getInterval() {
        return interval;
    }

    public int getPriceFactor() {
        return priceFactor;
    }

}
