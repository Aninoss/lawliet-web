package xyz.lawlietbot.spring.backend.userdata;

import java.util.ArrayList;
import java.util.Optional;

public class ServerListData {

    private ArrayList<DiscordServerData> servers = new ArrayList<>();

    public void clear() {
        servers = new ArrayList<>();
    }

    public void put(DiscordServerData server) {
         if (find(server.getId()).isEmpty()) {
             servers.add(server);
         }
    }

    public Optional<DiscordServerData> find(long serverId) {
        for(DiscordServerData server: servers) {
            if (server.getId() == serverId)
                return Optional.of(server);
        }
        return Optional.empty();
    }

    public int size() {
        return servers.size();
    }

    public ArrayList<DiscordServerData> getServers() {
        return servers;
    }

}
