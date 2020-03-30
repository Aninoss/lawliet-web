package com.gmail.leonard.spring.Backend.WebCommunicationClient.Events;

import com.gmail.leonard.spring.Backend.FAQ.FAQListContainer;
import com.gmail.leonard.spring.Backend.FAQ.FAQListSlot;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.TransferCache;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONObject;

public class OnFAQList implements Emitter.Listener {

    private TransferCache transferCache;

    public OnFAQList(TransferCache transferCache) {
        this.transferCache = transferCache;
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

        transferCache.complete(FAQListContainer.getInstance(), FAQListContainer.class);
        System.out.println("FAQ list ready");
    }
}
