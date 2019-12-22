package com.gmail.leonard.spring.Frontend.Views;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;

@Route
public class IEView extends Div implements BeforeEnterObserver {

    public IEView() {
        setSizeFull();
        add(new H1(getTranslation("ie.title")));
        add(new Html("<p>"+ getTranslation("ie.content") + "</p>"));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!VaadinSession.getCurrent().getBrowser().isIE()) event.rerouteTo(PageNotFoundView.class);
    }

}
