package com.gmail.leonard.spring.Backend.WebCommunicationClient.Modules;

import com.gmail.leonard.spring.Backend.FAQ.FAQListContainer;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.WebComClient;

import java.util.concurrent.CompletableFuture;

public class FAQList {

    public static CompletableFuture<FAQListContainer> fetchFAQList() {
        return WebComClient.getInstance().send(WebComClient.EVENT_FAQLIST, FAQListContainer.class);
    }

}
