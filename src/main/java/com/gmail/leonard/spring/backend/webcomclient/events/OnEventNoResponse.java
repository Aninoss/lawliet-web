package com.gmail.leonard.spring.backend.webcomclient.events;

import com.gmail.leonard.spring.backend.webcomclient.EventAbstract;
import com.gmail.leonard.spring.backend.webcomclient.TransferCache;
import org.json.JSONObject;

public class OnEventNoResponse extends EventAbstract<Void> {

    public OnEventNoResponse(TransferCache transferCache) {
        super(transferCache);
    }

    @Override
    protected Void processData(JSONObject mainJSON) {
        return null;
    }

}