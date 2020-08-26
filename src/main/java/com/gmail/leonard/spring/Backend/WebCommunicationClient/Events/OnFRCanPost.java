package com.gmail.leonard.spring.Backend.WebCommunicationClient.Events;

import com.gmail.leonard.spring.Backend.WebCommunicationClient.EventAbstract;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.TransferCache;
import org.json.JSONObject;

public class OnFRCanPost extends EventAbstract<Boolean> {

    public OnFRCanPost(TransferCache transferCache) {
        super(transferCache);
    }

    @Override
    protected Boolean processData(JSONObject mainJSON) {
        return mainJSON.getBoolean("success");
    }

}
