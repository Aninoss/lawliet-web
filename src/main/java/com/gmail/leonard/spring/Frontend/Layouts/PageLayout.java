package com.gmail.leonard.spring.Frontend.Layouts;

import com.gmail.leonard.spring.Backend.Language.PageTitleGen;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Views.HomeView;
import com.gmail.leonard.spring.NoLiteAccess;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import org.springframework.beans.factory.annotation.Autowired;

public class PageLayout extends Main implements HasDynamicTitle {

    private Route route;
    private UIData uiData;
    private SessionData sessionData;

    public PageLayout(SessionData sessionData, UIData uiData) {
        this.route = this.getClass().getAnnotation(Route.class);
        this.uiData = uiData;
        this.sessionData = sessionData;
        this.setWidth("100vw");
    }

    public String getRoute() { return route.value(); }

    public String getTitleText() { return getTranslation("category." + getRoute()); }

    public static String getRouteStatic(Class<? extends PageLayout> c) {
        return c.getAnnotation(Route.class).value();
    }

    @Override
    public String getPageTitle() { return PageTitleGen.getPageTitle(getRoute()); }

    public UIData getUiData() {
        return uiData;
    }

    public SessionData getSessionData() {
        return sessionData;
    }
}
