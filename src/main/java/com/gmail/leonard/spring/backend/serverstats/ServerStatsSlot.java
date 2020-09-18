package com.gmail.leonard.spring.backend.serverstats;

public class ServerStatsSlot {

    private final int month;
    private final int year;
    private final int serverCount;

    public ServerStatsSlot(int month, int year, int serverCount) {
        this.month = month;
        this.year = year;
        this.serverCount = serverCount;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getServerCount() {
        return serverCount;
    }

}
