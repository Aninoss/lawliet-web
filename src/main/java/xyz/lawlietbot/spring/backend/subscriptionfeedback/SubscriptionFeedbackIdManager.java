package xyz.lawlietbot.spring.backend.subscriptionfeedback;

import java.util.HashSet;
import xyz.lawlietbot.spring.backend.util.RandomUtil;

public class SubscriptionFeedbackIdManager {

    private static final HashSet<String> idSet = new HashSet<>();

    public static String generateId() {
        String id = RandomUtil.generateRandomString(20);
        idSet.add(id);
        return id;
    }

    public static boolean checkId(String id) {
        return idSet.remove(id);
    }

}
