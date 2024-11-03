package xyz.lawlietbot.spring;

import com.vaadin.flow.server.RequestHandler;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinSession;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.lawlietbot.spring.backend.payment.paddle.PaddleManager;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CustomRequestHandler implements RequestHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(CustomRequestHandler.class);

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response) {
        addHeaders(response);

        String auth = request.getHeader("Authorization");
        switch (request.getPathInfo()) {
            case "/discordlogin":
                return handleDiscordLogin(request, response);

            case "/print":
                handlePrint(request, response, auth);
                return true;

            case "/topgg":
                handleTopGG(request, response, auth);
                return true;

            case "/topgg_aninoss":
                handleTopGGAnicord(request, response, auth);
                return true;

            case "/topgg_vote_rewards":
                handleTopGGVoteRewards(request, response, auth);
                return true;

            case "/invite":
                handleInvite(response, request.getParameterMap());
                return true;

            case "/paddle":
                handlePaddle(request, response);
                return true;

            case "/paddle_billing":
                handlePaddleBilling(request, response);
                return true;

            default:
                return false;
        }
    }

    private void addHeaders(VaadinResponse response) {
        response.setHeader(
                "Content-Security-Policy",
                "default-src data: 'self' https://widgetbot.io https://e.widgetbot.io ws://localhost:35729/ https://www.paypal.com https://*.paddle.com https://sandbox-create-checkout.paddle.com https://create-checkout.paddle.com https://sandbox-buy.paddle.com https://buy.paddle.com https://*.profitwell.com; " +
                        "img-src 'self' https://*.lawlietbot.xyz/ https://cdn.discordapp.com/ https://*.donmai.us/ https://*.rule34.xxx/ https://*.paheal.net/ https://realbooru.com/ https://*.e621.net/ https://safebooru.org/ https://www.paypal.com https://cdn.paddle.com https://*.profitwell.com https://dna8twue3dlxq.cloudfront.net; " +
                        "media-src 'self' https://*.lawlietbot.xyz/ https://*.donmai.us/ https://*.rule34.xxx/ https://*.paheal.net/ https://realbooru.com/ https://*.e621.net/ https://safebooru.org/; " +
                        "object-src 'self'; " +
                        "script-src 'unsafe-inline' 'unsafe-eval' 'self' ajax.cloudflare.com https://cdn.jsdelivr.net https://www.paypal.com https://*.paddle.com https://*.profitwell.com https://polyfill.io https://*.googleapis.com https://*.sentry-cdn.com; " +
                        "style-src https://*.paddle.com https://*.profitwell.com 'unsafe-inline' 'self'; " +
                        "frame-src https://*.paddle.com; " +
                        "frame-ancestors https://top.gg https://discords.com https://wumpus.store; " +
                        "base-uri 'self'"
        );
        response.setHeader("X-Frame-Options", "allow-from https://top.gg/ https://discords.com/ https://wumpus.store/");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Referrer-Policy", "same-origin");
        response.setHeader("Feature-Policy", "microphone 'none'; geolocation 'none'");
        response.setHeader("Access-Control-Allow-Origin", "https://top.gg https://discords.com https://wumpus.store");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubdomains");
    }

    private boolean handleDiscordLogin(VaadinRequest request, VaadinResponse response) {
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        SessionData sessionData = (SessionData) request.getWrappedSession().getAttribute("session");

        if (code != null && state != null && sessionData != null) {
            boolean ok = sessionData.login(code, state);
            String resumeTarget = ok ? sessionData.getCurrentTarget() : "";
            response.setHeader("Location", "/" + resumeTarget);
            response.setStatus(301);
            return true;
        }
        return false;
    }

    private void handlePrint(VaadinRequest request, VaadinResponse response, String auth) {
        if (System.getenv("PRINT_AUTH").equals(auth)) {
            try (BufferedReader br = request.getReader()) {
                String body = br.lines().collect(Collectors.joining("\n"));
                if (body.length() > 0) {
                    LOGGER.info("Content:\n" + body);
                }
            } catch (IOException e) {
                LOGGER.error("Error while handling print", e);
                response.setStatus(500);
            }
        } else {
            response.setStatus(403);
        }
    }

    private void handleTopGG(VaadinRequest request, VaadinResponse response, String auth) {
        LOGGER.info("Receiving upvote signal...");
        if (System.getenv("TOPGG_AUTH").equals(auth)) {
            try (BufferedReader br = request.getReader()) {
                String body = br.lines().collect(Collectors.joining("\n"));
                if (!body.isEmpty()) {
                    JSONObject jsonObject = new JSONObject(body);
                    LOGGER.info("UPVOTE | {}", jsonObject.getLong("user"));
                    JSONObject responseJson = SendEvent.send(EventOut.TOPGG, jsonObject).get(5, TimeUnit.SECONDS);
                    if (!responseJson.getBoolean("success")) {
                        LOGGER.error("Error while handling upvote");
                        response.setStatus(500);
                    }
                } else {
                    LOGGER.error("Empty body while handling upvote");
                }
            } catch (Throwable e) {
                LOGGER.error("Error while handling upvote", e);
                response.setStatus(500);
            }
        } else {
            LOGGER.error("Invalid auth while handling upvote");
            response.setStatus(403);
        }
    }

    private void handleTopGGAnicord(VaadinRequest request, VaadinResponse response, String auth) {
        LOGGER.info("Receiving Anicord upvote signal...");
        if (System.getenv("TOPGG_ANINOSS_AUTH").equals(auth)) {
            try (BufferedReader br = request.getReader()) {
                String body = br.lines().collect(Collectors.joining("\n"));
                if (body.length() > 0) {
                    JSONObject jsonObject = new JSONObject(body);
                    LOGGER.info("UPVOTE ANICORD | {}", jsonObject.getLong("user"));
                    JSONObject responseJson = SendEvent.send(EventOut.TOPGG_ANICORD, jsonObject).get(5, TimeUnit.SECONDS);
                    if (!responseJson.getBoolean("success")) {
                        LOGGER.error("Error while handling Anicord upvote");
                        response.setStatus(500);
                    }
                } else {
                    LOGGER.error("Empty body while handling Anicord upvote");
                }
            } catch (Throwable e) {
                LOGGER.error("Error while handling Anicord upvote", e);
                response.setStatus(500);
            }
        } else {
            LOGGER.error("Invalid auth while handling Anicord upvote");
            response.setStatus(403);
        }
    }

    private void handleTopGGVoteRewards(VaadinRequest request, VaadinResponse response, String auth) {
        LOGGER.info("Receiving vote rewards upvote signal...");
        try (BufferedReader br = request.getReader()) {
            String body = br.lines().collect(Collectors.joining("\n"));
            if (body.length() > 0) {
                JSONObject jsonObject = new JSONObject(body)
                        .put("auth", auth);
                long guildId = jsonObject.getLong("guild");
                long userId = jsonObject.getLong("user");

                LOGGER.info("UPVOTE {} | {}", guildId, userId);
                JSONObject responseJson = SendEvent.sendToGuild(EventOut.TOPGG_VOTE_REWARDS, jsonObject, guildId).get(5, TimeUnit.SECONDS);
                if (!responseJson.getBoolean("success")) {
                    LOGGER.error("Error while handling vote rewards upvote");
                    response.setStatus(500);
                }
            } else {
                LOGGER.error("Empty body while handling vote rewards upvote");
            }
        } catch (Throwable e) {
            LOGGER.error("Error while handling vote rewards upvote", e);
            response.setStatus(500);
        }
    }

    private void handleInvite(VaadinResponse response, Map<String, String[]> params) {
        response.setHeader("Location", ExternalLinks.BOT_INVITE_URL_EXT);
        response.setStatus(301);

        if (!params.isEmpty()) {
            String type = new ArrayList<>(params.keySet()).get(0);
            if (!type.isEmpty()) {
                SendEvent.send(EventOut.INVITE, Map.of("type", type))
                        .exceptionally(ExceptionLogger.get());
            }
        }
    }

    private void handlePaddle(VaadinRequest request, VaadinResponse response) {
        try {
            StringBuilder bodyBuilder = new StringBuilder();
            Map<String, String[]> parameterMap = request.getParameterMap();
            parameterMap.keySet()
                    .forEach(key -> bodyBuilder.append("&").append(key).append("=").append(URLEncoder.encode(parameterMap.get(key)[0], StandardCharsets.UTF_8)));
            String body = bodyBuilder.substring(1);
            if (PaddleManager.verifyWebhookData(body)) {
                PaddleManager.registerSubscription(parameterMap);
            } else {
                response.setStatus(403);
            }
        } catch (IOException e) {
            LOGGER.error("Error while handling Paddle", e);
            response.setStatus(500);
        }
    }

    private void handlePaddleBilling(VaadinRequest request, VaadinResponse response) {
        try (BufferedReader br = request.getReader()) {
            String body = br.lines().collect(Collectors.joining("\n"));
            if (PaddleManager.verifyBillingWebhookData(body, request.getHeader("Paddle-Signature"))) {
                PaddleManager.registerBilling(new JSONObject(body));
            } else {
                response.setStatus(403);
            }
        } catch (IOException | JSONException e) {
            LOGGER.error("Error while handling Paddle Billing", e);
            response.setStatus(500);
        }
    }

}
