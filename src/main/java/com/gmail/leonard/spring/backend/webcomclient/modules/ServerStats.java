package com.gmail.leonard.spring.backend.webcomclient.modules;

import com.gmail.leonard.spring.backend.faq.FAQListContainer;
import com.gmail.leonard.spring.backend.serverstats.ServerStatsContainer;
import com.gmail.leonard.spring.backend.serverstats.ServerStatsSlot;
import com.gmail.leonard.spring.backend.webcomclient.WebComClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

public class ServerStats {

    public static CompletableFuture<JSONObject> fetchServerStats() {
        return WebComClient.getInstance().send(WebComClient.EVENT_SERVERSTATS, JSONObject.class);
    }

}
