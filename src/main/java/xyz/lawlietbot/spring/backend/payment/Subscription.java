package xyz.lawlietbot.spring.backend.payment;

import java.time.LocalDate;

public class Subscription {

    private final long subId;
    private final long planId;
    private final int quantity;
    private final String price;
    private final LocalDate nextPayment;
    private final String updateUrl;
    private final String email;

    public Subscription(long subId, long planId, int quantity, String price, LocalDate nextPayment, String updateUrl,
                        String email) {
        this.subId = subId;
        this.planId = planId;
        this.quantity = quantity;
        this.price = price;
        this.nextPayment = nextPayment;
        this.updateUrl = updateUrl;
        this.email = email;
    }

    public long getSubId() {
        return subId;
    }

    public long getPlanId() {
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

    public String getEmail() {
        return email;
    }

}
