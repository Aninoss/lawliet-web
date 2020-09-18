package com.gmail.leonard.spring.backend.webcomclient.events;

import com.gmail.leonard.spring.backend.faq.FAQListContainer;
import com.gmail.leonard.spring.backend.faq.FAQListSlot;
import com.gmail.leonard.spring.backend.webcomclient.EventAbstract;
import com.gmail.leonard.spring.backend.webcomclient.TransferCache;
import org.json.JSONArray;
import org.json.JSONObject;

public class OnFAQList extends EventAbstract<FAQListContainer> {

    public OnFAQList(TransferCache transferCache) {
        super(transferCache);
    }

    @Override
    protected FAQListContainer processData(JSONObject mainJSON) {
        FAQListContainer.getInstance().clear();
        JSONArray arrayJSON = mainJSON.getJSONArray("slots");

        for (int i = 0; i < arrayJSON.length(); i++) {
            JSONObject slot = arrayJSON.getJSONObject(i);

            FAQListSlot faqListSlot = new FAQListSlot();
            faqListSlot.getQuestion().set(slot.getJSONObject("question"));
            faqListSlot.getAnswer().set(slot.getJSONObject("answer"));

            FAQListContainer.getInstance().add(faqListSlot);
        }

        return FAQListContainer.getInstance();
    }

}