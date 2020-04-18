package com.gmail.leonard.spring.Backend.WebCommunicationClient;

import com.github.appreciated.css.grid.sizes.Int;
import com.gmail.leonard.spring.Backend.CustomThread;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public class TransferCache {

    final static Logger LOGGER = LoggerFactory.getLogger(TransferCache.class);

    private final String event;
    private String key = null;
    private final HashMap<Integer, CompletableFuture<?>> futuresMap = new HashMap<>();
    private ArrayList<CompletableFuture<?>> futuresList = new ArrayList<>();

    public TransferCache(String event) {
        this.event = event;
    }

    public TransferCache(String event, String key) {
        this(event);
        this.key = key;
    }

    public <T> CompletableFuture<T> register(JSONObject data, Class<T> c) {
        CompletableFuture<T> future = new CompletableFuture<>();
        if (hasKey()) {
            int dataKey = data.get(key).hashCode();
            futuresMap.put(dataKey, future);
            startTimer(dataKey, future, c);
        } else {
            futuresList.add(future);
            startTimer(null, future, c);
        }

        return future;
    }

    private <T> void startTimer(Integer dataKey, CompletableFuture<T> future, Class<T> c) {
        final int SECONDS = 5;

        new CustomThread(() -> {
            try {
                for (int i = 0; i < SECONDS; i++) {
                    Thread.sleep(1000);
                    if (future.isDone() || future.isCancelled() || future.isCompletedExceptionally()) return;
                }

                future.completeExceptionally(new TimeoutException());
                if (hasKey()) futuresMap.remove(dataKey);
                else futuresList.remove(future);
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted", e);
            }
        }, "CustomCompletableFuture Timeout", 1).start();
    }

    public <T> void complete(@NonNull JSONObject data, T value, Class<T> c) {
        if (hasKey()) {
            int dataKey = data.get(key).hashCode();
            Optional.ofNullable(futuresMap.get(dataKey)).ifPresent(future -> {
                ((CompletableFuture<T>)future).complete(value);
                futuresMap.remove(dataKey);
            });
        } else {
            complete(value, c);
        }
    }

    public <T> void complete(T value, Class<T> c) {
        if (!hasKey()) {
            futuresList.forEach(future -> ((CompletableFuture<T>)future).complete(value));
            futuresList = new ArrayList<>();
        } else {
            if (c == JSONObject.class) complete((JSONObject)value, value, c);
            else throw new RuntimeException("Wrong tranfer cache access");
        }
    }

    public boolean hasKey() {
        return key != null;
    }

    public String getEvent() {
        return event;
    }

}
