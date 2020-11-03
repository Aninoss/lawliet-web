package com.gmail.leonard.spring.backend.webcomclient.modules;

import com.gmail.leonard.spring.backend.CustomThread;
import com.gmail.leonard.spring.backend.feedback.FeedbackBean;
import com.gmail.leonard.spring.backend.webcomclient.WebComClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class OneWayTransfers {

    public static void sendTopGG(JSONObject jsonObject) {
        WebComClient.getInstance().sendSecure(WebComClient.EVENT_TOPGG, jsonObject, Void.class);
    }

    public static void sendTopGGAninoss(JSONObject jsonObject) {
        WebComClient.getInstance().sendSecure(WebComClient.EVENT_TOPGG_ANINOSS, jsonObject, Void.class);
    }

    public static void sendDonatebotIO(JSONObject jsonObject) {
        WebComClient.getInstance().sendSecure(WebComClient.EVENT_DONATEBOT_IO, jsonObject, Void.class);
    }

    public static CompletableFuture<Void> sendFeedback(FeedbackBean feedbackBean, Long serverId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cause", feedbackBean.getCause(feedbackBean));
        jsonObject.put("reason", feedbackBean.getReason(feedbackBean));
        if (serverId != null)
            jsonObject.put("server_id", serverId);
        //return WebComClient.getInstance().send(WebComClient.EVENT_FEEDBACK, jsonObject, Void.class);
        return CompletableFuture.completedFuture(null);
    }

    public static void sendInvite(String type) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type.toLowerCase());
        new CustomThread(() -> {
            WebComClient.getInstance().sendSecure(WebComClient.EVENT_INVITE, jsonObject, Void.class);
        }, "transfer_invite_data").start();
    }

}
