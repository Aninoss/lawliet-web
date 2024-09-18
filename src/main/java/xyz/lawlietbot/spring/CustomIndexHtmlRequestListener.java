package xyz.lawlietbot.spring;

import com.vaadin.flow.server.communication.IndexHtmlRequestListener;
import com.vaadin.flow.server.communication.IndexHtmlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.lawlietbot.spring.backend.FileString;

import java.io.IOException;

public class CustomIndexHtmlRequestListener implements IndexHtmlRequestListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(CustomIndexHtmlRequestListener.class);

    @Override
    public void modifyIndexHtmlResponse(IndexHtmlResponse response) {
        try {
            String pageString = new FileString(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream("bootstrap.html")
            ).toString();
            response.getDocument().body().append(pageString);

            response.getDocument().head().append("<script src=\"js/scripts.js\"></script>");
            response.getDocument().body().attr("onscroll", "onScroll()");
        } catch (IOException e) {
            LOGGER.error("Error while sending bootstrap page", e);
        }
    }

}
