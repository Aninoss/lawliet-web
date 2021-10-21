package xyz.lawlietbot.spring;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vaadin.flow.server.RequestHandler;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinSession;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.lawlietbot.spring.syncserver.SendEvent;

public class CustomRequestHandler implements RequestHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(CustomRequestHandler.class);

    private final LoadingCache<String, Boolean> inviteIPAdresses = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build(
                    new CacheLoader<String, Boolean>() {
                        @Override
                        public Boolean load(@NonNull String ip) throws Exception {
                            return true;
                        }
                    }
            );

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response) {
        String auth = request.getHeader("Authorization");
        if (auth != null) {
            if (request.getPathInfo().equals("/print") && handlePrint(request, auth)) return true;
            if (request.getPathInfo().equals("/topgg") && handleTopGG(request, auth)) return true;
            if (request.getPathInfo().equals("/topgg_aninoss") && handleTopGGAninoss(request, auth)) return true;
        }

        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubdomains");
        response.setHeader("Content-Security-Policy",
                "default-src data: 'self' https://widgetbot.io https://e.widgetbot.io ws://localhost:35729/ https://fonts.gstatic.com/; " +
                        "img-src 'self' https://*.lawlietbot.xyz/ https://cdn.discordapp.com/ https://*.donmai.us/ https://*.rule34.xxx/ https://realbooru.com/ https://*.e621.net/ https://safebooru.org/;" +
                        "media-src 'self' https://*.lawlietbot.xyz/ https://*.donmai.us/ https://*.rule34.xxx/ https://realbooru.com/ https://*.e621.net/ https://safebooru.org/; " +
                        "object-src 'self'; " +
                        "script-src 'unsafe-inline' 'unsafe-eval' 'self' ajax.cloudflare.com https://cdn.jsdelivr.net; " +
                        "style-src https://fonts.googleapis.com/ https://fonts.gstatic.com/ 'unsafe-inline' 'self'; " +
                        "frame-ancestors https://top.gg https://discords.com"
        );
        response.setHeader("X-Frame-Options", "allow-from https://top.gg/ https://discords.com/");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Referrer-Policy", "same-origin");
        response.setHeader("Feature-Policy", "microphone 'none'; geolocation 'none'");
        response.setHeader("Access-Control-Allow-Origin", "https://top.gg https://discords.com");
        response.setHeader("X-XSS-Protection", "1; mode=block");

        if (request.getPathInfo().equalsIgnoreCase("/invite")) {
            return handleInvite(response, request.getParameterMap());
        }

        return false;
    }

    private boolean handleInvite(VaadinResponse response, Map<String, String[]> params) {
        response.setHeader("Location", ExternalLinks.BOT_INVITE_URL_EXT);
        response.setStatus(301);

        if (params.size() > 0) {
            String type = new ArrayList<>(params.keySet()).get(0);
            if (type.length() > 0) {
                SendEvent.sendInvite(type);
            }
        }

        return true;
    }

    private boolean handlePrint(VaadinRequest request, String auth) {
        try {
            if (auth.equals(System.getenv("PRINT_AUTH"))) {
                StringBuilder sb = new StringBuilder();
                BufferedReader br = request.getReader();

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                if (sb.length() > 0) {
                    LOGGER.info("Content:\n" + sb);
                }

                return true;
            }
        } catch (IOException e) {
            LOGGER.error("Error while handling upvote", e);
        }

        return false;
    }

    private boolean handleTopGG(VaadinRequest request, String auth) {
        try {
            if (auth.equals(System.getenv("TOPGG_AUTH"))) {
                StringBuilder sb = new StringBuilder();
                BufferedReader br = request.getReader();

                String line;
                while((line = br.readLine()) != null) {
                    sb.append("\n").append(line);
                }

                if (sb.length() > 0) {
                    JSONObject jsonObject = new JSONObject(sb.substring(1));
                    LOGGER.info("UPVOTE | {}", jsonObject.getLong("user"));
                    SendEvent.sendTopGG(jsonObject);
                }

                return true;
            }
        } catch (IOException e) {
            LOGGER.error("Error while handling upvote", e);
        }

        return false;
    }

    private boolean handleTopGGAninoss(VaadinRequest request, String auth) {
        try {
            if (auth.equals(System.getenv("TOPGG_ANINOSS_AUTH"))) {
                StringBuilder sb = new StringBuilder();
                BufferedReader br = request.getReader();

                String line;
                while((line = br.readLine()) != null) {
                    sb.append("\n").append(line);
                }

                if (sb.length() > 0) {
                    JSONObject jsonObject = new JSONObject(sb.substring(1));
                    LOGGER.info("UPVOTE ANINOSS | {}", jsonObject.getLong("user"));
                    SendEvent.sendTopGGAnicord(jsonObject);
                }

                return true;
            }
        } catch (IOException e) {
            LOGGER.error("Error while handling upvote", e);
        }

        return false;
    }

}
