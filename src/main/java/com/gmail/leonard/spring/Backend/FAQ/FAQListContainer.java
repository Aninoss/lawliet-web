package com.gmail.leonard.spring.Backend.FAQ;

import com.gmail.leonard.spring.Backend.LanguageString;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.Events.OnFAQList;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.Modules.FAQList;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.WebComClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FAQListContainer {

    final static Logger LOGGER = LoggerFactory.getLogger(FAQListContainer.class);
    private static FAQListContainer ourInstance = new FAQListContainer();
    private ArrayList<FAQListSlot> entries = new ArrayList<>();

    private FAQListContainer() {}

    public static FAQListContainer getInstance() { return ourInstance; }


    public FAQListSlot get(int n) {
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
