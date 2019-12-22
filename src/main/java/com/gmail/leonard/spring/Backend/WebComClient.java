package com.gmail.leonard.spring.Backend;

import com.gmail.leonard.spring.Backend.CommandList.CommandListCategory;
import com.gmail.leonard.spring.Backend.CommandList.CommandListContainer;
import com.gmail.leonard.spring.Backend.CommandList.CommandListSlot;
import com.gmail.leonard.spring.Backend.UserData.DiscordServerData;
import com.gmail.leonard.spring.Backend.UserData.ServerListData;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.validation.constraints.NotNull;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class WebComClient {

    private static WebComClient instance = new WebComClient();

    private static final String EVENT_COMMANDLIST = "command_list";
    private static final String EVENT_SERVERLIST = "server_list";

    private boolean areCommandsReady = false;
    private boolean started = false;
    private Socket socket;
    private LoadingCache<Long, Optional<SessionData>> loadingCache;

    private WebComClient() {
        loadingCache = CacheBuilder.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .build(
                        new CacheLoader<Long, Optional<SessionData>>() {
                            @Override
                            public Optional<SessionData> load(Long userId) {
                                return Optional.empty();
                            }
                        });
    }

    public static WebComClient getInstance() { return instance; }

    public void start(int port) {
        if (started) return;
        started = true;

        IO.Options options = new IO.Options();
        options.reconnection= true;
        try {
            socket = IO.socket("http://127.0.0.1:" + port + "/");

            //On Commands List
            socket.on(EVENT_COMMANDLIST, args -> {
                CommandListContainer.getInstance().clear();
                JSONArray mainJSON = new JSONArray((String)args[0]);

                //Read every command category
                for(int i = 0; i < mainJSON.length(); i++) {
                    JSONObject categoryJSON = mainJSON.getJSONObject(i);

                    CommandListCategory commandListCategory = new CommandListCategory();
                    commandListCategory.setId(categoryJSON.getString("id"));
                    commandListCategory.getLangName().set(categoryJSON.getJSONObject("name"));

                    JSONArray commandsJSON = categoryJSON.optJSONArray("commands");
                    //Read every command
                    for(int j = 0; j < commandsJSON.length(); j++) {
                        JSONObject commandJSON = commandsJSON.getJSONObject(j);

                        CommandListSlot commandListSlot = new CommandListSlot();
                        commandListSlot.setTrigger(commandJSON.getString("trigger"));
                        commandListSlot.setEmoji(commandJSON.getString("emoji"));
                        commandListSlot.getLangTitle().set(commandJSON.getJSONObject("title"));
                        commandListSlot.getLangDescShort().set(commandJSON.getJSONObject("desc_short"));
                        commandListSlot.getLangDescLong().set(commandJSON.getJSONObject("desc_long"));
                        commandListSlot.getLangUsage().set(commandJSON.getJSONObject("usage"));
                        commandListSlot.getLangExamples().set(commandJSON.getJSONObject("examples"));
                        commandListSlot.getLangUserPermissions().set(commandJSON.getJSONObject("user_permissions"));
                        commandListSlot.setNsfw(commandJSON.getBoolean("nsfw"));
                        commandListSlot.setRequiresUserPermissions(commandJSON.getBoolean("requires_user_permissions"));
                        commandListSlot.setCanBeTracked(commandJSON.getBoolean("can_be_tracked"));

                        commandListCategory.add(commandListSlot);
                    }

                    CommandListContainer.getInstance().add(commandListCategory);
                }

                areCommandsReady = true;
                System.out.println("Commands ready");
            });

            //On Individual Server List
            socket.on(EVENT_SERVERLIST, args -> {
                JSONObject mainJSON = new JSONObject((String)args[0]);

                long userId = mainJSON.getLong("user_id");
                Optional<SessionData> sessionDataOptional;
                try {
                    sessionDataOptional = loadingCache.get(userId);
                    if (!sessionDataOptional.isPresent()) {
                        loadingCache.invalidate(userId);
                        return;
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    loadingCache.invalidate(userId);
                    return;
                }

                SessionData sessionData = sessionDataOptional.get();
                ServerListData serverListData = sessionData.getServerListData();
                serverListData.clear();
                JSONArray serverArray = mainJSON.getJSONArray("server_list");

                for(int i = 0; i < serverArray.length(); i++) {
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
                loadingCache.invalidate(userId);
            });

            socket.connect();
            System.out.println("The WebCom client has been started!");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public boolean isReady() {
        return areCommandsReady;
    }

    public void updateServers(SessionData sessionData) {
        if (sessionData.isLoggedIn()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", sessionData.getUserId());
            loadingCache.put(sessionData.getUserId(), Optional.of(sessionData));
            socket.emit(EVENT_SERVERLIST, jsonObject.toString());
            try {
                while (loadingCache.get(sessionData.getUserId()).isPresent())
                        Thread.sleep(1000);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

}
