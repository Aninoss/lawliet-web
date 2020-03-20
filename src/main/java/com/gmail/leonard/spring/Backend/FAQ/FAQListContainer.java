package com.gmail.leonard.spring.Backend.FAQ;

import com.gmail.leonard.spring.Backend.LanguageString;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.WebComClient;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FAQListContainer {

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
            try {
                System.out.println("Update faq list...");
                WebComClient.getInstance().updateFAQList().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

}
