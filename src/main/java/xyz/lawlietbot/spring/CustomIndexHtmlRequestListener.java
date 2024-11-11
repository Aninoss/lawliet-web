package xyz.lawlietbot.spring;

import com.vaadin.flow.server.communication.IndexHtmlRequestListener;
import com.vaadin.flow.server.communication.IndexHtmlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.lawlietbot.spring.backend.util.FileUtil;

import java.io.IOException;

public class CustomIndexHtmlRequestListener implements IndexHtmlRequestListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomIndexHtmlRequestListener.class);

    @Override
    public void modifyIndexHtmlResponse(IndexHtmlResponse response) {
        try {
            String bootstrapHtml = FileUtil.readResource("bootstrap.html");

            response.getDocument().body().append(bootstrapHtml);
            response.getDocument().head()
                    .appendElement("script")
                    .attr("src", "js/scripts.js");
            response.getDocument().body()
                    .attr("onscroll", "onScroll()");
        } catch (IOException e) {
            LOGGER.error("Error while loading bootstrap page", e);
        }
    }
}