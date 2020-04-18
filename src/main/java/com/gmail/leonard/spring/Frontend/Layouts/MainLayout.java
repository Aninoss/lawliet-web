package com.gmail.leonard.spring.Frontend.Layouts;

import com.gmail.leonard.spring.Backend.Language.PageTitleGen;
import com.gmail.leonard.spring.Backend.Redirector;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.CookieConsent;
import com.gmail.leonard.spring.Frontend.Components.FooterArea;
import com.gmail.leonard.spring.Frontend.Components.Header.HeaderComponent;
import com.gmail.leonard.spring.Frontend.Components.Header.VerticalMenuBarComponent;
import com.gmail.leonard.spring.Frontend.Styles;
import com.gmail.leonard.spring.Frontend.Views.ExceptionView;
import com.gmail.leonard.spring.Frontend.Views.IEView;
import com.gmail.leonard.spring.Frontend.Views.PageNotFoundView;
import com.gmail.leonard.spring.LoginAccess;
import com.gmail.leonard.spring.NoLiteAccess;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@CssImport("./styles/styles.css")
@CssImport("./styles/bootstrap.css")
@CssImport("./styles/main.css")
@Theme(value = Lumo.class, variant = Lumo.DARK)
@BodySize(width = "100%", height = "100%")
public class MainLayout extends FlexLayout implements RouterLayout, BeforeEnterObserver, PageConfigurator, HasErrorParameter<Exception>, BeforeLeaveObserver {

    final static Logger LOGGER = LoggerFactory.getLogger(MainLayout.class);

    private SessionData sessionData;
    private UIData uiData;
    private String target;

    public MainLayout(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        if (VaadinSession.getCurrent().getBrowser().isIE()) return;

        this.sessionData = sessionData;
        this.uiData = uiData;

        if(UI.getCurrent() != null)
            UI.getCurrent().getElement().getStyle().set("width", "100%");

        setMinHeight("100vh");
        setId("main-page");
        getStyle()
                .set("flex-direction", "column-reverse");

        Div blackscreen = new Div();
        blackscreen.setId("blackscreen");
        blackscreen.addClassName(Styles.VISIBLE_MOBILE);
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
        if (sessionData.isLoggedIn()) {
            uiData.login(sessionData.getUserId().get());
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Class<?> cTemp = event.getNavigationTarget();

        if (PageLayout.class.isAssignableFrom(cTemp)) {
            Class<? extends PageLayout> c = (Class<? extends PageLayout>)cTemp;

            if (uiData.isLite() && event.getNavigationTarget().isAnnotationPresent(NoLiteAccess.class)) {
                event.rerouteToError(Exception.class);
                return;
            }

            target = PageLayout.getRouteStatic(c);

            if (c != PageNotFoundView.class) sessionData.setCurrentTarget(c);
            if (VaadinSession.getCurrent().getBrowser().isIE() && event.getNavigationTarget() != IEView.class) event.rerouteTo(IEView.class);

            if (!sessionData.isLoggedIn() && event.getNavigationTarget().isAnnotationPresent(LoginAccess.class)) {
                new Redirector().redirect(sessionData.getLoginUrl());
                return;
            }

            if ((sessionData.isLoggedIn() && !uiData.getUserId().isPresent()) ||
                    (!sessionData.isLoggedIn() && uiData.getUserId().isPresent()) ||
                    (sessionData.isLoggedIn() && !uiData.getUserId().get().equals(sessionData.getUserId().get()))
            ) {
                UI.getCurrent().getPage().reload();
            }
        }
    }

    @Override
    public void configurePage(InitialPageSettings settings) {
        settings.addMetaTag("og:type", "website");
        settings.addMetaTag("og:site_name", getTranslation("bot.name"));
        settings.addMetaTag("og:title", PageTitleGen.getPageTitle(target));
        settings.addMetaTag("og:description", getTranslation("bot.desc"));
        settings.addMetaTag("og:image", "http://lawlietbot.xyz/styles/img/bot_icon.png");
        settings.addMetaTag("msapplication-config", "/browserconfig.xml");
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent beforeEnterEvent, ErrorParameter<Exception> errorParameter) {
        LOGGER.error("Error in page initialization", errorParameter.getException());
        beforeEnterEvent.rerouteTo(ExceptionView.class);
        return 500;
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        Class<?> target = event.getNavigationTarget();

        if (uiData.isLite() && target.isAnnotationPresent(NoLiteAccess.class)) {
            event.postpone();
            return;
        }

        if (!sessionData.isLoggedIn() && target.isAnnotationPresent(LoginAccess.class)) {
            if (PageLayout.class.isAssignableFrom(target))
                sessionData.setCurrentTarget((Class<? extends PageLayout>)target);

            new Redirector().redirect(sessionData.getLoginUrl());
            event.postpone();
        }
    }
}
