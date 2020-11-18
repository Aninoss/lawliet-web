package com.gmail.leonard.spring.backend.webcomclient;

import com.gmail.leonard.spring.backend.StringUtil;
import com.gmail.leonard.spring.backend.webcomclient.events.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import java.util.concurrent.CompletableFuture;

public class WebComClient {

    private static final WebComClient instance = new WebComClient();
    public static WebComClient getInstance() { return instance; }

    public static final String EVENT_COMMANDLIST = "command_list";
    public static final String EVENT_FAQLIST = "faq_list";
    public static final String EVENT_FR_FETCH = "fr_fetch";
    public static final String EVENT_FR_BOOST = "fr_boost";
    public static final String EVENT_FR_CAN_POST = "fr_can_post";
    public static final String EVENT_FR_POST = "fr_post";
    public static final String EVENT_TOPGG = "topgg";
    public static final String EVENT_TOPGG_ANINOSS = "topgg_aninoss";
    public static final String EVENT_DONATEBOT_IO = "donatebot.io";
    public static final String EVENT_INVITE = "invite";
    public static final String EVENT_SERVERSTATS = "serverstats";

    private final static Logger LOGGER = LoggerFactory.getLogger(WebComClient.class);
    private final TransferCache transferCache = new TransferCache();

    private boolean started = false;
    private CustomWebSocketClient client;

    public void start(int port) {
        if (started) return;
        started = true;

        try {
            client = new CustomWebSocketClient(new URI("ws://localhost:" + port));
            
            //Events
            client.addConnectedHandler(new OnConnected());

            client.addEventHandler(EVENT_COMMANDLIST, new OnEventJSONResponse(transferCache));
            client.addEventHandler(EVENT_FAQLIST, new OnFAQList(transferCache));
            client.addEventHandler(EVENT_FR_FETCH, new OnFRFetch(transferCache));
            client.addEventHandler(EVENT_FR_BOOST, new OnEventJSONResponse(transferCache));
            client.addEventHandler(EVENT_FR_CAN_POST, new OnFRCanPost(transferCache));
            client.addEventHandler(EVENT_FR_POST, new OnEventNoResponse(transferCache));
            client.addEventHandler(EVENT_SERVERSTATS, new OnEventJSONResponse(transferCache));

            client.addEventHandler(EVENT_TOPGG, new OnEventNoResponse(transferCache));
            client.addEventHandler(EVENT_TOPGG_ANINOSS, new OnEventNoResponse(transferCache));
            client.addEventHandler(EVENT_DONATEBOT_IO, new OnEventNoResponse(transferCache));
            client.addEventHandler(EVENT_INVITE, new OnEventNoResponse(transferCache));

            client.connect();
            LOGGER.info("The WebCom client has been started!");
        } catch (URISyntaxException e) {
            LOGGER.error("Could not initialize web com client", e);
        }
    }

    public <T> void sendSecure(String event, Class<T> c) {
        sendSecure(event, new JSONObject(), c);
    }

    public <T> void sendSecure(String event, JSONObject jsonObject, Class<T> c) {
        blockWhileDisconnected();
        send(event, jsonObject, c);
    }

    public <T> CompletableFuture<T> send(String event, Class<T> c) {
        return send(event, new JSONObject(), c);
    }

    public <T> CompletableFuture<T> send(String event, JSONObject jsonObject, Class<T> c) {
        jsonObject.put("id", StringUtil.getRandomString());
        CompletableFuture<T> completableFuture = transferCache.register(jsonObject, c);

        if (client.isConnected()) {
            client.send(event, jsonObject);
        } else {
            completableFuture.completeExceptionally(new NotYetConnectedException());
        }

        return completableFuture;
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    private synchronized void blockWhileDisconnected() {
        if (!client.isConnected()) {
            try {
                while (!client.isConnected()) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted", e);
            }
        }
    }

}
