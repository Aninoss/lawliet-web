package xyz.lawlietbot.spring.backend.payment;

import java.time.Instant;

public class PremiumCode {

    private final String code;
    private final String level;
    private final Instant expiration;

    public PremiumCode(String code, String level, Instant expiration) {
        this.code = code;
        this.level = level;
        this.expiration = expiration;
    }

    public String getCode() {
        return code;
    }

    public String getLevel() {
        return level;
    }

    public Instant getExpiration() {
        return expiration;
    }

}
