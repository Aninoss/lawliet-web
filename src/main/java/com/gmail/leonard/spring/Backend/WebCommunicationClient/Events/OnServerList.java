package com.gmail.leonard.spring.Backend.WebCommunicationClient.Events;

import com.gmail.leonard.spring.Backend.CommandList.CommandListCategory;
import com.gmail.leonard.spring.Backend.CommandList.CommandListContainer;
import com.gmail.leonard.spring.Backend.CommandList.CommandListSlot;
import com.gmail.leonard.spring.Backend.UserData.DiscordServerData;
import com.gmail.leonard.spring.Backend.UserData.ServerListData;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.WebComClient;
import com.google.common.cache.LoadingCache;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class OnServerList implements Emitter.Listener {

    private WebComClient webComClient;
    private LoadingCache<Long, Optional<CompletableFuture<ServerListData>>> serverListLoadingCache;

    public OnServerList(LoadingCache<Long, Optional<CompletableFuture<ServerListData>>> serverListLoadingCache) {
        this.serverListLoadingCache = serverListLoadingCache;
    }

    @Override
    public void call(Object... args) {
        JSONObject mainJSON = new JSONObject((String) args[0]);

        long userId = mainJSON.getLong("user_id");
        Optional<CompletableFuture<ServerListData>> completableFutureOptional;

        completableFutureOptional = serverListLoadingCache.getUnchecked(userId);
        serverListLoadingCache.invalidate(userId);
        if (!completableFutureOptional.isPresent()) return;

        ArrayList<SessionData> sessionDataList = SessionData.getSessionData(userId);

        ServerListData serverListData = null;
        for (SessionData sessionData : sessionDataList) {
            serverListData = sessionData.getServerListData();
            serverListData.clear();
            JSONArray serverArray = mainJSON.getJSONArray("server_list");

            for (int i = 0; i < serverArray.length(); i++) {
                JSONObject serverObject = serverArray.getJSONObject(i);

                long serverId = serverObject.getLong("server_id");
                String name = serverObject.getString("name");
                Optional<String> iconURL = Optional.empty();
                if (serverObject.has("icon")) {
                    iconURL = Optional.of(serverObject.getString("icon"));
                }

                DiscordServerData discordServerData = new DiscordServerData(serverId, name, iconURL);
                serverListData.put(discordServerData);
            }
        }

        if (serverListData != null) {
            CompletableFuture<ServerListData> completableFuture = completableFutureOptional.get();
            completableFuture.complete(serverListData);
        }
    }
}
