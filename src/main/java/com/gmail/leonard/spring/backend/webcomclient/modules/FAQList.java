package com.gmail.leonard.spring.backend.webcomclient.modules;

import com.gmail.leonard.spring.backend.faq.FAQListContainer;
import com.gmail.leonard.spring.backend.webcomclient.WebComClient;

import java.util.concurrent.CompletableFuture;

public class FAQList {

    public static CompletableFuture<FAQListContainer> fetchFAQList() {
        return WebComClient.getInstance().send(WebComClient.EVENT_FAQLIST, FAQListContainer.class);
    }

}
