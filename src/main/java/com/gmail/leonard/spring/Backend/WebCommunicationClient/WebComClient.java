package com.gmail.leonard.spring.Backend.WebCommunicationClient;

import com.gmail.leonard.spring.Backend.CommandList.CommandListContainer;
import com.gmail.leonard.spring.Backend.CustomThread;
import com.gmail.leonard.spring.Backend.FAQ.FAQListContainer;
import com.gmail.leonard.spring.Backend.FeatureRequests.FRDynamicBean;
import com.gmail.leonard.spring.Backend.Feedback.FeedbackBean;
import com.gmail.leonard.spring.Backend.StringUtil;
import com.gmail.leonard.spring.Backend.UserData.ServerListData;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.Events.*;
import com.vaadin.flow.component.UI;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class WebComClient {

    private static final WebComClient instance = new WebComClient();
    public static WebComClient getInstance() { return instance; }

    private static final String CONNECTED = "connected";

    private static final String EVENT_COMMANDLIST = "command_list";
    private static final String EVENT_FAQLIST = "faq_list";
    private static final String EVENT_SERVERLIST = "server_list";
    private static final String EVENT_SERVERMEMBERS = "server_members";
    private static final String EVENT_FR_FETCH = "fr_fetch";
    private static final String EVENT_TOPGG = "topgg";
    private static final String EVENT_DONATEBOT_IO = "donatebot.io";
    private static final String EVENT_FEEDBACK = "feedback";
    private static final String EVENT_INVITE = "invite";

    final static Logger LOGGER = LoggerFactory.getLogger(WebComClient.class);
    private final TransferCache transferCache = new TransferCache();

    private boolean started = false;
    private Socket socket;

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
        return sendSecure(event, new JSONObject(), c);
    }

    private <T> CompletableFuture<T> sendSecure(String event, JSONObject jsonObject, Class<T> c) {
        blockWhileDisconnected();
        return send(event, jsonObject, c);
    }

    private <T> CompletableFuture<T> send(String event, Class<T> c) {
        return send(event, new JSONObject(), c);
    }

    private <T> CompletableFuture<T> send(String event, JSONObject jsonObject, Class<T> c) {
        jsonObject.put("id", StringUtil.getRandomString());
        CompletableFuture<T> completableFuture = transferCache.register(jsonObject, c);

        if (socket.connected()) {
            socket.emit(event, jsonObject.toString());
        } else {
            completableFuture.completeExceptionally(new NotYetConnectedException());
        }

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
            socket.on(CONNECTED, new OnConnected());

            socket.on(EVENT_COMMANDLIST, new OnCommandList(transferCache));
            socket.on(EVENT_FAQLIST, new OnFAQList(transferCache));
            socket.on(EVENT_SERVERLIST, new OnServerList(transferCache));
            socket.on(EVENT_FR_FETCH, new OnFRFetch(transferCache));

            socket.on(EVENT_SERVERMEMBERS, new OnEventJSONResponse(transferCache));

            socket.on(EVENT_TOPGG, new OnEventNoResponse(transferCache));
            socket.on(EVENT_DONATEBOT_IO, new OnEventNoResponse(transferCache));
            socket.on(EVENT_FEEDBACK, new OnEventNoResponse(transferCache));
            socket.on(EVENT_INVITE, new OnEventNoResponse(transferCache));

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

            return send(EVENT_SERVERMEMBERS, jsonObject, JSONObject.class);
        }

        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<FRDynamicBean> getFRFetch(SessionData sessionData) {
        JSONObject jsonObject = new JSONObject();
        if (sessionData.isLoggedIn()) jsonObject.put("user_id", sessionData.getUserId().get());
        return send(EVENT_FR_FETCH, jsonObject, FRDynamicBean.class);
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
