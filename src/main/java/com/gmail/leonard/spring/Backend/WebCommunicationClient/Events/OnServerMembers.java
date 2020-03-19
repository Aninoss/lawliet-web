package com.gmail.leonard.spring.Backend.WebCommunicationClient.Events;

import com.gmail.leonard.spring.Backend.Pair;
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

public class OnServerMembers implements Emitter.Listener {

    private WebComClient webComClient;
    private LoadingCache<Long, Optional<CompletableFuture<Optional<Pair<Long, Long>>>>> serverMembersCountLoadingCache;

    public OnServerMembers(LoadingCache<Long, Optional<CompletableFuture<Optional<Pair<Long, Long>>>>> serverMembersCountLoadingCache) {
        this.serverMembersCountLoadingCache = serverMembersCountLoadingCache;
    }

    @Override
    public void call(Object... args) {
        JSONObject mainJSON = new JSONObject((String) args[0]);

        long userId = mainJSON.getLong("user_id");
        boolean success = mainJSON.getBoolean("success");

        Optional<CompletableFuture<Optional<Pair<Long, Long>>>> completableFutureOptional = serverMembersCountLoadingCache.getUnchecked(userId);
        serverMembersCountLoadingCache.invalidate(userId);
        if (!completableFutureOptional.isPresent()) return;

        CompletableFuture<Optional<Pair<Long, Long>>> completableFuture = completableFutureOptional.get();
        if (success) {
            Optional<Pair<Long, Long>> count = Optional.of(new Pair<>(mainJSON.getLong("members_online"), mainJSON.getLong("members_total")));
            completableFuture.complete(count);
        } else {
            completableFuture.complete(Optional.empty());
        }
    }
}
