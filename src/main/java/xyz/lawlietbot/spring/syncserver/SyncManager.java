package xyz.lawlietbot.spring.syncserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URISyntaxException;

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
            client = new CustomWebSocketClient(
                    System.getenv("SYNC_HOST"),
                    Integer.parseInt(System.getenv("SYNC_PORT")),
                    "web"
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

    public CustomWebSocketClient getClient() {
        return client;
    }

}
