package com.gmail.leonard.spring.Backend.WebCommunicationClient.Events;

import com.gmail.leonard.spring.Backend.Pair;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.TransferCache;
import io.socket.emitter.Emitter;
import org.json.JSONObject;
import java.util.Optional;

public class OnServerMembers implements Emitter.Listener {

    private TransferCache transferCache;

    public OnServerMembers(TransferCache transferCache) {
        this.transferCache = transferCache;
    }

    @Override
    public void call(Object... args) {
        JSONObject mainJSON = new JSONObject((String) args[0]);
        transferCache.complete(mainJSON, JSONObject.class);
    }
}
