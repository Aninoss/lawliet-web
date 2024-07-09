package xyz.lawlietbot.spring.backend.payment.paddle;

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
import xyz.lawlietbot.spring.backend.Pair;
import xyz.lawlietbot.spring.backend.UICache;
import xyz.lawlietbot.spring.backend.payment.Currency;
import xyz.lawlietbot.spring.backend.payment.*;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PaddleManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(PaddleManager.class);
    private final static Verifier verifier;
    private final static PaddleBillingWebhookVerifier paddleBillingWebhookVerifier;
    private final static LoadingCache<String, CompletableFuture<Void>> checkoutCache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofHours(1))
            .build(new CacheLoader<>() {
                @NotNull
                @Override
                public CompletableFuture<Void> load(@NotNull String key) {
                    return new CompletableFuture<>();
                }
            });
    private final static LoadingCache<Pair<String, Integer>, JSONObject> subscriptionPricesCache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .build(new CacheLoader<>() {
                @NotNull
                @Override
                public JSONObject load(@NotNull Pair<String, Integer> pair) throws Exception {
                    return PaddleAPI.retrieveSubscriptionPrices(pair.getKey(), pair.getValue());
                }
            });
    private final static LoadingCache<String, JSONObject> productPricesCache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .build(new CacheLoader<>() {
                @NotNull
                @Override
                public JSONObject load(@NotNull String ip) throws Exception {
                    return PaddleAPI.retrieveProductPrices(ip);
                }
            });

    static {
        String publicKey = "";
        try {
            publicKey = new FileString(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream("paddle_public_key_" + System.getenv("PADDLE_ENVIRONMENT") + ".txt")
            ).toString();
        } catch (IOException e) {
            LOGGER.error("Error on public key read");
        }
        verifier = new Verifier(publicKey.replace("\r", ""));
        paddleBillingWebhookVerifier = new PaddleBillingWebhookVerifier(System.getenv("PADDLE_BILLING_WEBHOOK_KEY"));
    }

    public static PaddleSubscriptionPrices retrieveSubscriptionPrices(String customerIpAddress, int group) {
        customerIpAddress = Objects.requireNonNullElse(customerIpAddress, System.getenv("PADDLE_DEFAULT_IP"));

        JSONObject json;
        try {
            json = subscriptionPricesCache.get(new Pair<>(customerIpAddress, group));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        JSONArray productsJson = json.getJSONObject("response").getJSONArray("products");
        HashMap<Long, Double> subscriptionPriceMap = new HashMap<>();

        Currency currency = null;
        boolean includesVat = false;
        for (int i = 0; i < productsJson.length(); i++) {
            JSONObject productJson = productsJson.getJSONObject(i);
            currency = Currency.valueOf(productJson.getString("currency"));
            includesVat = productJson.getBoolean("vendor_set_prices_included_tax");

            long productId = productJson.getLong("product_id");
            double price = productJson.getJSONObject("price").getDouble(includesVat ? "gross" : "net");
            subscriptionPriceMap.put(productId, price);
        }

        return new PaddleSubscriptionPrices(
                currency,
                subscriptionPriceMap,
                includesVat
        );
    }

    public static Map<String, String> retrieveProductPrices(String customerIpAddress, String vatString) {
        customerIpAddress = Objects.requireNonNullElse(customerIpAddress, System.getenv("PADDLE_DEFAULT_IP"));

        JSONObject json;
        try {
            json = productPricesCache.get(customerIpAddress);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        JSONArray itemsJson = json.getJSONObject("data").getJSONObject("details").getJSONArray("line_items");
        HashMap<String, String> productPriceMap = new HashMap<>();

        for (int i = 0; i < itemsJson.length(); i++) {
            JSONObject itemJson = itemsJson.getJSONObject(i);
            JSONObject priceJson = itemJson.getJSONObject("price");
            String priceId = priceJson.getString("id");

            String price;
            if (priceJson.getString("tax_mode").equals("external")) {
                price = itemJson.getJSONObject("formatted_totals").getString("subtotal") + vatString;
            } else {
                price = itemJson.getJSONObject("formatted_totals").getString("total");
            }
            productPriceMap.put(priceId, price);
        }

        return productPriceMap;
    }

    public static void openPopup(SubDuration duration, SubLevel level, DiscordUser discordUser, int quantity, List<Long> presetGuildIds, Locale locale, int group) {
        UI.getCurrent().getPage().executeJs("openPaddle($0, $1, $2, $3, $4, $5)",
                System.getenv("PADDLE_ENVIRONMENT"),
                Integer.parseInt(System.getenv("PADDLE_VENDOR_ID")),
                (int) PaddleManager.getPlanId(duration, level, group),
                quantity,
                locale.getLanguage(),
                generatePassthrough(discordUser, presetGuildIds)
        );
    }

    public static void openPopupBilling(String priceId, DiscordUser discordUser, Locale locale, String type) {
        UI.getCurrent().getPage().executeJs("openPaddleBilling($0, $1, $2, $3, $4, $5, $6, $7)",
                System.getenv("PADDLE_ENVIRONMENT"),
                System.getenv("PADDLE_CLIENT_TOKEN"),
                priceId,
                locale.getLanguage(),
                String.valueOf(discordUser.getId()),
                discordUser.getUsername(),
                discordUser.getUserAvatar(),
                type
        );
    }

    public static void openPopupCustom(String id) {
        UI.getCurrent().getPage().executeJs(
                "openPaddleCustom($0, $1, $2)",
                System.getenv("PADDLE_ENVIRONMENT"),
                Integer.parseInt(System.getenv("PADDLE_VENDOR_ID")),
                id
        );
    }

    public static boolean verifyWebhookData(String postBody) {
        return verifier.verifyDataWithSignature(postBody);
    }

    public static boolean verifyBillingWebhookData(String postBody, String paddleSignature) {
        try {
            return paddleBillingWebhookVerifier.verify(postBody.replace("\r", ""), paddleSignature);
        } catch (InvalidKeyException e) {
            LOGGER.error("Paddle Billing webhook verification error", e);
            return false;
        }
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
            String email = parameterMap.get("email")[0];
            long discordId = passthroughJson.getLong("discord_id");
            UI ui = UICache.get(discordId);

            JSONObject json = new JSONObject();
            json.put("user_id", discordId);
            json.put("title", ui != null ? ui.getTranslation("premium.usermessage.title") : null);
            json.put("desc", ui != null ? ui.getTranslation("premium.usermessage.desc", ExternalLinks.LAWLIET_PREMIUM, ExternalLinks.BETA_SERVER_INVITE, ExternalLinks.LAWLIET_DEVELOPMENT_VOTES) : null);
            json.put("sub_id", subscriptionId);
            json.put("unlocks_server", PaddleManager.getSubLevelType(planId) == SubLevel.PRO);
            json.put("preset_guilds", passthroughJson.has("preset_guilds") ? passthroughJson.getJSONArray("preset_guilds") : new JSONArray());
            json.put("plan_id", planId);
            json.put("quantity", quantity);
            json.put("state", state);
            json.put("total_price", totalPrice);
            json.put("next_payment", nextPayment);
            json.put("update_url", updateUrl);
            json.put("email", email);
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

    public static void registerBilling(JSONObject json) {
        LOGGER.info("--- NEW PAYMENT RECEIVED ---\n{}", json);

        JSONObject data = json.getJSONObject("data");
        JSONObject itemData = data.getJSONArray("items").getJSONObject(0);
        JSONObject priceData = itemData.getJSONObject("price");
        JSONObject customData = data.getJSONObject("custom_data");
        JSONObject detailsTotalData = data.getJSONObject("details").getJSONObject("totals");

        String transactionId = data.getString("id");
        CompletableFuture<Void> future = waitForCheckoutAsync(transactionId);

        long userId = Long.parseLong(customData.getString("discordId"));
        int quantity = itemData.getInt("quantity");
        String priceId = priceData.getString("id");

        try {
            if (ProductTxt2Img.fromPriceId(priceId) != null) {
                registerTxt2Img(priceData, quantity, userId, detailsTotalData, customData);
            } else {
                registerPremiumCode(priceId, userId, detailsTotalData, customData, priceData, quantity);
            }
        } catch (Throwable e) {
            LOGGER.error("Error in new paddle billing payment", e);
        } finally {
            future.complete(null);
        }
    }

    private static void registerTxt2Img(JSONObject priceData, int quantity, long userId, JSONObject detailsTotalData, JSONObject customData) {
        int n = priceData.getJSONObject("custom_data").getInt("n") * quantity;

        JSONObject requestJson = new JSONObject();
        requestJson.put("user_id", userId);
        requestJson.put("n", n);

        JSONObject responseJson = SendEvent.sendToAnyCluster(EventOut.PADDLE_TXT2IMG, requestJson).join();
        if (!responseJson.has("ok")) {
            throw new RuntimeException("Paddle txt2img error");
        }

        sendNotification(detailsTotalData, customData, userId, priceData, quantity);
        LOGGER.info("Txt2img notification sent");
    }

    private static void registerPremiumCode(String priceId, long userId, JSONObject detailsTotalData, JSONObject customData, JSONObject priceData, int quantity) {
        ProductPremium product = ProductPremium.fromPriceId(priceId);

        JSONObject requestJson = new JSONObject();
        requestJson.put("user_id", userId);
        requestJson.put("plan", product.getPlan());
        requestJson.put("days", product.getDays());

        JSONObject responseJson = SendEvent.sendToAnyCluster(EventOut.PADDLE_PREMIUM_CODES, requestJson).join();
        if (!responseJson.has("ok")) {
            throw new RuntimeException("Paddle premium codes error");
        }

        sendNotification(detailsTotalData, customData, userId, priceData, quantity);
        LOGGER.info("Premium code notification sent");
    }

    private static void sendNotification(JSONObject detailsTotalData, JSONObject customData, long userId, JSONObject priceData, int quantity) {
        Currency currency = Currency.valueOf(detailsTotalData.getString("currency_code"));
        int total = detailsTotalData.getInt("grand_total");
        String priceString = NumberFormat.getCurrencyInstance(Locale.ENGLISH)
                .format((double) total / Math.pow(10, currency.getDecimalPlaces()))
                .replace("¤", currency.getSymbol());

        WebhookNotifier.newSub(
                customData.getString("discordTag"),
                userId,
                customData.getString("discordAvatar"),
                priceData.getString("description"),
                quantity,
                priceString
        );
    }

    private static void printParameterMap(Map<String, String[]> parameterMap) {
        LOGGER.info("--- NEW SUBSCRIPTION RECEIVED ---");
        parameterMap.forEach((k, v) -> LOGGER.info("{}: {}", k, v[0]));
    }

    public static long getPlanId(SubDuration duration, SubLevel level, int group) {
        int i;
        if (duration == SubDuration.MONTHLY) {
            switch (level) {
                case BASIC:
                    i = 0;
                    break;

                case PRO:
                    i = 1;
                    break;

                case ULTIMATE:
                    i = 2;
                    break;

                default:
                    return 0L;
            }
        } else {
            switch (level) {
                case BASIC:
                    i = 3;
                    break;

                case PRO:
                    i = 4;
                    break;

                case ULTIMATE:
                    i = 5;
                    break;

                default:
                    return 0L;
            }
        }

        return Long.parseLong(System.getenv("PADDLE_SUBSCRIPTION_IDS_" + group).split(",")[i]);
    }

    public static SubLevel getSubLevelType(long planIdLong) {
        for (int i = 0; i < 2; i++) {
            String subString = System.getenv("PADDLE_SUBSCRIPTION_IDS_" + i);
            if (subString == null) {
                continue;
            }

            String[] subIds = subString.split(",");
            String planId = String.valueOf(planIdLong);

            if (List.of(subIds[0], subIds[3]).contains(planId)) {
                return SubLevel.BASIC;
            }
            if (List.of(subIds[1], subIds[4]).contains(planId)) {
                return SubLevel.PRO;
            }
            if (List.of(subIds[2], subIds[5]).contains(planId)) {
                return SubLevel.ULTIMATE;
            }
        }
        return null;
    }

    private static String generatePassthrough(DiscordUser discordUser, List<Long> presetGuildIds) {
        JSONObject json = new JSONObject();
        json.put("discord_id", discordUser.getId());
        json.put("discord_tag", Base64.getEncoder().encodeToString(discordUser.getUsername().getBytes(StandardCharsets.UTF_8)));
        json.put("discord_avatar", discordUser.getUserAvatar());

        JSONArray presetGuildsArray = new JSONArray();
        for (long presetGuildId : presetGuildIds) {
            presetGuildsArray.put(presetGuildId);
        }
        json.put("preset_guilds", presetGuildsArray);

        return json.toString();
    }

}
