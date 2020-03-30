package com.gmail.leonard.spring.Backend.WebCommunicationClient.Events;

import com.gmail.leonard.spring.Backend.CommandList.CommandListCategory;
import com.gmail.leonard.spring.Backend.CommandList.CommandListContainer;
import com.gmail.leonard.spring.Backend.CommandList.CommandListSlot;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.TransferCache;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONObject;

public class OnGenericResponseless implements Emitter.Listener {

    private TransferCache transferCache;

    public OnGenericResponseless(TransferCache transferCache) {
        this.transferCache = transferCache;
    }

    @Override
    public void call(Object... args) {
        transferCache.complete(null, Void.class);
    }

}
