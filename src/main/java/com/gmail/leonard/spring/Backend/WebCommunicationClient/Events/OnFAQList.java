package com.gmail.leonard.spring.Backend.WebCommunicationClient.Events;

import com.gmail.leonard.spring.Backend.FAQ.FAQListContainer;
import com.gmail.leonard.spring.Backend.FAQ.FAQListSlot;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.TransferCache;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnFAQList implements Emitter.Listener {

    final static Logger LOGGER = LoggerFactory.getLogger(OnFAQList.class);
    private final TransferCache transferCache;

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
        LOGGER.info("FAQ list ready");
    }
}
