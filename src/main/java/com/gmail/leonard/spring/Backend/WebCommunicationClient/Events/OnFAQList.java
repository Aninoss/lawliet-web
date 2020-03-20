package com.gmail.leonard.spring.Backend.WebCommunicationClient.Events;

import com.gmail.leonard.spring.Backend.CommandList.CommandListCategory;
import com.gmail.leonard.spring.Backend.CommandList.CommandListContainer;
import com.gmail.leonard.spring.Backend.CommandList.CommandListSlot;
import com.gmail.leonard.spring.Backend.FAQ.FAQListContainer;
import com.gmail.leonard.spring.Backend.FAQ.FAQListSlot;
import com.gmail.leonard.spring.Backend.LanguageString;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OnFAQList implements Emitter.Listener {

    private List<CompletableFuture<Void>> faqListRequests;

    public OnFAQList(List<CompletableFuture<Void>> faqListRequests) {
        this.faqListRequests = faqListRequests;
    }

    @Override
    public void call(Object... args) {
        FAQListContainer.getInstance().clear();
        JSONArray mainJSON = new JSONArray((String) args[0]);

        for (int i = 0; i < mainJSON.length(); i++) {
            JSONObject slot = mainJSON.getJSONObject(i);

            FAQListSlot faqListSlot = new FAQListSlot();
            faqListSlot.getQuestion().set(slot.getJSONObject("question"));
            faqListSlot.getAnswer().set(slot.getJSONObject("answer"));

            FAQListContainer.getInstance().add(faqListSlot);
        }

        for(CompletableFuture<Void> cf: faqListRequests)
            cf.complete(null);
        faqListRequests.clear();

        System.out.println("FAQ list ready");
    }
}
