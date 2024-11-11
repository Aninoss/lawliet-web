package xyz.lawlietbot.spring;

import com.vaadin.flow.server.communication.IndexHtmlRequestListener;
import com.vaadin.flow.server.communication.IndexHtmlResponse;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.lawlietbot.spring.backend.util.FileUtil;

public class CustomIndexHtmlRequestListener implements IndexHtmlRequestListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomIndexHtmlRequestListener.class);

    @Override
    public void modifyIndexHtmlResponse(IndexHtmlResponse response) {
        Document document = response.getDocument();
        try {
            String bootstrapHtml = FileUtil.readResource("bootstrap.html");
            document.body().append(bootstrapHtml);
        } catch (Throwable e) {
            LOGGER.error("Error while loading bootstrap page", e);
        }

        document.head()
                .appendElement("script")
                .attr("src", "js/scripts.js");
        document.body()
                .attr("onscroll", "onScroll()");
    }
}