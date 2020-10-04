package com.gmail.leonard.spring.backend.webcomclient.events;

import com.gmail.leonard.spring.backend.featurerequests.FRDynamicBean;
import com.gmail.leonard.spring.backend.featurerequests.FRPanelType;
import com.gmail.leonard.spring.backend.webcomclient.EventAbstract;
import com.gmail.leonard.spring.backend.webcomclient.TransferCache;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;

public class OnFRFetch extends EventAbstract<FRDynamicBean> {

    public OnFRFetch(TransferCache transferCache) {
        super(transferCache);
    }

    @Override
    protected FRDynamicBean processData(JSONObject mainJSON) {
        int boostsTotal = mainJSON.getInt("boosts_total");
        int boostsRemaining = mainJSON.getInt("boosts_remaining");

        FRDynamicBean frDynamicBean = new FRDynamicBean(boostsRemaining, boostsTotal);

        JSONArray jsonEntriesArray = mainJSON.getJSONArray("data");
        for(int j = 0; j < jsonEntriesArray.length(); j++) {
            JSONObject jsonEntry = jsonEntriesArray.getJSONObject(j);
            FRPanelType type = FRPanelType.valueOf(jsonEntry.getString("type"));
            boolean pub = jsonEntry.getBoolean("public");
            frDynamicBean.addEntry(
                    jsonEntry.getInt("id"),
                    jsonEntry.getString("title"),
                    jsonEntry.getString("description"),
                    type == FRPanelType.PENDING && pub ? jsonEntry.getInt("boosts") : null,
                    pub,
                    type,
                    LocalDate.ofEpochDay(jsonEntry.getLong("date"))
            );
        }

        return frDynamicBean;
    }

}
