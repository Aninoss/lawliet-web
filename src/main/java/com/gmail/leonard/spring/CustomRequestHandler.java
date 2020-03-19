package com.gmail.leonard.spring;

import com.gmail.leonard.spring.Frontend.Views.IEView;
import com.vaadin.flow.server.RequestHandler;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinSession;
import java.io.IOException;

public class CustomRequestHandler implements RequestHandler {

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response) throws IOException {
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

}
