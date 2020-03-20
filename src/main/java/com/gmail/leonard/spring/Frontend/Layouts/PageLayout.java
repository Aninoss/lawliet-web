package com.gmail.leonard.spring.Frontend.Layouts;

import com.gmail.leonard.spring.Backend.Language.PageTitleGen;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;

public class PageLayout extends Main implements HasDynamicTitle {

    private Route route;

    public PageLayout() {
        this.route = this.getClass().getAnnotation(Route.class);
    }

    public String getRoute() { return route.value(); }

    public String getTitleText() { return getTranslation("category." + getRoute()); }

    public static String getRouteStatic(Class<? extends PageLayout> c) {
        return c.getAnnotation(Route.class).value();
    }

    @Override
    public String getPageTitle() { return PageTitleGen.getPageTitle(getRoute()); }

}
