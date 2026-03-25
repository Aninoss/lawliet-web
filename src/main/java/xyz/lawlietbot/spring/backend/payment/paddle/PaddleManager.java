package xyz.lawlietbot.spring.backend.payment.paddle;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jamiussiam.paddle.verifier.Verifier;
import com.vaadin.flow.component.UI;
import org.apache.tomcat.util.buf.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.lawlietbot.spring.ExternalLinks;
import xyz.lawlietbot.spring.backend.UICache;
import xyz.lawlietbot.spring.backend.payment.*;
import xyz.lawlietbot.spring.backend.payment.Currency;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.backend.util.FileUtil;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PaddleManager {

    public final static String TYPE_BASIC = "basic";
    public final static String TYPE_PRO = "pro";
    public final static String TYPE_TXT2IMG = "txt2img";
    public final static String TYPE_PREMIUM = "premium";

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
    private final static LoadingCache<IpGroupAndCoupon, JSONObject> pricesCache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .build(new CacheLoader<>() {
                @NotNull
                @Override
                public JSONObject load(@NotNull PaddleManager.IpGroupAndCoupon ipGroupAndCoupon) throws Exception {
                    return PaddleAPI.retrieveProductPrices(ipGroupAndCoupon.ipAddress, ipGroupAndCoupon.group, ipGroupAndCoupon.coupon);
                }
            });

    static {
        String publicKey = "";
        try {
            publicKey = FileUtil.readResource("paddle_public_key_" + System.getenv("PADDLE_ENVIRONMENT") + ".txt");
        } catch (IOException e) {
            LOGGER.error("Error on public key read");
        }
        verifier = new Verifier(publicKey.replace("\r", ""));
        paddleBillingWebhookVerifier = new PaddleBillingWebhookVerifier(System.getenv("PADDLE_BILLING_WEBHOOK_KEY"));
    }

    public static PaddlePriceOverview retrievePrices(String customerIpAddress, int group) {
        customerIpAddress = Objects.requireNonNullElse(customerIpAddress, System.getenv("PADDLE_DEFAULT_IP"));

        JSONObject pricesJson;
        try {
            pricesJson = pricesCache.get(new IpGroupAndCoupon(customerIpAddress, group, System.getenv("PADDLE_SALE_DISCOUNT_ID"))).getJSONObject("data");

            JSONArray itemsJson = pricesJson.getJSONObject("details").getJSONArray("line_items");
            HashMap<String, PaddlePriceOverview.Price> productPriceMap = new HashMap<>();
            Currency currency = Currency.valueOf(pricesJson.getString("currency_code"));

            for (int i = 0; i < itemsJson.length(); i++) {
                JSONObject itemJson = itemsJson.getJSONObject(i);
                JSONObject totalsJson = itemJson.getJSONObject("totals");
                JSONObject priceJson = itemJson.getJSONObject("price");
                String priceId = priceJson.getString("id");

                PaddlePriceOverview.Price price;
                if (priceJson.getString("tax_mode").equals("external")) {
                    price = new PaddlePriceOverview.Price(
                            (Double.parseDouble(totalsJson.getString("subtotal")) - Double.parseDouble(totalsJson.getString("discount"))) / Math.pow(10, currency.getDecimalPlaces()),
                            Double.parseDouble(totalsJson.getString("subtotal")) / Math.pow(10, currency.getDecimalPlaces()),
                            false
                    );
                } else {
                    price = new PaddlePriceOverview.Price(
                            (Double.parseDouble(totalsJson.getString("total")) - Double.parseDouble(totalsJson.getString("discount"))) / Math.pow(10, currency.getDecimalPlaces()),
                            Double.parseDouble(totalsJson.getString("total")) / Math.pow(10, currency.getDecimalPlaces()),
                            true
                    );
                }
                productPriceMap.put(priceId, price);
            }

            return new PaddlePriceOverview(
                    Currency.valueOf(pricesJson.getString("currency_code")),
                    productPriceMap
                    );
        } catch (ExecutionException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static void openPopupSubscription(SubDuration duration, SubLevel level, DiscordUser discordUser, int quantity, List<Long> presetGuildIds, Locale locale, int group) {
        String planId = PaddleManager.getPlanId(duration, level, group);
        UI.getCurrent().getPage().executeJs("openPaddleBilling($0, $1, $2, $3, $4, $5, $6, $7, $8, $9, $10)",
                System.getenv("PADDLE_ENVIRONMENT"),
                System.getenv("PADDLE_CLIENT_TOKEN"),
                planId,
                quantity,
                locale.getLanguage(),
                productIsInSale(planId) ? System.getenv("PADDLE_SALE_CODE") : null,
                String.valueOf(discordUser.getId()),
                discordUser.getUsername(),
                discordUser.getUserAvatar(),
                presetGuildIds != null ? StringUtils.join(presetGuildIds.stream().map(String::valueOf).toList(), ',') : null,
                level.name().toLowerCase()
        );
    }

    public static void openPopupOneOff(String priceId, DiscordUser discordUser, Locale locale, String type) {
        UI.getCurrent().getPage().executeJs("openPaddleBilling($0, $1, $2, $3, $4, $5, $6, $7, $8, $9, $10)",
                System.getenv("PADDLE_ENVIRONMENT"),
                System.getenv("PADDLE_CLIENT_TOKEN"),
                priceId,
                1,
                locale.getLanguage(),
                productIsInSale(priceId) ? System.getenv("PADDLE_SALE_CODE") : null,
                String.valueOf(discordUser.getId()),
                discordUser.getUsername(),
                discordUser.getUserAvatar(),
                null,
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

    public static void registerSubscriptionOld(Map<String, String[]> parameterMap) throws IOException {
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
                    passthroughJson.has("discord_avatar") ? passthroughJson.getString("discord_avatar") : null,
                    checkoutJson.getJSONObject("checkout").getString("title"),
                    quantity
            );
            LOGGER.info("Subscription notification sent");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            LOGGER.error("Error in new Paddle sub", e);
            throw e;
        } finally {
            future.complete(null);
        }
    }

    public static void registerBilling(JSONObject json) {
        try {
            LOGGER.info("--- PADDLE BILLING NOTIFICATION RECEIVED ---\n{}", json);

            JSONObject data = json.getJSONObject("data");
            JSONObject itemData = data.getJSONArray("items").getJSONObject(0);
            JSONObject priceData = itemData.getJSONObject("price");
            JSONObject customData = data.getJSONObject("custom_data");
            JSONObject priceCustomData = priceData.getJSONObject("custom_data");
            int quantity = itemData.getInt("quantity");
            long userId = Long.parseLong(customData.getString("discord_id"));

            String transactionId = data.getString(data.has("transaction_id") ? "transaction_id" : "id");
            CompletableFuture<Void> future = waitForCheckoutAsync(transactionId);

            boolean accept = false;
            try {
                accept = switch (priceCustomData.getString("type")) {
                    case TYPE_BASIC, TYPE_PRO -> registerSubscription(json, data, priceData, customData, quantity, userId);
                    case TYPE_TXT2IMG -> registerTxt2Img(priceData, customData, quantity, userId);
                    case TYPE_PREMIUM -> registerPremiumCode(priceData, customData, quantity, userId);
                    default -> false;
                };
            } catch (Throwable e) {
                LOGGER.error("Error in new paddle billing payment", e);
                throw e;
            } finally {
                if (accept) {
                    future.complete(null);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean registerSubscription(JSONObject root, JSONObject data, JSONObject priceData, JSONObject customData, int quantity, long userId) {
        UI ui = UICache.get(userId);
        String eventType = root.getString("event_type");
        if (!eventType.startsWith("subscription.")) {
            return false;
        }
        boolean created = eventType.equals("subscription.created");

        JSONObject json = new JSONObject();
        json.put("data", data);
        json.put("title", ui != null ? ui.getTranslation("premium.usermessage.title") : null);
        json.put("description", ui != null ? ui.getTranslation("premium.usermessage.desc", ExternalLinks.LAWLIET_PREMIUM, ExternalLinks.BETA_SERVER_INVITE, ExternalLinks.LAWLIET_DEVELOPMENT_VOTES) : null);
        json.put("created", created);
        SendEvent.send(EventOut.PADDLE_BILLING, json).join();

        if (created) {
            sendNotification(customData, userId, priceData, quantity);
            LOGGER.info("Subscription notification sent");
        }
        return true;
    }

    private static boolean registerTxt2Img(JSONObject priceData, JSONObject customData, int quantity, long userId) {
        int n = priceData.getJSONObject("custom_data").getInt("n") * quantity;

        JSONObject requestJson = new JSONObject();
        requestJson.put("user_id", userId);
        requestJson.put("n", n);

        JSONObject responseJson = SendEvent.sendToAnyCluster(EventOut.PADDLE_TXT2IMG, requestJson).join();
        if (!responseJson.has("ok")) {
            throw new RuntimeException("Paddle txt2img error");
        }

        sendNotification(customData, userId, priceData, quantity);
        LOGGER.info("Txt2img notification sent");
        return true;
    }

    private static boolean registerPremiumCode(JSONObject priceData, JSONObject customData, int quantity, long userId) {
        ProductPremium product = ProductPremium.fromPriceId(priceData.getString("id"));
        JSONObject requestJson = new JSONObject();
        requestJson.put("user_id", userId);
        requestJson.put("level", product.getLevel());
        requestJson.put("days", product.getDays());
        requestJson.put("quantity", quantity);

        JSONObject responseJson = SendEvent.send(EventOut.CREATE_PREMIUM_CODE, requestJson).join();
        if (!responseJson.has("ok")) {
            throw new RuntimeException("Paddle premium codes error");
        }

        sendNotification(customData, userId, priceData, quantity);
        LOGGER.info("Premium code notification sent");
        return true;
    }

    private static void sendNotification(JSONObject customData, long userId, JSONObject priceData, int quantity) {
        try {
            WebhookNotifier.newSub(
                    customData.getString("discord_tag"),
                    userId,
                    customData.getString("discord_avatar"),
                    priceData.getString("description"),
                    quantity
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printParameterMap(Map<String, String[]> parameterMap) {
        LOGGER.info("--- NEW SUBSCRIPTION RECEIVED ---");
        parameterMap.forEach((k, v) -> LOGGER.info("{}: {}", k, v[0]));
    }

    public static String getPlanId(SubDuration duration, SubLevel level, int group) {
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
                    return null;
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
                    return null;
            }
        }

        return System.getenv("PADDLE_PREMIUM_SUBSCRIPTION_IDS_" + group).split(",")[i];
    }

    public static SubLevel getSubLevelType(long planIdLong) {
        for (int i = 0; i < 2; i++) {
            String[] classicSubIds = System.getenv("PADDLE_SUBSCRIPTION_IDS_" + i).split(",");
            String[] billingSubIds = System.getenv("PADDLE_PREMIUM_SUBSCRIPTION_IDS_" + i).split(",");
            String planId = String.valueOf(planIdLong);
            if (List.of(classicSubIds[0], classicSubIds[3], billingSubIds[0], billingSubIds[3]).contains(planId)) {
                return SubLevel.BASIC;
            }
            if (List.of(classicSubIds[1], classicSubIds[4], billingSubIds[1], billingSubIds[4]).contains(planId)) {
                return SubLevel.PRO;
            }
            if (List.of(classicSubIds[2], classicSubIds[5], billingSubIds[2], billingSubIds[5]).contains(planId)) {
                return SubLevel.ULTIMATE;
            }
        }
        return null;
    }

    private static String generatePassthrough(DiscordUser discordUser, List<Long> presetGuildIds) {
        try {
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
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean productIsInSale(String id) {
        String saleProducts = System.getenv("PADDLE_SALE_PRODUCTS");
        return saleProducts != null && Set.of(saleProducts.split(",")).contains(id);
    }

    private static class IpGroupAndCoupon {

        private final String ipAddress;
        private final int group;
        private final String coupon;

        public IpGroupAndCoupon(String ipAddress, int group, String coupon) {
            this.ipAddress = ipAddress;
            this.group = group;
            this.coupon = coupon;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IpGroupAndCoupon that = (IpGroupAndCoupon) o;
            return Objects.equals(ipAddress, that.ipAddress) && Objects.equals(group, that.group) && Objects.equals(coupon, that.coupon);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ipAddress, group, coupon);
        }

    }

}
