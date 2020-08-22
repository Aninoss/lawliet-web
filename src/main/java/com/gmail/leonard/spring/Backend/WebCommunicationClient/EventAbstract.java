package com.gmail.leonard.spring.Backend.WebCommunicationClient;

import com.gmail.leonard.spring.Backend.WebCommunicationClient.TransferCache;
import io.socket.emitter.Emitter;
import org.json.JSONObject;

public abstract class EventAbstract<T> implements Emitter.Listener {

    private final TransferCache transferCache;
    
    public EventAbstract(TransferCache transferCache) {
        this.transferCache = transferCache;
    }
    
    @Override
    public void call(Object... args) {
        JSONObject mainJSON = new JSONObject((String) args[0]);
        T data = processData(mainJSON);
        transferCache.complete(mainJSON, data);
    }

    protected abstract T processData(JSONObject mainJSON);

}
