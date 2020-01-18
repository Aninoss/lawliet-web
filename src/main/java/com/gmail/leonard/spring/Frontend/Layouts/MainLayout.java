package com.gmail.leonard.spring.Frontend.Layouts;

import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.CookieConsent;
import com.gmail.leonard.spring.Frontend.Components.FooterArea;
import com.gmail.leonard.spring.Frontend.Components.Header.HeaderComponent;
import com.gmail.leonard.spring.Frontend.Components.Header.VerticalMenuBarComponent;
import com.gmail.leonard.spring.Frontend.Views.IEView;
import com.gmail.leonard.spring.Frontend.Views.PageNotFoundView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.beans.factory.annotation.Autowired;

@CssImport("./styles/styles.css")
@Theme(value = Lumo.class, variant = Lumo.DARK)
@BodySize(width = "100%", height = "100%")
public class MainLayout extends VerticalLayout implements RouterLayout, BeforeEnterObserver, PageConfigurator {

    private SessionData sessionData;

    public MainLayout(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        if (VaadinSession.getCurrent().getBrowser().isIE()) return;

        this.sessionData = sessionData;

        if(UI.getCurrent() != null)
            UI.getCurrent().getElement().getStyle().set("width", "100%");

        setPadding(false);
        setSpacing(false);
        setMinHeight("100vh");
        setId("main-page");
        getStyle()
                .set("flex-direction", "column-reverse");

        Div blackscreen = new Div();
        blackscreen.setId("blackscreen");
        blackscreen.addClassName("visible-xsmall");
        blackscreen.addClassName("expandable");
        blackscreen.getElement().setAttribute("onclick", "verticalBarHide()");

        VerticalMenuBarComponent verticalMenuBarComponent = new VerticalMenuBarComponent(sessionData, uiData);

        UI.getCurrent().getElement().appendChild(new HeaderComponent(sessionData, uiData).getElement());
        UI.getCurrent().getElement().appendChild(blackscreen.getElement());
        UI.getCurrent().getElement().appendChild(verticalMenuBarComponent.getElement());
        UI.getCurrent().getElement().appendChild(new CookieConsent().getElement());

        add(new FooterArea());

        Div divStretch = new Div();
        divStretch.setWidthFull();
        add(divStretch);
        setFlexGrow(1, divStretch);

        UI.getCurrent().getPage().executeJs("onLoad()");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Class<?> c = event.getNavigationTarget();
        if (c != PageNotFoundView.class) sessionData.setCurrentTarget(c);
        if (VaadinSession.getCurrent().getBrowser().isIE() && event.getNavigationTarget() != IEView.class) event.rerouteTo(IEView.class);
    }

    @Override
    public void configurePage(InitialPageSettings settings) {
        settings.addMetaTag("og:title", "Lawliet Bot");
        settings.addMetaTag("og:description", "The official website for the Lawliet Bot");
        settings.addMetaTag("og:image", "http://lawlietbot.xyz/styles/img/bot_icon.png");
        settings.getLoadingIndicatorConfiguration().setFirstDelay(50);
    }
}
