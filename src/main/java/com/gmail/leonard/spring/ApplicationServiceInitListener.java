package com.gmail.leonard.spring;

import com.gmail.leonard.spring.Backend.FileString;
import com.gmail.leonard.spring.Frontend.Views.IEView;
import com.vaadin.flow.server.BootstrapListener;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

import java.io.IOException;

public class ApplicationServiceInitListener implements VaadinServiceInitListener {

    public ApplicationServiceInitListener() {}

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.addBootstrapListener((BootstrapListener) response ->
            {
                if (response.getSession().getBrowser().isIE()) {
                    response.getUI().navigate(IEView.class);
                } else {
                    try {
                        response.getDocument().body().append(new FileString(
                                Thread.currentThread().getContextClassLoader().getResourceAsStream("bootstrap.html")
                        ).toString());

                        response.getDocument().head().append("<script src=\"js/scripts.js\"></script>");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        );
    }

}
