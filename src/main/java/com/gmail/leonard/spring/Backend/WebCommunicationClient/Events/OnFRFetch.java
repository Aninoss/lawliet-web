package com.gmail.leonard.spring.Backend.WebCommunicationClient.Events;

import com.gmail.leonard.spring.Backend.FeatureRequests.FRDynamicBean;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.EventAbstract;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.TransferCache;
import org.json.JSONObject;

public class OnFRFetch extends EventAbstract<FRDynamicBean> {

    public OnFRFetch(TransferCache transferCache) {
        super(transferCache);
    }

    @Override
    protected FRDynamicBean processData(JSONObject mainJSON) {
        int boostsTotal = mainJSON.getInt("boosts_total");

        return new FRDynamicBean(5, boostsTotal);
    }

}
