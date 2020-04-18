package com.gmail.leonard.spring.Backend.WebCommunicationClient.Events;

import com.gmail.leonard.spring.Backend.CommandList.CommandListCategory;
import com.gmail.leonard.spring.Backend.CommandList.CommandListContainer;
import com.gmail.leonard.spring.Backend.CommandList.CommandListSlot;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.TransferCache;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnCommandList implements Emitter.Listener {

    final static Logger LOGGER = LoggerFactory.getLogger(OnCommandList.class);
    private final TransferCache transferCache;

    public OnCommandList(TransferCache transferCache) {
        this.transferCache = transferCache;
    }

    @Override
    public void call(Object... args) {
        CommandListContainer.getInstance().clear();
        JSONArray mainJSON = new JSONArray((String) args[0]);

        //Read every command category
        for (int i = 0; i < mainJSON.length(); i++) {
            JSONObject categoryJSON = mainJSON.getJSONObject(i);

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

                commandListCategory.add(commandListSlot);
            }

            CommandListContainer.getInstance().add(commandListCategory);
        }

        transferCache.complete(CommandListContainer.getInstance(), CommandListContainer.class);
        LOGGER.info("Commands ready");
    }
}
