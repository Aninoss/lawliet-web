package xyz.lawlietbot.spring.backend.faq;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.lawlietbot.spring.ExceptionLogger;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

public class FAQListContainer {

    private final static Logger LOGGER = LoggerFactory.getLogger(FAQListContainer.class);
    private static final FAQListContainer ourInstance = new FAQListContainer();
    private ArrayList<FAQListSlot> entries = new ArrayList<>();
    private Instant nextUpdate = Instant.now();

    private FAQListContainer() {
    }

    public static FAQListContainer getInstance() {
        return ourInstance;
    }


    public FAQListSlot get(int n) {
        loadIfEmpty();
        return entries.get(n);
    }

    public int size() {
        loadIfEmpty();
        return entries.size();
    }

    private synchronized void loadIfEmpty() {
        if (entries.size() == 0) {
            LOGGER.info("Loading FAQ list");
            entries = fetch().join();
            setNextUpdate();
        } else if (Instant.now().isAfter(nextUpdate)) {
            setNextUpdate();
            LOGGER.info("Updating FAQ list");
            fetch().thenAccept(e -> entries = e)
                    .exceptionally(ExceptionLogger.get());
        }
    }

    private void setNextUpdate() {
        nextUpdate = Instant.now().plus(20, ChronoUnit.MINUTES);
    }

    private CompletableFuture<ArrayList<FAQListSlot>> fetch() {
        CompletableFuture<ArrayList<FAQListSlot>> future = new CompletableFuture<>();
        SendEvent.sendToAnyCluster(EventOut.FAQ_LIST)
                .exceptionally(e -> {
                    future.completeExceptionally(e);
                    return null;
                })
                .thenAccept(responseJson -> {
                    if (responseJson.has("slots")) {
                        JSONArray arrayJSON = responseJson.getJSONArray("slots");

                        ArrayList<FAQListSlot> entries = new ArrayList<>();
                        for (int i = 0; i < arrayJSON.length(); i++) {
                            JSONObject slot = arrayJSON.getJSONObject(i);

                            FAQListSlot faqListSlot = new FAQListSlot();
                            faqListSlot.getQuestion().set(slot.getJSONObject("question"));
                            faqListSlot.getAnswer().set(slot.getJSONObject("answer"));

                            entries.add(faqListSlot);
                        }

                        future.complete(entries);
                    } else {
                        future.completeExceptionally(new NoSuchElementException("No entries"));
                    }
                });

        return future;
    }

}
