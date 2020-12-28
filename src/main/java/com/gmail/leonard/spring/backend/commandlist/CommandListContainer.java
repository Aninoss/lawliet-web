package com.gmail.leonard.spring.backend.commandlist;

import com.gmail.leonard.spring.syncserver.SendEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommandListContainer {

    private final static Logger LOGGER = LoggerFactory.getLogger(CommandListContainer.class);
    private static final CommandListContainer ourInstance = new CommandListContainer();
    private final CopyOnWriteArrayList<CommandListCategory> categories = new CopyOnWriteArrayList<>();

    private CommandListContainer() {
    }

    public static CommandListContainer getInstance() {
        return ourInstance;
    }


    public void add(CommandListCategory commandListCategory) {
        if (find(commandListCategory.getId()) == null)
            categories.add(commandListCategory);
    }

    public CommandListCategory get(int n) {
        return categories.get(n);
    }

    public CommandListCategory find(String id) {
        return categories.stream()
                .filter(categorie -> categorie.getId().equals(id))
                .findFirst().orElse(null);
    }

    public List<CommandListCategory> getCategories() {
        loadIfEmpty();
        return categories;
    }

    public int size() {
        loadIfEmpty();
        return categories.size();
    }

    public int allCommandsSize(boolean showNsfw) {
        loadIfEmpty();
        int n = 0;
        for (CommandListCategory category : categories) {
            n += category.size(showNsfw);
        }

        return n;
    }

    public void clear() {
        categories.clear();
    }

    private synchronized void loadIfEmpty() {
        if (categories.size() == 0) {
            LOGGER.info("Updating command list");
            JSONObject responseJson = SendEvent.sendRequestCommandList().join();
            if (responseJson.has("categories")) {
                JSONArray arrayJSON = responseJson.getJSONArray("categories");

                //Read every command category
                for (int i = 0; i < arrayJSON.length(); i++) {
                    JSONObject categoryJSON = arrayJSON.getJSONObject(i);

                    CommandListCategory commandListCategory = new CommandListCategory();
                    commandListCategory.setId(categoryJSON.getString("id"));
                    commandListCategory.getLangName().set(categoryJSON.getJSONObject("name"));

                    JSONArray commandsJSON = categoryJSON.optJSONArray("commands");
                    //Read every command
                    for (int j = 0; j < commandsJSON.length(); j++) {
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
                        commandListSlot.setPatreonOnly(commandJSON.getBoolean("patron_only"));

                        commandListCategory.add(commandListSlot);
                    }

                    CommandListContainer.getInstance().add(commandListCategory);
                }
            }
        }
    }

    private JSONObject getLocalCommandList() {
        File cacheFile = new File("commands_cache.json");
        if (cacheFile.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(cacheFile));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                return new JSONObject(sb.toString());
            } catch (IOException e) {
                LOGGER.error("File error", e);
            }
        }
        return null;
    }

}
