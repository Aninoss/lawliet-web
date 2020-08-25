package com.gmail.leonard.spring.Backend.WebCommunicationClient.Modules;

import com.gmail.leonard.spring.Backend.CustomThread;
import com.gmail.leonard.spring.Backend.Feedback.FeedbackBean;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.WebComClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class OneWayTransfers {

    final static Logger LOGGER = LoggerFactory.getLogger(OneWayTransfers.class);

    public static CompletableFuture<Void> sendTopGG(JSONObject jsonObject) {
        return WebComClient.getInstance().sendSecure(WebComClient.EVENT_TOPGG, jsonObject, Void.class);
    }

    public static CompletableFuture<Void> sendDonatebotIO(JSONObject jsonObject) {
        return WebComClient.getInstance().sendSecure(WebComClient.EVENT_DONATEBOT_IO, jsonObject, Void.class);
    }

    public static CompletableFuture<Void> sendFeedback(FeedbackBean feedbackBean, Long serverId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cause", feedbackBean.getCause(feedbackBean));
        jsonObject.put("reason", feedbackBean.getReason(feedbackBean));
        if (feedbackBean.getContact(feedbackBean))
            jsonObject.put("username_discriminated", feedbackBean.getUsernameDiscriminated(feedbackBean));
        if (serverId != null)
            jsonObject.put("server_id", serverId);
        return WebComClient.getInstance().send(WebComClient.EVENT_FEEDBACK, jsonObject, Void.class);
    }

    public static void sendInvite(String type) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type.toLowerCase());
        new CustomThread(() -> {
            try {
                WebComClient.getInstance().sendSecure(WebComClient.EVENT_INVITE, jsonObject, Void.class).get();
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Exception while sending invite data", e);
            }
        }, "transfer_invite_data").start();
    }

}
