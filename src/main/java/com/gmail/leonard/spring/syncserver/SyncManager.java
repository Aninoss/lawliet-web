package com.gmail.leonard.spring.syncserver;

import com.gmail.leonard.spring.backend.commandlist.CommandListContainer;
import com.gmail.leonard.spring.backend.faq.FAQListContainer;
import org.apache.http.ExceptionLogger;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class SyncManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(SyncManager.class);

    private static final SyncManager ourInstance = new SyncManager();

    public static SyncManager getInstance() {
        return ourInstance;
    }

    private final CustomWebSocketClient client;
    private boolean started = false;

    private SyncManager() {
        try {
            client = new CustomWebSocketClient("localhost", 9998, "web");
            client.addConnectedHandler(() -> {
                CommandListContainer.getInstance().clear();
                FAQListContainer.getInstance().clear();
                return false;
            });
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        Reflections reflections = new Reflections("com/gmail/leonard/spring/syncserver/events");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(SyncServerEvent.class);
        annotated.stream()
                .map(clazz -> {
                    try {
                        return clazz.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        LOGGER.error("Error when creating sync event class", e);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .filter(obj -> obj instanceof SyncServerFunction)
                .map(obj -> (SyncServerFunction) obj)
                .forEach(this::addEvent);
    }

    public synchronized void start() {
        if (started)
            return;
        started = true;

        this.client.connect();
    }

    public CustomWebSocketClient getClient() {
        return client;
    }

    private void addEvent(SyncServerFunction function) {
        SyncServerEvent event = function.getClass().getAnnotation(SyncServerEvent.class);
        if (event != null)
            this.client.addEventHandler(event.event(), function);
    }

}
