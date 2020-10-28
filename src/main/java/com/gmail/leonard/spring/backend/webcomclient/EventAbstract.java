package com.gmail.leonard.spring.backend.webcomclient;

import org.json.JSONObject;
import java.util.function.Consumer;

public abstract class EventAbstract<T> implements Consumer<JSONObject> {

    private final TransferCache transferCache;
    
    public EventAbstract(TransferCache transferCache) {
        this.transferCache = transferCache;
    }

    @Override
    public void accept(JSONObject mainJSON) {
        T data = processData(mainJSON);
        transferCache.complete(mainJSON, data);
    }

    protected abstract T processData(JSONObject mainJSON);

}
