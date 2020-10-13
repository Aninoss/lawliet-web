package com.gmail.leonard.spring.backend.serverstats;

public class ServerStatsBean {

    private final ServerStatsSlot[] slots;
    private final int servers;
    private final int users;

    public ServerStatsBean(int servers, int users, ServerStatsSlot[] slots) {
        this.slots = slots;
        //this.servers = servers; todo
        //this.users = users;
        this.servers = 46734;
        this.users = 2042683;
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
