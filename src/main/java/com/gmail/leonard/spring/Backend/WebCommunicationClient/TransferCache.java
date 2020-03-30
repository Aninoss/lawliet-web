package com.gmail.leonard.spring.Backend.WebCommunicationClient;

import com.github.appreciated.css.grid.sizes.Int;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public class TransferCache {

    private String event, key = null;
    private HashMap<Integer, CompletableFuture<?>> futuresMap = new HashMap<>();
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

        Thread t = new Thread(() -> {
            try {
                for (int i = 0; i < SECONDS; i++) {
                    Thread.sleep(1000);
                    if (future.isDone() || future.isCancelled() || future.isCompletedExceptionally()) return;
                }

                future.completeExceptionally(new TimeoutException());
                if (hasKey()) futuresMap.remove(dataKey);
                else futuresList.remove(future);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.setPriority(1);
        t.setName("CustomCompletableFuture Timeout");
        t.start();
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
