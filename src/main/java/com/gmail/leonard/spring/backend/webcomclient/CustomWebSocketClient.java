package com.gmail.leonard.spring.backend.webcomclient;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.gmail.leonard.spring.backend.CustomThread;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomWebSocketClient extends WebSocketClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(CustomWebSocketClient.class);

    private final HashMap<String, Consumer<JSONObject>> eventHandlers = new HashMap<>();
    private final ArrayList<Runnable> connectedTempHandlers = new ArrayList<>();
    private final ArrayList<Runnable> connectedHandlers = new ArrayList<>();
    private boolean connected = false;

    public CustomWebSocketClient(URI serverURI) {
        super(serverURI);
    }

    public CustomWebSocketClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        connected = true;
        LOGGER.info("Web socket connected");
        connectedHandlers.forEach(Runnable::run);
        for(Runnable runnable : new ArrayList<>(connectedTempHandlers)) {
            connectedTempHandlers.remove(runnable);
            runnable.run();
        }
    }

    public void addConnectedHandler(Runnable runnable) {
        connectedHandlers.add(runnable);
    }

    public void addConnectedTempHandler(Runnable runnable) {
        connectedTempHandlers.add(runnable);
    }

    public void removeConnectedHandler(Runnable runnable) {
        connectedHandlers.remove(runnable);
    }

    public void addEventHandler(String event, Consumer<JSONObject> eventConsumer) {
        eventHandlers.put(event, eventConsumer);
    }

    public void removeEventHandler(String event) {
        eventHandlers.remove(event);
    }

    public void removeEventHandler(String event, Consumer<JSONObject> eventConsumer) {
        eventHandlers.remove(event, eventConsumer);
    }

    @Override
    public void onMessage(String message) {
        String event = message.split("::")[0];
        String content = message.substring(event.length() + 2);
        Consumer<JSONObject> eventConsumer = eventHandlers.get(event);
        if (eventConsumer != null)
            eventConsumer.accept(new JSONObject(content));
    }

    public void send(String event, JSONObject content) {
        if (eventHandlers.containsKey(event))
            super.send(event + "::" + content.toString());
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (connected) LOGGER.info("Web socket disconnected");
        connected = false;
        new CustomThread(() -> {
            try {
                Thread.sleep(2000);
                reconnect();
            } catch (InterruptedException e) {
                //Ignore
            }
        }, "websocket_reconnect").start();
    }

    @Override
    public void onError(Exception ex) {
        if (!ex.toString().contains("Connection refused"))
            LOGGER.error("Web socket error", ex);
    }

    public boolean isConnected() {
        if (getSocket() == null)
            return false;
        return getSocket().isConnected();
    }

}
