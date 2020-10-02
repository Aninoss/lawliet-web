package com.gmail.leonard.spring.backend.serverstats;

public class ServerStatsBean {

    private final ServerStatsSlot[] slots;
    private final int servers;
    private final int users;

    public ServerStatsBean(int servers, int users, ServerStatsSlot[] slots) {
        this.slots = slots;
        this.servers = servers;
        this.users = users;
    }

    public ServerStatsSlot[] getSlots() {
        return slots;
    }

    public int getServers() {
        return servers;
    }

    public int getUsers() {
        return users;
    }

}
