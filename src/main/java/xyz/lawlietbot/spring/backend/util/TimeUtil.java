package xyz.lawlietbot.spring.backend.util;

import java.time.Duration;
import java.time.Instant;

public final class TimeUtil {

    private TimeUtil() {}

    public static long getMilisBetweenInstants(Instant instantBefore, Instant instantAfter) {
        Duration duration = Duration.between(instantBefore, instantAfter);
        return Math.max(1, duration.getSeconds() * 1000 + duration.getNano() / 1000000);
    }

}
