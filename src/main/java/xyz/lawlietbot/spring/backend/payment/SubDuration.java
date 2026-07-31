package xyz.lawlietbot.spring.backend.payment;

public enum SubDuration {

    MONTHLY(1, 1), ANNUALLY(9, 12);

    private final int priceFactor;
    private final int cadence;

    SubDuration(int priceFactor, int cadence) {
        this.priceFactor = priceFactor;
        this.cadence = cadence;
    }

    public int getPriceFactor() {
        return priceFactor;
    }

    public int getCadence() {
        return cadence;
    }

}