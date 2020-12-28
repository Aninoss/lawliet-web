package com.gmail.leonard.spring.backend.faq;

import com.gmail.leonard.spring.syncserver.SendEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class FAQListContainer {

    private final static Logger LOGGER = LoggerFactory.getLogger(FAQListContainer.class);
    private static final FAQListContainer ourInstance = new FAQListContainer();
    private final ArrayList<FAQListSlot> entries = new ArrayList<>();

    private FAQListContainer() {
    }

    public static FAQListContainer getInstance() {
        return ourInstance;
    }


    public FAQListSlot get(int n) {
        loadIfEmpty();
        return entries.get(n);
    }

    public void clear() {
        entries.clear();
    }

    public int size() {
        loadIfEmpty();
        return entries.size();
    }

    private void add(FAQListSlot faqListSlot) {
        entries.add(faqListSlot);
    }

    private synchronized void loadIfEmpty() {
        if (entries.size() == 0) {
            LOGGER.info("Updating FAQ list");
            JSONObject responseJson = SendEvent.sendRequestFAQList().join();
            clear();
            if (responseJson.has("slots")) {
                JSONArray arrayJSON = responseJson.getJSONArray("slots");

                for (int i = 0; i < arrayJSON.length(); i++) {
                    JSONObject slot = arrayJSON.getJSONObject(i);

                    FAQListSlot faqListSlot = new FAQListSlot();
                    faqListSlot.getQuestion().set(slot.getJSONObject("question"));
                    faqListSlot.getAnswer().set(slot.getJSONObject("answer"));

                    add(faqListSlot);
                }
            }
        }
    }

}
