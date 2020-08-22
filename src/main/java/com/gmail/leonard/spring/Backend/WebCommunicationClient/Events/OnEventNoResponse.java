package com.gmail.leonard.spring.Backend.WebCommunicationClient.Events;

import com.gmail.leonard.spring.Backend.CommandList.CommandListCategory;
import com.gmail.leonard.spring.Backend.CommandList.CommandListContainer;
import com.gmail.leonard.spring.Backend.CommandList.CommandListSlot;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.EventAbstract;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.TransferCache;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
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