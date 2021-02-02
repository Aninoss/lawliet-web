package xyz.lawlietbot.spring.frontend.layouts;

import xyz.lawlietbot.spring.backend.language.PageTitleGen;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;

public class PageLayout extends Main implements HasDynamicTitle {

    private final Route route;
    private final UIData uiData;
    private final SessionData sessionData;

    public PageLayout(SessionData sessionData, UIData uiData) {
        this.route = this.getClass().getAnnotation(Route.class);
        this.uiData = uiData;
        this.sessionData = sessionData;
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
