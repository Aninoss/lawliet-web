package xyz.lawlietbot.spring.backend.payment;

import java.time.LocalDate;

public class Subscription {

    private final int subId;
    private final int planId;
    private final int quantity;
    private final String price;
    private final LocalDate nextPayment;

    public Subscription(int subId, int planId, int quantity, String price, LocalDate nextPayment) {
        this.subId = subId;
        this.planId = planId;
        this.quantity = quantity;
        this.price = price;
        this.nextPayment = nextPayment;
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

}
