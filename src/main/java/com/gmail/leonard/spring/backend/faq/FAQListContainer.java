package com.gmail.leonard.spring.backend.faq;

import com.gmail.leonard.spring.backend.webcomclient.modules.FAQList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class FAQListContainer {

    private final static Logger LOGGER = LoggerFactory.getLogger(FAQListContainer.class);
    private static FAQListContainer ourInstance = new FAQListContainer();
    private ArrayList<FAQListSlot> entries = new ArrayList<>();

    private FAQListContainer() {}

    public static FAQListContainer getInstance() { return ourInstance; }


    public synchronized FAQListSlot get(int n) {
        loadIfEmpty();
        return entries.get(n);
    }

    public void add(FAQListSlot faqListSlot) { entries.add(faqListSlot); }

    public void clear() { entries.clear(); }

    public int size() {
        loadIfEmpty();
        return entries.size();
    }


    private void loadIfEmpty() {
        if (entries.size() == 0) {
            LOGGER.info("Updating FAQ list");
            FAQList.fetchFAQList().join();
        }
    }

}
