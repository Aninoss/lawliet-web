package com.gmail.leonard.spring;

import com.gmail.leonard.spring.Backend.SecretManager;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.WebComClient;
import com.vaadin.flow.server.RequestHandler;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinSession;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CustomRequestHandler implements RequestHandler {

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response) throws IOException {
        String auth = request.getHeader("Authorization");

        if (auth != null) {
            if (request.getPathInfo().equals("/topgg") && handleTopGG(request, auth)) return true;
            if (request.getPathInfo().equals("/donatebotio") && handleDonatebotIO(request, auth)) return true;
        }

        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubdomains");
        response.setHeader("Content-Security-Policy",
                "default-src data: 'self'; " +
                        "img-src 'self' https://cdn.discordapp.com/; " +
                        "media-src 'self'; " +
                        "object-src 'self'; " +
                        "script-src 'unsafe-inline' 'unsafe-eval' 'self' ajax.cloudflare.com; " +
                        "style-src 'unsafe-inline' 'self'; " +
                        "frame-ancestors *"
        );
        response.setHeader("X-Frame-Options", "allow-from https://top.gg/");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Referrer-Policy", "same-origin");
        response.setHeader("Feature-Policy", "microphone 'none'; geolocation 'none'");
        response.setHeader("Access-Control-Allow-Origin", "https://top.gg");

        return false;
    }

    private boolean handleTopGG(VaadinRequest request, String auth) {
        try {
            if (auth.equals(SecretManager.getString("discordbots.auth"))) {
                StringBuilder sb = new StringBuilder();
                BufferedReader br = request.getReader();

                String line;
                while((line = br.readLine()) != null) {
                    sb.append("\n").append(line);
                }

                if (sb.length() > 0) WebComClient.getInstance().sendTopGG(new JSONObject(sb.toString().substring(1))).get();
                return true;
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean handleDonatebotIO(VaadinRequest request, String auth) {
        try {
            if (auth.equals(SecretManager.getString("donation.auth"))) {
                StringBuilder sb = new StringBuilder();
                BufferedReader br = request.getReader();

                String line;
                while((line = br.readLine()) != null) {
                    sb.append("\n").append(line);
                }

                if (sb.length() > 0) WebComClient.getInstance().sendDonatebotIO(new JSONObject(sb.toString().substring(1))).get();
                return true;
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

}
