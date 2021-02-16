package xyz.lawlietbot.spring.syncserver;

import org.java_websocket.client.WebSocketJsonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URISyntaxException;

public class SyncManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(SyncManager.class);

    private static final SyncManager ourInstance = new SyncManager();

    public static SyncManager getInstance() {
        return ourInstance;
    }

    private final WebSocketJsonClient client;
    private boolean started = false;

    private SyncManager() {
        try {
            client = new WebSocketJsonClient(
                    System.getenv("SYNC_HOST"),
                    Integer.parseInt(System.getenv("SYNC_PORT")),
                    "web",
                    System.getenv("SYNC_AUTH")
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void start() {
        if (started)
            return;
        started = true;

        this.client.connect();
    }

    public WebSocketJsonClient getClient() {
        return client;
    }

}
