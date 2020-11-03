package com.gmail.leonard.spring.backend.webcomclient.modules;

import com.gmail.leonard.spring.backend.userdata.ServerListData;
import com.gmail.leonard.spring.backend.userdata.SessionData;
import com.gmail.leonard.spring.backend.webcomclient.WebComClient;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

public class Dashboard {

    public static CompletableFuture<ServerListData> fetchServerListData(SessionData sessionData) {
        if (sessionData.isLoggedIn()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", sessionData.getDiscordUser().get().getId());

            //return WebComClient.getInstance().send(WebComClient.EVENT_SERVERLIST, jsonObject, ServerListData.class);
        }

        return CompletableFuture.completedFuture(null);
    }

    public static CompletableFuture<JSONObject> fetchServerMembersCount(SessionData sessionData, long serverId) {
        if (sessionData.isLoggedIn()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", sessionData.getDiscordUser().get().getId());
            jsonObject.put("server_id", serverId);

            //return WebComClient.getInstance().send(WebComClient.EVENT_SERVERMEMBERS, jsonObject, JSONObject.class);
        }

        return CompletableFuture.completedFuture(null);
    }

}
