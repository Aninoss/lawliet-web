package com.gmail.leonard.spring.Backend.WebCommunicationClient.Events;

import com.gmail.leonard.spring.Backend.CommandList.CommandListCategory;
import com.gmail.leonard.spring.Backend.CommandList.CommandListContainer;
import com.gmail.leonard.spring.Backend.CommandList.CommandListSlot;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OnCommandList implements Emitter.Listener {

    private List<CompletableFuture<Void>> commandListRequests;

    public OnCommandList(List<CompletableFuture<Void>> commandListRequests) {
        this.commandListRequests = commandListRequests;
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

        for(CompletableFuture<Void> cf: commandListRequests)
            cf.complete(null);
        commandListRequests.clear();

        System.out.println("Commands ready");
    }
}
