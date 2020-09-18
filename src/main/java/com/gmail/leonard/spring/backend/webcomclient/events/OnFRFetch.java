package com.gmail.leonard.spring.backend.webcomclient.events;

import com.gmail.leonard.spring.backend.featurerequests.FRDynamicBean;
import com.gmail.leonard.spring.backend.featurerequests.FRPanelType;
import com.gmail.leonard.spring.backend.webcomclient.EventAbstract;
import com.gmail.leonard.spring.backend.webcomclient.TransferCache;
import org.json.JSONArray;
import org.json.JSONObject;

public class OnFRFetch extends EventAbstract<FRDynamicBean> {

    public OnFRFetch(TransferCache transferCache) {
        super(transferCache);
    }

    @Override
    protected FRDynamicBean processData(JSONObject mainJSON) {
        int boostsTotal = mainJSON.getInt("boosts_total");
        int boostsRemaining = mainJSON.getInt("boosts_remaining");

        FRDynamicBean frDynamicBean = new FRDynamicBean(boostsRemaining, boostsTotal);

        for(int i = 0; i < FRPanelType.values().length; i++) {
            FRPanelType type = FRPanelType.values()[i];
            JSONArray jsonEntriesArray = mainJSON.getJSONArray(type.name());
            for(int j = 0; j < jsonEntriesArray.length(); j++) {
                JSONObject jsonEntry = jsonEntriesArray.getJSONObject(j);
                frDynamicBean.addEntry(
                        type,
                        jsonEntry.getInt("id"),
                        jsonEntry.getString("title"),
                        jsonEntry.getString("description"),
                        i == 0 ? jsonEntry.getInt("boosts") : null,
                        jsonEntry.getBoolean("public")
                );
            }
        }

        return frDynamicBean;
    }

}
