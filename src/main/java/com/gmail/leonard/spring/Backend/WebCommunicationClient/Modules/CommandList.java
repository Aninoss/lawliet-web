package com.gmail.leonard.spring.Backend.WebCommunicationClient.Modules;

import com.gmail.leonard.spring.Backend.CommandList.CommandListContainer;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.WebComClient;

import java.util.concurrent.CompletableFuture;

public class CommandList {

    public static CompletableFuture<CommandListContainer> fetchCommandList() {
        return WebComClient.getInstance().send(WebComClient.EVENT_COMMANDLIST, CommandListContainer.class);
    }

}
