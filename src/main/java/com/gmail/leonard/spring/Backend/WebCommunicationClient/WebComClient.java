package com.gmail.leonard.spring.Backend.WebCommunicationClient;

import com.gmail.leonard.spring.Backend.Pair;
import com.gmail.leonard.spring.Backend.UserData.ServerListData;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.Events.OnCommandList;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.Events.OnFAQList;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.Events.OnServerList;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.Events.OnServerMembers;
import com.gmail.leonard.spring.TimedCompletableFuture;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONObject;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class WebComClient {

    private static WebComClient instance = new WebComClient();

    private static final String EVENT_COMMANDLIST = "command_list";
    public static final String EVENT_FAQLIST = "faq_list";
    private static final String EVENT_SERVERLIST = "server_list";
    private static final String EVENT_SERVERMEMBERS = "server_members";
    private static final String EVENT_TOPGG = "topgg";
    private static final String EVENT_DONATEBOT_IO = "donatebot.io";
    private static final String EVENT_FEEDBACK = "feedback";


    private boolean started = false;
    private Socket socket;

    private LoadingCache<Long, Optional<TimedCompletableFuture<ServerListData>>> serverListLoadingCache;
    private LoadingCache<Long, Optional<TimedCompletableFuture<Optional<Pair<Long, Long>>>>> serverMembersCountLoadingCache;
    private List<TimedCompletableFuture<Void>> commandListRequests;
    private List<TimedCompletableFuture<Void>> faqListRequests;

    private WebComClient() {
        serverListLoadingCache = CacheBuilder.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<Long, Optional<TimedCompletableFuture<ServerListData>>>() {
                            @Override
                            @ParametersAreNonnullByDefault
                            public Optional<TimedCompletableFuture<ServerListData>> load(Long userId) {
                                return Optional.empty();
                            }
                        });

        serverMembersCountLoadingCache = CacheBuilder.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<Long, Optional<TimedCompletableFuture<Optional<Pair<Long, Long>>>>>() {
                            @Override
                            @ParametersAreNonnullByDefault
                            public Optional<TimedCompletableFuture<Optional<Pair<Long, Long>>>> load(Long userId) {
                                return Optional.empty();
                            }
                        });

        commandListRequests = Collections.synchronizedList(new ArrayList<>());
        faqListRequests = Collections.synchronizedList(new ArrayList<>());
    }

    public static WebComClient getInstance() { return instance; }

    public void start(int port) {
        if (started) return;
        started = true;

        IO.Options options = new IO.Options();
        options.reconnection = true;
        try {
            socket = IO.socket("http://127.0.0.1:" + port + "/");

            //Events
            socket.on(EVENT_COMMANDLIST, new OnCommandList(commandListRequests));
            socket.on(EVENT_FAQLIST, new OnFAQList(faqListRequests));
            socket.on(EVENT_SERVERLIST, new OnServerList(serverListLoadingCache));
            socket.on(EVENT_SERVERMEMBERS, new OnServerMembers(serverMembersCountLoadingCache));

            socket.connect();
            System.out.println("The WebCom client has been started!");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public TimedCompletableFuture<Void> updateCommandList() {
        socket.emit(EVENT_COMMANDLIST);
        TimedCompletableFuture<Void> completableFuture = new TimedCompletableFuture<>();
        commandListRequests.add(completableFuture);
        return completableFuture;
    }

    public TimedCompletableFuture<Void> updateFAQList() {
        socket.emit(EVENT_FAQLIST);
        TimedCompletableFuture<Void> completableFuture = new TimedCompletableFuture<>();
        faqListRequests.add(completableFuture);
        return completableFuture;
    }

    public TimedCompletableFuture<ServerListData> getServerListData(SessionData sessionData) {
        if (sessionData.isLoggedIn()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", sessionData.getUserId().get());
            socket.emit(EVENT_SERVERLIST, jsonObject.toString());

            TimedCompletableFuture<ServerListData> completableFuture = new TimedCompletableFuture<>();
            serverListLoadingCache.put(sessionData.getUserId().get(), Optional.of(completableFuture));
            return completableFuture;
        }

        return new TimedCompletableFuture<>(null);
    }

    public TimedCompletableFuture<Optional<Pair<Long, Long>>> getServerMembersCount(SessionData sessionData, long serverId) {
        if (sessionData.isLoggedIn()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", sessionData.getUserId());
            jsonObject.put("server_id", serverId);
            socket.emit(EVENT_SERVERMEMBERS, jsonObject.toString());

            TimedCompletableFuture<Optional<Pair<Long, Long>>> completableFuture = new TimedCompletableFuture<>();
            serverMembersCountLoadingCache.put(sessionData.getUserId().get(), Optional.of(completableFuture));
            return completableFuture;
        }

        return new TimedCompletableFuture<>(null);
    }

    public void sendTopGG(String data) { socket.emit(EVENT_TOPGG, data); }
    public void sendDonatebotIO(String data) { socket.emit(EVENT_DONATEBOT_IO, data); }

    public void sendFeedback(String reason, String explanation) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("reason", reason);
        jsonObject.put("explanation", explanation);
        socket.emit(EVENT_FEEDBACK, jsonObject.toString());
    }

}
