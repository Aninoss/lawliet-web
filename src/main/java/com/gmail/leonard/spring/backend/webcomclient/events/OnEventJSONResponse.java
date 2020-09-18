package com.gmail.leonard.spring.backend.webcomclient.events;

import com.gmail.leonard.spring.backend.webcomclient.EventAbstract;
import com.gmail.leonard.spring.backend.webcomclient.TransferCache;
import org.json.JSONObject;

public class OnEventJSONResponse extends EventAbstract<JSONObject> {

    public OnEventJSONResponse(TransferCache transferCache) {
        super(transferCache);
    }

    @Override
    protected JSONObject processData(JSONObject mainJSON) {
        return mainJSON;
    }

}
