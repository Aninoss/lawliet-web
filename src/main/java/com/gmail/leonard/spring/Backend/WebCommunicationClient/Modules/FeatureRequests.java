package com.gmail.leonard.spring.Backend.WebCommunicationClient.Modules;

import com.gmail.leonard.spring.Backend.FeatureRequests.FRDynamicBean;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.WebComClient;
import org.json.JSONObject;
import java.util.concurrent.CompletableFuture;

public class FeatureRequests {

    public static CompletableFuture<FRDynamicBean> fetchFeatureRequestMainData(SessionData sessionData) {
        JSONObject jsonObject = new JSONObject();
        if (sessionData.isLoggedIn()) jsonObject.put("user_id", sessionData.getUserId().get());
        return WebComClient.getInstance().send(WebComClient.EVENT_FR_FETCH, jsonObject, FRDynamicBean.class);
    }

    public static CompletableFuture<JSONObject> sendBoost(FRDynamicBean frDynamicBean, int id, long userId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("entry_id", id);
        jsonObject.put("user_id", userId);
        return WebComClient.getInstance().send(WebComClient.EVENT_FR_BOOST, jsonObject, JSONObject.class);
    }

    public static CompletableFuture<Boolean> canPost(long userId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", userId);
        return WebComClient.getInstance().send(WebComClient.EVENT_FR_CAN_POST, jsonObject, Boolean.class);
    }

    public static CompletableFuture<Void> postNewFeatureRequest(long userId, String title, String description) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", userId);
        jsonObject.put("title", title);
        jsonObject.put("description", description);
        return WebComClient.getInstance().send(WebComClient.EVENT_FR_POST, jsonObject, Void.class);
    }

}
