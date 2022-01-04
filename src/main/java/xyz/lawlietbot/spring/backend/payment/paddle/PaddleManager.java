package xyz.lawlietbot.spring.backend.payment.paddle;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jamiussiam.paddle.verifier.Verifier;
import com.vaadin.flow.component.UI;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.lawlietbot.spring.ExternalLinks;
import xyz.lawlietbot.spring.backend.UICache;
import xyz.lawlietbot.spring.backend.payment.SubDuration;
import xyz.lawlietbot.spring.backend.payment.SubLevel;
import xyz.lawlietbot.spring.backend.payment.SubLevelType;
import xyz.lawlietbot.spring.backend.payment.WebhookNotifier;
import xyz.lawlietbot.spring.syncserver.SendEvent;

public class PaddleManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(PaddleManager.class);
    private static final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" + //TODO
            "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAzzW9O9nhVKkKy8lVokfb\n" +
            "z6hfAmcH36BAzsha0my8c1BWoyl34qlMi3MTgucBUbuzSCaqTCr3sSf5iPyILBik\n" +
            "DEJ9JrFo3JaRtmEG7Bk/yleSkP1qXdezffS9QTdC25yhR97zXWLeVkbFhY4pP9DB\n" +
            "m4gFvucxyfP06EkGoZRFHrJZEbYmedY0NywOHyHEGT4uDsfEUEmojbzopdEV25u2\n" +
            "5kPU45SJE3/9F8SL3Q63qeY38iEr5tGiJURh4WUCkDW/qXGzP0DQ++cPQSqIpvhZ\n" +
            "1luR4Cgx8lbJ3qmE/co4PzFn4thFTlo0Tj193e8Mrt7FUxAUtLNbHH0xeBICBx9c\n" +
            "gvmuD9eQ/5FH83G3bIGvmGOAwbAHC5TT0M3T1BFTTIrvFizWii7HMbAFUQRQ1noV\n" +
            "rlIoUoPCF2mQiVY7IkqvAUCQnbTSPlLI02iW55cXPTqAE7oO3pFP3LRP71jM7Q6u\n" +
            "t4qr2HQLy7fIBlu9E0zcDXW4ZNSheGU8Bfmz04MMA4ZGBeTy+7lDl/jKXdeDRbii\n" +
            "wfYeqvEUbv4okt/pTWUP9q+dj9HPprYkL4RjSNo4AIo9wSIdTSPXovXUqn6qWsWl\n" +
            "ZbwNXW6JMsXhoQy/v5ZbilKctCAiqwZmjHZXPS/Z0Bj9sIeOtOxj7KF/wUlS7gdd\n" +
            "yt/IGUP93vJzRDcKJgAl8/kCAwEAAQ==\n" +
            "-----END PUBLIC KEY-----";
    private static final Verifier VERIFIER = new Verifier(PUBLIC_KEY);
    private static final LoadingCache<String, CompletableFuture<Void>> checkoutCache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofHours(1))
            .build(new CacheLoader<>() {
                @NotNull
                @Override
                public CompletableFuture<Void> load(@NotNull String key) {
                    return new CompletableFuture<>();
                }
            });

    public static boolean verifyWebhookData(String postBody) {
        return VERIFIER.verifyDataWithSignature(postBody);
    }

    public static CompletableFuture<Void> waitForCheckoutAsync(String checkoutId) {
        try {
            return checkoutCache.get(checkoutId);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static void registerSubscription(JSONObject json) throws IOException {
        String checkoutId = json.getString("checkout_id");
        CompletableFuture<Void> future = waitForCheckoutAsync(checkoutId);
        try {
            JSONObject passthroughJson = new JSONObject(json.getString("passthrough"));
            JSONObject checkoutJson = PaddleAPI.retrieveCheckout(checkoutId);
            long discordId = passthroughJson.getLong("discord_id");
            UI ui = UICache.get(discordId);
            SendEvent.sendStripe(
                    discordId,
                    ui != null ? ui.getTranslation("premium.usermessage.title") : null,
                    ui != null ? ui.getTranslation("premium.usermessage.desc", ExternalLinks.LAWLIET_PREMIUM, ExternalLinks.BETA_SERVER_INVITE) : null,
                    json.getInt("subscription_id"),
                    PaddleManager.getSubLevelType(json.getInt("subscription_plan_id")) == SubLevelType.PRO
            ).join();
            try {
                WebhookNotifier.newSub(
                        passthroughJson.getString("discord_tag"),
                        discordId,
                        passthroughJson.getString("discord_avatar"),
                        checkoutJson.getJSONObject("checkout").getString("title"),
                        json.getInt("quantity"),
                        json.getString("currency"),
                        json.getDouble("unit_price") * json.getInt("quantity"),
                        Double.parseDouble(checkoutJson.getJSONObject("order").getString("total_tax"))
                );
            } catch (Throwable e) {
                LOGGER.error("Error in new Paddle sub", e);
            }
        } finally {
            future.complete(null);
        }
    }

    public static int getPlanId(SubDuration duration, SubLevel level) {
        if (duration == SubDuration.MONTHLY) {
            switch (level.getSubLevelType()) {
                case BASIC:
                    return 21921;

                case PRO:
                    return 21919;

                default:
                    return 0;
            }
        } else {
            switch (level.getSubLevelType()) {
                case BASIC:
                    return 21920;

                case PRO:
                    return 21918;

                default:
                    return 0;
            }
        }
    }

    public static SubLevelType getSubLevelType(int planId) {
        switch (planId) {
            case 21921:
            case 21920:
                return SubLevelType.BASIC;

            case 21919:
            case 21918:
                return SubLevelType.PRO;

            default:
                return null;
        }
    }

}
