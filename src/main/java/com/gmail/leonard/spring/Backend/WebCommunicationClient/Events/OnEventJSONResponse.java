package com.gmail.leonard.spring.Backend.WebCommunicationClient.Events;

import com.gmail.leonard.spring.Backend.WebCommunicationClient.EventAbstract;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.TransferCache;
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
