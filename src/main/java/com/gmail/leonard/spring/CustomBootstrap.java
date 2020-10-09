package com.gmail.leonard.spring;

import com.gmail.leonard.spring.backend.FileString;
import com.gmail.leonard.spring.frontend.views.IEView;
import com.vaadin.flow.server.BootstrapListener;
import com.vaadin.flow.server.BootstrapPageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CustomBootstrap implements BootstrapListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(CustomBootstrap.class);

    @Override
    public void modifyBootstrapPage(BootstrapPageResponse response) {
        if (response.getSession().getBrowser().isIE()) {
            response.getUI().navigate(IEView.class);
        } else {
            try {
                String pageString = new FileString(
                        Thread.currentThread().getContextClassLoader().getResourceAsStream("bootstrap.html")
                ).toString();
                response.getDocument().body().append(pageString);

                response.getDocument().head().append("<script src=\"js/scripts.js\"></script>");
                response.getDocument().head().append("<script data-ad-client=\"ca-pub-6615556433120664\" async src=\"https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js\"></script>");
                response.getDocument().body().attr("onscroll", "onScroll()");
            } catch (IOException e) {
                LOGGER.error("Error while sending bootstrap page", e);
            }
        }
    }

}
