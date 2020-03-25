package com.gmail.leonard.spring;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public class TimedCompletableFuture<T> extends CompletableFuture<T> {

    public TimedCompletableFuture() {
        this(5);
    }

    public TimedCompletableFuture(int seconds) {
        if (seconds > 0) {
            Thread t = new Thread(() -> {
                try {
                    for (int i = 0; i < seconds; i++) {
                        Thread.sleep(1000);
                        if (isDone() || isCancelled() || isCompletedExceptionally()) return;
                    }
                    completeExceptionally(new TimeoutException());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            t.setPriority(1);
            t.setName("CustomCompletableFuture Timeout");
            t.start();
        }
    }

    public TimedCompletableFuture(T value) {
        this(0);
        complete(value);
    }


}
