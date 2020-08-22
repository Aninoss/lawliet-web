package com.gmail.leonard.spring.Backend.WebCommunicationClient.Events;

import com.gmail.leonard.spring.Backend.UserData.DiscordServerData;
import com.gmail.leonard.spring.Backend.UserData.ServerListData;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.EventAbstract;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.TransferCache;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Optional;

public class OnServerList extends EventAbstract<ServerListData> {

    public OnServerList(TransferCache transferCache) {
        super(transferCache);
    }

    @Override
    protected ServerListData processData(JSONObject mainJSON) {
        ServerListData serverListData = new ServerListData();
        JSONArray serverArray = mainJSON.getJSONArray("server_list");

        for (int i = 0; i < serverArray.length(); i++) {
            JSONObject serverObject = serverArray.getJSONObject(i);

            long serverId = serverObject.getLong("server_id");
            String name = serverObject.getString("name");
            Optional<String> iconURL = Optional.empty();
            if (serverObject.has("icon")) {
                iconURL = Optional.of(serverObject.getString("icon"));
            }

            DiscordServerData discordServerData = new DiscordServerData(serverId, name, iconURL);
            serverListData.put(discordServerData);
        }

        return serverListData;
    }

}
