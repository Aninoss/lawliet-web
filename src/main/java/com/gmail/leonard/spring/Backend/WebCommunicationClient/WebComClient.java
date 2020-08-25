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

    public static final String EVENT_COMMANDLIST = "command_list";
    public static final String EVENT_FAQLIST = "faq_list";
    public static final String EVENT_SERVERLIST = "server_list";
    public static final String EVENT_SERVERMEMBERS = "server_members";
    public static final String EVENT_FR_FETCH = "fr_fetch";
    public static final String EVENT_FR_BOOST = "fr_boost";
    public static final String EVENT_TOPGG = "topgg";
    public static final String EVENT_DONATEBOT_IO = "donatebot.io";
    public static final String EVENT_FEEDBACK = "feedback";
    public static final String EVENT_INVITE = "invite";

    final static Logger LOGGER = LoggerFactory.getLogger(WebComClient.class);
    private final TransferCache transferCache = new TransferCache();

    private boolean started = false;
    private Socket socket;

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
            socket.on(EVENT_SERVERMEMBERS, new OnEventJSONResponse(transferCache));
            socket.on(EVENT_FR_FETCH, new OnFRFetch(transferCache));
            socket.on(EVENT_FR_BOOST, new OnEventJSONResponse(transferCache));

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

    public <T> CompletableFuture<T> sendSecure(String event, Class<T> c) {
        return sendSecure(event, new JSONObject(), c);
    }

    public <T> CompletableFuture<T> sendSecure(String event, JSONObject jsonObject, Class<T> c) {
        blockWhileDisconnected();
        return send(event, jsonObject, c);
    }

    public <T> CompletableFuture<T> send(String event, Class<T> c) {
        return send(event, new JSONObject(), c);
    }

    public <T> CompletableFuture<T> send(String event, JSONObject jsonObject, Class<T> c) {
        jsonObject.put("id", StringUtil.getRandomString());
        CompletableFuture<T> completableFuture = transferCache.register(jsonObject, c);

        if (socket.connected()) {
            socket.emit(event, jsonObject.toString());
        } else {
            completableFuture.completeExceptionally(new NotYetConnectedException());
        }

        return completableFuture;
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

}
