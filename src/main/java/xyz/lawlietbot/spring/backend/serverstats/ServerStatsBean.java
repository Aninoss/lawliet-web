package xyz.lawlietbot.spring.backend.serverstats;

import java.util.Optional;

public class ServerStatsBean {

    private final ServerStatsSlot[] slots;
    private final Long servers;

    public ServerStatsBean(Long servers, ServerStatsSlot[] slots) {
        this.slots = slots;
        this.servers = servers;
    }

    public ServerStatsSlot[] getSlots() {
        return slots;
    }

    public Optional<Long> getServers() {
        return Optional.ofNullable(servers);
    }

}
