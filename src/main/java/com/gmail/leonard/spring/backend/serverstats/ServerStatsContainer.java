package com.gmail.leonard.spring.backend.serverstats;

import com.gmail.leonard.spring.backend.userdata.SessionData;
import com.gmail.leonard.spring.backend.webcomclient.modules.ServerStats;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Optional;
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
                    JSONObject statsJson = ServerStats.fetchServerStats().get();

                    JSONArray statsDataJson = statsJson.getJSONArray("data");

                    ServerStatsSlot[] slots = new ServerStatsSlot[statsDataJson.length()];
                    for (int i = 0; i < statsDataJson.length(); i++) {
                        JSONObject statsSlotJson = statsDataJson.getJSONObject(i);
                        slots[i] = new ServerStatsSlot(statsSlotJson.getInt("month"), statsSlotJson.getInt("year"), statsSlotJson.getInt("value"));
                    }

                    return new ServerStatsBean(
                            statsJson.isNull("servers") ? null : statsJson.getLong("servers"),
                            slots
                    );
                }
            });

    public ServerStatsBean getBean() throws ExecutionException {
        ServerStatsBean serverStats = cache.get(0);
        if (!serverStats.getServers().isPresent())
            cache.invalidate(0);

        return serverStats;
    }

}
