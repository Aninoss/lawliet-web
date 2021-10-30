package xyz.lawlietbot.spring.backend;

import java.time.Duration;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.vaadin.flow.component.UI;

public class UICache {

    private static final Cache<Long, UI> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();

    public static void put(long userId, UI ui) {
        cache.put(userId, ui);
    }

    public static UI get(long userId) {
        return cache.getIfPresent(userId);
    }

}
