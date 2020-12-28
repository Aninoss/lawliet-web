package com.gmail.leonard.spring.backend.serverstats;

import com.gmail.leonard.spring.syncserver.SendEvent;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ServerStatsContainer {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerStatsContainer.class);

    private final static ServerStatsContainer ourInstance = new ServerStatsContainer();

    public static ServerStatsContainer getInstance() {
        return ourInstance;
    }

    private ServerStatsContainer() {
    }

    private final LoadingCache<Integer, ServerStatsBean> cache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build(new CacheLoader<Integer, ServerStatsBean>() {
                @Override
                public ServerStatsBean load(@Nonnull final Integer n) throws Exception {
                    LOGGER.info("Updating server stats");
                    return SendEvent.sendRequestServerStats().get();
                }
            });

    public ServerStatsBean getBean() throws ExecutionException {
        ServerStatsBean serverStats = cache.get(0);
        if (!serverStats.getServers().isPresent())
            cache.invalidate(0);

        return serverStats;
    }

}
