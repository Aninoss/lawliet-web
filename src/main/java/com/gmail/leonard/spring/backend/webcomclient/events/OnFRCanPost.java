package com.gmail.leonard.spring.backend.webcomclient.events;

import com.gmail.leonard.spring.backend.webcomclient.EventAbstract;
import com.gmail.leonard.spring.backend.webcomclient.TransferCache;
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
