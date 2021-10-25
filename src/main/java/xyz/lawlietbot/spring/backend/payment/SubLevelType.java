package xyz.lawlietbot.spring.backend.payment;

public enum SubLevelType {

    BASIC(false),
    PRO(true);

    private final boolean recommended;

    SubLevelType(boolean recommended) {
        this.recommended = recommended;
    }

    public boolean isRecommended() {
        return recommended;
    }

}
