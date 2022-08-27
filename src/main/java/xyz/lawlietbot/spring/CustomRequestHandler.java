package xyz.lawlietbot.spring;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.vaadin.flow.server.RequestHandler;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinSession;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.lawlietbot.spring.backend.payment.paddle.PaddleManager;
import xyz.lawlietbot.spring.backend.payment.stripe.StripeManager;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

public class CustomRequestHandler implements RequestHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(CustomRequestHandler.class);

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response) {
        modifySessionCookie(request, response);
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

            case "/invite":
                handleInvite(response, request.getParameterMap());
                return true;

            case "/stripe":
                handleStripe(request, response);
                return true;

            case "/paddle":
                handlePaddle(request, response);
                return true;

            default:
                return false;
        }
    }

    private void addHeaders(VaadinResponse response) {
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubdomains");
        response.setHeader(
                "Content-Security-Policy",
                "default-src data: 'self' https://widgetbot.io https://e.widgetbot.io ws://localhost:35729/ https://fonts.gstatic.com/ https://www.paypal.com https://cdn.paddle.com https://sandbox-create-checkout.paddle.com https://create-checkout.paddle.com https://sandbox-buy.paddle.com https://buy.paddle.com; " +
                        "img-src 'self' https://*.lawlietbot.xyz/ https://cdn.discordapp.com/ https://*.donmai.us/ https://*.rule34.xxx/ https://realbooru.com/ https://*.e621.net/ https://safebooru.org/ https://www.paypal.com https://cdn.paddle.com;" +
                        "media-src 'self' https://*.lawlietbot.xyz/ https://*.donmai.us/ https://*.rule34.xxx/ https://realbooru.com/ https://*.e621.net/ https://safebooru.org/; " +
                        "object-src 'self'; " +
                        "script-src 'unsafe-inline' 'unsafe-eval' 'self' ajax.cloudflare.com https://cdn.jsdelivr.net https://www.paypal.com https://cdn.paddle.com;" +
                        "style-src https://fonts.googleapis.com/ https://fonts.gstatic.com/ https://cdn.paddle.com 'unsafe-inline' 'self'; " +
                        "frame-ancestors https://top.gg https://discords.com"
        );
        response.setHeader("X-Frame-Options", "allow-from https://top.gg/ https://discords.com/");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Referrer-Policy", "same-origin");
        response.setHeader("Feature-Policy", "microphone 'none'; geolocation 'none'");
        response.setHeader("Access-Control-Allow-Origin", "https://top.gg https://discords.com");
        response.setHeader("X-XSS-Protection", "1; mode=block");
    }

    private void modifySessionCookie(VaadinRequest request, VaadinResponse response) {
        String sessionId = request.getWrappedSession().getId();
        response.setHeader("Set-Cookie", "JSESSIONID=" + sessionId + "; Path=/; HttpOnly; Secure; SameSite=None");
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
                if (body.length() > 0) {
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

    private void handleInvite(VaadinResponse response, Map<String, String[]> params) {
        response.setHeader("Location", ExternalLinks.BOT_INVITE_URL_EXT);
        response.setStatus(301);

        if (params.size() > 0) {
            String type = new ArrayList<>(params.keySet()).get(0);
            if (type.length() > 0) {
                SendEvent.send(EventOut.INVITE, Map.of("type", type))
                        .exceptionally(ExceptionLogger.get());
            }
        }
    }

    private void handleStripe(VaadinRequest request, VaadinResponse response) {
        try (BufferedReader br = request.getReader()) {
            String sigHeader = request.getHeader("Stripe-Signature");
            if (sigHeader != null) {
                String payload = br.lines().collect(Collectors.joining("\n"));
                String signature = System.getenv("STRIPE_WEBHOOK_SIGNATURE");
                Event event = Webhook.constructEvent(payload, sigHeader, signature);
                if (event.getType().equals("checkout.session.completed")) {
                    String json = event.getData().toJson();
                    JSONObject data = new JSONObject(json);
                    String sessionId = data.getJSONObject("object").getString("id");
                    StripeManager.registerSubscription(Session.retrieve(sessionId));
                } else {
                    response.setStatus(500);
                }
            } else {
                response.setStatus(403);
            }
        } catch (SignatureVerificationException e) {
            response.setStatus(403);
        } catch (IOException | StripeException e) {
            LOGGER.error("Error while handling Stripe", e);
            response.setStatus(500);
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

}
