package com.gmail.leonard.spring.backend.webcomclient.modules;

import com.gmail.leonard.spring.backend.commandlist.CommandListCategory;
import com.gmail.leonard.spring.backend.commandlist.CommandListContainer;
import com.gmail.leonard.spring.backend.commandlist.CommandListSlot;
import com.gmail.leonard.spring.backend.webcomclient.WebComClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.CompletableFuture;

public class CommandList {

    private final static Logger LOGGER = LoggerFactory.getLogger(CommandList.class);

    public static CompletableFuture<CommandListContainer> fetchCommandList() {
        return WebComClient.getInstance().send(WebComClient.EVENT_COMMANDLIST, CommandListContainer.class);
    }

}
