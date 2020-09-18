package com.gmail.leonard.spring.backend.webcomclient.modules;

import com.gmail.leonard.spring.backend.commandlist.CommandListContainer;
import com.gmail.leonard.spring.backend.webcomclient.WebComClient;

import java.util.concurrent.CompletableFuture;

public class CommandList {

    public static CompletableFuture<CommandListContainer> fetchCommandList() {
        return WebComClient.getInstance().send(WebComClient.EVENT_COMMANDLIST, CommandListContainer.class);
    }

}
