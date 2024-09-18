package xyz.lawlietbot.spring;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

public class ApplicationServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.addIndexHtmlRequestListener(new CustomIndexHtmlRequestListener());
        event.addRequestHandler(new CustomRequestHandler());
    }

}