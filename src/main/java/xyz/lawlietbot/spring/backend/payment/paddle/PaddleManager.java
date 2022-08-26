package xyz.lawlietbot.spring.backend.payment.paddle;

import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
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
import xyz.lawlietbot.spring.backend.FileString;
import xyz.lawlietbot.spring.backend.UICache;
import xyz.lawlietbot.spring.backend.payment.SubDuration;
import xyz.lawlietbot.spring.backend.payment.SubLevel;
import xyz.lawlietbot.spring.backend.payment.SubLevelType;
import xyz.lawlietbot.spring.backend.payment.WebhookNotifier;
import xyz.lawlietbot.spring.syncserver.SyncUtil;

public class PaddleManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(PaddleManager.class);
    private final static Verifier verifier;
    private final static LoadingCache<String, CompletableFuture<Void>> checkoutCache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofHours(1))
            .build(new CacheLoader<>() {
                @NotNull
                @Override
                public CompletableFuture<Void> load(@NotNull String key) {
                    return new CompletableFuture<>();
                }
            });

    static {
        String publicKey = "";
        try {
            publicKey = new FileString(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream("paddle_public_key.txt")
            ).toString();
        } catch (IOException e) {
            LOGGER.error("Error on public key read");
        }
        verifier = new Verifier(publicKey.replace("\r", ""));
    }

    public static boolean verifyWebhookData(String postBody) {
        return verifier.verifyDataWithSignature(postBody);
    }

    public static CompletableFuture<Void> waitForCheckoutAsync(String checkoutId) {
        try {
            return checkoutCache.get(checkoutId);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static void registerSubscription(Map<String, String[]> parameterMap) throws IOException {
        String checkoutId = parameterMap.get("checkout_id")[0];
        CompletableFuture<Void> future = waitForCheckoutAsync(checkoutId);
        try {
            JSONObject passthroughJson = new JSONObject(parameterMap.get("passthrough")[0]);
            int subscriptionId = Integer.parseInt(parameterMap.get("subscription_id")[0]);
            int planId = Integer.parseInt(parameterMap.get("subscription_plan_id")[0]);
            int quantity = Integer.parseInt(parameterMap.get("quantity")[0]);

            JSONObject checkoutJson = PaddleAPI.retrieveCheckout(checkoutId);
            long discordId = passthroughJson.getLong("discord_id");
            UI ui = UICache.get(discordId);
            SyncUtil.sendStripe(
                    discordId,
                    ui != null ? ui.getTranslation("premium.usermessage.title") : null,
                    ui != null ? ui.getTranslation("premium.usermessage.desc", ExternalLinks.LAWLIET_PREMIUM, ExternalLinks.BETA_SERVER_INVITE) : null,
                    subscriptionId,
                    PaddleManager.getSubLevelType(planId) == SubLevelType.PRO,
                    passthroughJson.getJSONArray("preset_guilds")
            ).join();
            try {
                String discordTag = new String(Base64.getDecoder().decode(passthroughJson.getString("discord_tag")));
                WebhookNotifier.newSub(
                        discordTag,
                        discordId,
                        passthroughJson.getString("discord_avatar"),
                        checkoutJson.getJSONObject("checkout").getString("title"),
                        quantity,
                        checkoutJson.getJSONObject("order").getString("formatted_total")
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
                    return 746336;

                case PRO:
                    return 746338;

                default:
                    return 0;
            }
        } else {
            switch (level.getSubLevelType()) {
                case BASIC:
                    return 746337;

                case PRO:
                    return 746340;

                default:
                    return 0;
            }
        }
    }

    public static SubLevelType getSubLevelType(int planId) {
        switch (planId) {
            case 746336:
            case 746337:
                return SubLevelType.BASIC;

            case 746338:
            case 746340:
                return SubLevelType.PRO;

            default:
                return null;
        }
    }

}
