package com.gmail.leonard.spring.backend.serverstats;

public class ServerStatsBean {

    private final ServerStatsSlot[] slots;
    private final int servers;

    public ServerStatsBean(int servers, ServerStatsSlot[] slots) {
        this.slots = slots;
        this.servers = servers;
    }

    public ServerStatsSlot[] getSlots() {
        return slots;
    }

    public int getServers() {
        return servers;
    }

}
