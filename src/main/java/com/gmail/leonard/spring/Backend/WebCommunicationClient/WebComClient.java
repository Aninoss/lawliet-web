package com.gmail.leonard.spring.Backend.WebCommunicationClient;

import com.gmail.leonard.spring.Backend.CommandList.CommandListContainer;
import com.gmail.leonard.spring.Backend.CustomThread;
import com.gmail.leonard.spring.Backend.FAQ.FAQListContainer;
import com.gmail.leonard.spring.Backend.Feedback.FeedbackBean;
import com.gmail.leonard.spring.Backend.UserData.ServerListData;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.Events.*;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class WebComClient {

    private static final WebComClient instance = new WebComClient();
    public static WebComClient getInstance() { return instance; }

    private static final String EVENT_COMMANDLIST = "command_list";
    public static final String EVENT_FAQLIST = "faq_list";
    private static final String EVENT_SERVERLIST = "server_list";
    private static final String EVENT_SERVERMEMBERS = "server_members";
    private static final String EVENT_TOPGG = "topgg";
    private static final String EVENT_DONATEBOT_IO = "donatebot.io";
    private static final String EVENT_FEEDBACK = "feedback";
    private static final String EVENT_INVITE = "invite";

    final static Logger LOGGER = LoggerFactory.getLogger(WebComClient.class);
    private final HashMap<String, TransferCache> transferCaches = new HashMap<>();

    private boolean started = false;
    private Socket socket;

    private WebComClient() {
        addTransferCaches(
                new TransferCache(EVENT_COMMANDLIST),
                new TransferCache(EVENT_FAQLIST),
                new TransferCache(EVENT_SERVERLIST, "user_id"),
                new TransferCache(EVENT_SERVERMEMBERS, "user_id"),
                new TransferCache(EVENT_TOPGG),
                new TransferCache(EVENT_DONATEBOT_IO),
                new TransferCache(EVENT_FEEDBACK),
                new TransferCache(EVENT_INVITE)
        );
    }

    private void addTransferCaches(TransferCache... newTransferCaches) {
        Stream.of(newTransferCaches)
                .forEach(transferCache -> transferCaches.put(transferCache.getEvent(), transferCache));
    }

    private synchronized void blockWhileDisconnected() {
        if (!socket.connected()) {
            try {
                while (!socket.connected()) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted", e);
            }
        }
    }

    private <T> CompletableFuture<T> sendSecure(String event, Class<T> c) {
        return sendSecure(event, null, c);
    }

    private <T> CompletableFuture<T> sendSecure(String event, JSONObject jsonObject, Class<T> c) {
        blockWhileDisconnected();
        return send(event, jsonObject, c);
    }

    private <T> CompletableFuture<T> send(String event, Class<T> c) {
        return send(event, null, c);
    }

    private <T> CompletableFuture<T> send(String event, JSONObject jsonObject, Class<T> c) {
        CompletableFuture<T> completableFuture = transferCaches.get(event).register(jsonObject, c);

        if (socket.connected()) {
            if (jsonObject != null) socket.emit(event, jsonObject.toString());
            else socket.emit(event);
        } else
            completableFuture.completeExceptionally(new NotYetConnectedException());

        return completableFuture;
    }

    public void start(int port) {
        if (started) return;
        started = true;

        IO.Options options = new IO.Options();
        options.reconnection = true;
        try {
            socket = IO.socket("http://127.0.0.1:" + port + "/");

            //Events
            socket.on(EVENT_COMMANDLIST, new OnCommandList(transferCaches.get(EVENT_COMMANDLIST)));
            socket.on(EVENT_FAQLIST, new OnFAQList(transferCaches.get(EVENT_FAQLIST)));
            socket.on(EVENT_SERVERLIST, new OnServerList(transferCaches.get(EVENT_SERVERLIST)));
            socket.on(EVENT_SERVERMEMBERS, new OnServerMembers(transferCaches.get(EVENT_SERVERMEMBERS)));

            socket.on(EVENT_TOPGG, new OnGenericResponseless(transferCaches.get(EVENT_TOPGG)));
            socket.on(EVENT_DONATEBOT_IO, new OnGenericResponseless(transferCaches.get(EVENT_DONATEBOT_IO)));
            socket.on(EVENT_FEEDBACK, new OnGenericResponseless(transferCaches.get(EVENT_FEEDBACK)));
            socket.on(EVENT_INVITE, new OnGenericResponseless(transferCaches.get(EVENT_INVITE)));

            socket.connect();
            LOGGER.info("The WebCom client has been started!");
        } catch (URISyntaxException e) {
            LOGGER.error("Could not initialize web com client", e);
        }
    }

    public CompletableFuture<CommandListContainer> updateCommandList() {
        return send(EVENT_COMMANDLIST, CommandListContainer.class);
    }

    public CompletableFuture<FAQListContainer> updateFAQList() {
        return send(EVENT_FAQLIST, FAQListContainer.class);
    }

    public CompletableFuture<ServerListData> getServerListData(SessionData sessionData) {
        if (sessionData.isLoggedIn()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", sessionData.getUserId().get());
            return send(EVENT_SERVERLIST, jsonObject, ServerListData.class);
        }

        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<JSONObject> getServerMembersCount(SessionData sessionData, long serverId) {
        if (sessionData.isLoggedIn()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", sessionData.getUserId().get());
            jsonObject.put("server_id", serverId);
            socket.emit(EVENT_SERVERMEMBERS, jsonObject.toString());

            return send(EVENT_SERVERMEMBERS, jsonObject, JSONObject.class);
        }

        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> sendTopGG(JSONObject jsonObject) {
        return sendSecure(EVENT_TOPGG, jsonObject, Void.class);
    }

    public CompletableFuture<Void> sendDonatebotIO(JSONObject jsonObject) {
        return sendSecure(EVENT_DONATEBOT_IO, jsonObject, Void.class);
    }

    public CompletableFuture<Void> sendFeedback(FeedbackBean feedbackBean, Long serverId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cause", feedbackBean.getCause(feedbackBean));
        jsonObject.put("reason", feedbackBean.getReason(feedbackBean));
        if (feedbackBean.getContact(feedbackBean))
            jsonObject.put("username_discriminated", feedbackBean.getUsernameDiscriminated(feedbackBean));
        if (serverId != null)
            jsonObject.put("server_id", serverId);
        return send(EVENT_FEEDBACK, jsonObject, Void.class);
    }

    public void sendInvite(String type) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type.toLowerCase());
        new CustomThread(() -> {
            try {
                sendSecure(EVENT_INVITE, jsonObject, Void.class).get();
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Exception while sending invite data", e);
            }
        }, "transfer_invite_data").start();
    }

}
