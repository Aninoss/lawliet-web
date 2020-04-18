package com.gmail.leonard.spring;

import com.gmail.leonard.spring.Backend.FileString;
import com.gmail.leonard.spring.ExternalLinks;
import com.gmail.leonard.spring.Frontend.Views.IEView;
import com.vaadin.flow.server.BootstrapListener;
import com.vaadin.flow.server.BootstrapPageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CustomBootstrap implements BootstrapListener {

    final static Logger LOGGER = LoggerFactory.getLogger(CustomBootstrap.class);

    @Override
    public void modifyBootstrapPage(BootstrapPageResponse response) {
        if (response.getSession().getBrowser().isIE()) {
            response.getUI().navigate(IEView.class);
        } else {
            try {
                response.getDocument().body().append(new FileString(
                        Thread.currentThread().getContextClassLoader().getResourceAsStream("bootstrap.html")
                ).toString());

                response.getDocument().head().append("<script src=\"js/scripts.js\"></script>");
                response.getDocument().body().attr("onscroll", "onScroll()");
            } catch (IOException e) {
                LOGGER.error("Error while sending bootstrap page", e);
            }
        }
    }

}
