package xyz.lawlietbot.spring.backend.payment.paddle;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jamiussiam.paddle.verifier.Verifier;
import com.vaadin.flow.component.UI;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.lawlietbot.spring.ExternalLinks;
import xyz.lawlietbot.spring.backend.FileString;
import xyz.lawlietbot.spring.backend.UICache;
import xyz.lawlietbot.spring.backend.payment.SubDuration;
import xyz.lawlietbot.spring.backend.payment.SubLevel;
import xyz.lawlietbot.spring.backend.payment.WebhookNotifier;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

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

    public static void openPopup(SubDuration duration, SubLevel level, DiscordUser discordUser, int quantity, List<Long> presetGuildIds, Locale locale) {
        System.out.println(generatePassthrough(discordUser, presetGuildIds));
        UI.getCurrent().getPage().executeJs("openPaddle($0, $1, $2, $3, $4)",
                Integer.parseInt(System.getenv("PADDLE_VENDOR_ID")),
                (int) PaddleManager.getPlanId(duration, level),
                quantity,
                locale.getLanguage(),
                generatePassthrough(discordUser, presetGuildIds)
        );
    }

    public static void openPopupCustom(String id) {
        UI.getCurrent().getPage().executeJs(
                "openPaddleCustom($0, $1)",
                Integer.parseInt(System.getenv("PADDLE_VENDOR_ID")),
                id
        );
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
        printParameterMap(parameterMap);

        String checkoutId = parameterMap.get("checkout_id")[0];
        CompletableFuture<Void> future = waitForCheckoutAsync(checkoutId);

        try {
            JSONObject checkoutJson = PaddleAPI.retrieveCheckout(checkoutId);

            JSONObject passthroughJson = new JSONObject(parameterMap.get("passthrough")[0]);
            long subscriptionId = Long.parseLong(parameterMap.get("subscription_id")[0]);
            long planId = Long.parseLong(parameterMap.get("subscription_plan_id")[0]);
            int quantity = Integer.parseInt(parameterMap.get("quantity")[0]);
            String state = parameterMap.get("status")[0];
            String currency = parameterMap.get("currency")[0];
            double total = Double.parseDouble(checkoutJson.getJSONObject("order").getString("total"));
            String totalPrice = String.format("%s %.02f", currency, total);
            String nextPayment = parameterMap.get("next_bill_date")[0];
            String updateUrl = parameterMap.get("update_url")[0];
            long discordId = passthroughJson.getLong("discord_id");
            UI ui = UICache.get(discordId);

            JSONObject json = new JSONObject();
            json.put("user_id", discordId);
            json.put("title", ui != null ? ui.getTranslation("premium.usermessage.title") : null);
            json.put("desc",  ui != null ? ui.getTranslation("premium.usermessage.desc", ExternalLinks.LAWLIET_PREMIUM, ExternalLinks.BETA_SERVER_INVITE, ExternalLinks.LAWLIET_DEVELOPMENT_VOTES) : null);
            json.put("sub_id", subscriptionId);
            json.put("unlocks_server", PaddleManager.getSubLevelType(planId) == SubLevel.PRO);
            json.put("preset_guilds", passthroughJson.has("preset_guilds") ? passthroughJson.getJSONArray("preset_guilds") : new JSONArray());
            json.put("plan_id", planId);
            json.put("quantity", quantity);
            json.put("state", state);
            json.put("total_price", totalPrice);
            json.put("next_payment", nextPayment);
            json.put("update_url", updateUrl);
            SendEvent.send(EventOut.PADDLE, json).join();

            String discordTag = new String(Base64.getDecoder().decode(passthroughJson.getString("discord_tag")));
            WebhookNotifier.newSub(
                    discordTag,
                    discordId,
                    passthroughJson.getString("discord_avatar"),
                    checkoutJson.getJSONObject("checkout").getString("title"),
                    quantity,
                    checkoutJson.getJSONObject("order").getString("formatted_total")
            );
            LOGGER.info("Subscription notification sent");
        } catch (Throwable e) {
            LOGGER.error("Error in new Paddle sub", e);
        } finally {
            future.complete(null);
        }
    }

    private static void printParameterMap(Map<String, String[]> parameterMap) {
        LOGGER.info("--- NEW SUBSCRIPTION RECEIVED ---");
        parameterMap.forEach((k, v) -> LOGGER.info("{}: {}", k, v[0]));
    }

    public static long getPlanId(SubDuration duration, SubLevel level) {
        if (duration == SubDuration.MONTHLY) {
            switch (level) {
                case BASIC:
                    return 746336L;

                case PRO:
                    return 746338L;

                case ULTIMATE:
                    return 820443L;

                default:
                    return 0L;
            }
        } else {
            switch (level) {
                case BASIC:
                    return 746337L;

                case PRO:
                    return 746340L;

                case ULTIMATE:
                    return 820444L;

                default:
                    return 0L;
            }
        }
    }

    public static SubLevel getSubLevelType(long planId) {
        switch (String.valueOf(planId)) {
            case "746336":
            case "746337":
                return SubLevel.BASIC;

            case "746338":
            case "746340":
                return SubLevel.PRO;

            case "820443":
            case "820444":
                return SubLevel.ULTIMATE;

            default:
                return null;
        }
    }

    private static String generatePassthrough(DiscordUser discordUser, List<Long> presetGuildIds) {
        String discordTag = discordUser.getUsername() + "#" + discordUser.getDiscriminator();

        JSONObject json = new JSONObject();
        json.put("discord_id", discordUser.getId());
        json.put("discord_tag", Base64.getEncoder().encodeToString(discordTag.getBytes(StandardCharsets.UTF_8)));
        json.put("discord_avatar", discordUser.getUserAvatar());

        JSONArray presetGuildsArray = new JSONArray();
        for (long presetGuildId : presetGuildIds) {
            presetGuildsArray.put(presetGuildId);
        }
        json.put("preset_guilds", presetGuildsArray);

        return json.toString();
    }

}
