package xyz.lawlietbot.spring.backend.payment;

import java.time.LocalDate;

public class Subscription {

    private final int subId;
    private final int planId;
    private final int quantity;
    private final String price;
    private final LocalDate nextPayment;
    private final String updateUrl;

    public Subscription(int subId, int planId, int quantity, String price, LocalDate nextPayment, String updateUrl) {
        this.subId = subId;
        this.planId = planId;
        this.quantity = quantity;
        this.price = price;
        this.nextPayment = nextPayment;
        this.updateUrl = updateUrl;
    }

    public int getSubId() {
        return subId;
    }

    public int getPlanId() {
        return planId;
    }

    public boolean isActive() {
        return nextPayment != null;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public LocalDate getNextPayment() {
        return nextPayment;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

}
