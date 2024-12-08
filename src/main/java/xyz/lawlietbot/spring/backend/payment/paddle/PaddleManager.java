package xyz.lawlietbot.spring.backend.payment.paddle;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jamiussiam.paddle.verifier.Verifier;
import com.vaadin.flow.component.UI;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.lawlietbot.spring.ExternalLinks;
import xyz.lawlietbot.spring.backend.Pair;
import xyz.lawlietbot.spring.backend.UICache;
import xyz.lawlietbot.spring.backend.payment.Currency;
import xyz.lawlietbot.spring.backend.payment.*;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.backend.util.FileUtil;
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
    private final static LoadingCache<IpAndCoupon, JSONObject> productPricesCache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .build(new CacheLoader<>() {
                @NotNull
                @Override
                public JSONObject load(@NotNull IpAndCoupon ipAndCoupon) throws Exception {
                    return PaddleAPI.retrieveProductPrices(ipAndCoupon.ipAddress, ipAndCoupon.coupon);
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

    public static PaddlePriceOverview retrieveSubscriptionPrices(String customerIpAddress, int group) {
        customerIpAddress = Objects.requireNonNullElse(customerIpAddress, System.getenv("PADDLE_DEFAULT_IP"));

        JSONObject pricesJson;
        try {
            pricesJson = subscriptionPricesCache.get(new Pair<>(customerIpAddress, group));
            JSONArray productsJson = pricesJson.getJSONObject("response").getJSONArray("products");
            HashMap<String, PaddlePriceOverview.Price> subscriptionPriceMap = new HashMap<>();

            Currency currency = null;
            for (int i = 0; i < productsJson.length(); i++) {
                JSONObject productJson = productsJson.getJSONObject(i);
                currency = Currency.valueOf(productJson.getString("currency"));
                boolean includesVat = productJson.getBoolean("vendor_set_prices_included_tax");

                long productId = productJson.getLong("product_id");
                double currentPrice = productJson.getJSONObject("price").getDouble(includesVat ? "gross" : "net");
                double previousPrice = currentPrice;
                if (productIsInSale(String.valueOf(productId))) {
                    currentPrice = currentPrice * (100 - Integer.parseInt(System.getenv("PADDLE_SALE_PERCENT"))) / 100.0;
                }
                subscriptionPriceMap.put(String.valueOf(productId), new PaddlePriceOverview.Price(currentPrice, previousPrice, includesVat));
            }

            return new PaddlePriceOverview(
                    currency,
                    subscriptionPriceMap
            );
        } catch (ExecutionException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static PaddlePriceOverview retrieveProductPrices(String customerIpAddress) {
        customerIpAddress = Objects.requireNonNullElse(customerIpAddress, System.getenv("PADDLE_DEFAULT_IP"));

        JSONObject pricesJson;
        try {
            pricesJson = productPricesCache.get(new IpAndCoupon(customerIpAddress, System.getenv("PADDLE_SALE_DISCOUNT_ID"))).getJSONObject("data");

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

    public static void openPopup(SubDuration duration, SubLevel level, DiscordUser discordUser, int quantity, List<Long> presetGuildIds, Locale locale, int group) {
        long planId = PaddleManager.getPlanId(duration, level, group);
        UI.getCurrent().getPage().executeJs("openPaddle($0, $1, $2, $3, $4, $5, $6)",
                System.getenv("PADDLE_ENVIRONMENT"),
                Integer.parseInt(System.getenv("PADDLE_VENDOR_ID")),
                (int) planId,
                quantity,
                locale.getLanguage(),
                productIsInSale(String.valueOf(planId)) ? System.getenv("PADDLE_SALE_CODE") : null,
                generatePassthrough(discordUser, presetGuildIds)
        );
    }

    public static void openPopupBilling(String priceId, DiscordUser discordUser, Locale locale, String type) {
        UI.getCurrent().getPage().executeJs("openPaddleBilling($0, $1, $2, $3, $4, $5, $6, $7, $8)",
                System.getenv("PADDLE_ENVIRONMENT"),
                System.getenv("PADDLE_CLIENT_TOKEN"),
                priceId,
                locale.getLanguage(),
                productIsInSale(priceId) ? System.getenv("PADDLE_SALE_CODE") : null,
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
                    passthroughJson.has("discord_avatar") ? passthroughJson.getString("discord_avatar") : null,
                    checkoutJson.getJSONObject("checkout").getString("title"),
                    quantity,
                    checkoutJson.getJSONObject("order").getString("formatted_total")
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
                throw e;
            } finally {
                future.complete(null);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static void registerTxt2Img(JSONObject priceData, int quantity, long userId, JSONObject detailsTotalData, JSONObject customData) {
        try {
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
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static void registerPremiumCode(String priceId, long userId, JSONObject detailsTotalData, JSONObject customData, JSONObject priceData, int quantity) {
        try {
            ProductPremium product = ProductPremium.fromPriceId(priceId);

            JSONObject requestJson = new JSONObject();
            requestJson.put("user_id", userId);
            requestJson.put("level", product.getLevel());
            requestJson.put("days", product.getDays());
            requestJson.put("quantity", quantity);

            JSONObject responseJson = SendEvent.send(EventOut.CREATE_PREMIUM_CODE, requestJson).join();
            if (!responseJson.has("ok")) {
                throw new RuntimeException("Paddle premium codes error");
            }

            sendNotification(detailsTotalData, customData, userId, priceData, quantity);
            LOGGER.info("Premium code notification sent");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendNotification(JSONObject detailsTotalData, JSONObject customData, long userId, JSONObject priceData, int quantity) {
        try {
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
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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

    private static class IpAndCoupon {

        private final String ipAddress;
        private final String coupon;

        public IpAndCoupon(String ipAddress, String coupon) {
            this.ipAddress = ipAddress;
            this.coupon = coupon;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IpAndCoupon that = (IpAndCoupon) o;
            return Objects.equals(ipAddress, that.ipAddress) && Objects.equals(coupon, that.coupon);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ipAddress, coupon);
        }

    }

}
