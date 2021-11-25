package xyz.lawlietbot.spring.frontend.layouts;

import java.util.Map;
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
import xyz.lawlietbot.spring.LoginAccess;
import xyz.lawlietbot.spring.NavBarSolid;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.SetDivStretchBackground;
import xyz.lawlietbot.spring.backend.Redirector;
import xyz.lawlietbot.spring.backend.language.PageTitleGen;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.CookieConsent;
import xyz.lawlietbot.spring.frontend.components.FooterArea;
import xyz.lawlietbot.spring.frontend.components.header.HeaderComponent;
import xyz.lawlietbot.spring.frontend.components.header.VerticalMenuBarComponent;
import xyz.lawlietbot.spring.frontend.views.ExceptionView;
import xyz.lawlietbot.spring.frontend.views.IEView;
import xyz.lawlietbot.spring.frontend.views.PageNotFoundView;

@CssImport("./styles/styles.css")
@CssImport("./styles/styles-reversed.css")
@CssImport("./styles/bootstrap.css")
@CssImport("./styles/main.css")
@Theme(value = Lumo.class, variant = Lumo.DARK)
@BodySize(width = "100%", height = "100%")
public class MainLayout extends FlexLayout implements RouterLayout, BeforeEnterObserver, PageConfigurator, HasErrorParameter<Exception>, BeforeLeaveObserver {

    private final static Logger LOGGER = LoggerFactory.getLogger(MainLayout.class);

    private SessionData sessionData;
    private UIData uiData;
    private String target;
    private Div divStretch;
    private HeaderComponent headerComponent;

    public MainLayout(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        if (VaadinSession.getCurrent().getBrowser().isIE()) return;

        this.sessionData = sessionData;
        this.uiData = uiData;

        setMinHeight("100vh");
        setWidthFull();
        setId("main-page");
        getStyle().set("flex-direction", "column-reverse")
                .set("overflow-y", "hidden");

        /* black background in mobile burger menu */
        Div blackscreen = new Div();
        blackscreen.setId("blackscreen");
        blackscreen.addClassName(Styles.VISIBLE_MOBILE);
        blackscreen.addClassName("expandable");
        blackscreen.getElement().setAttribute("onclick", "verticalBarHide()");

        VerticalMenuBarComponent verticalMenuBarComponent = new VerticalMenuBarComponent(sessionData, uiData);

        headerComponent = new HeaderComponent(sessionData, uiData);
        UI.getCurrent().getElement().appendChild(headerComponent.getElement());
        UI.getCurrent().getElement().appendChild(blackscreen.getElement());
        UI.getCurrent().getElement().appendChild(verticalMenuBarComponent.getElement());
        UI.getCurrent().getElement().appendChild(new CookieConsent().getElement());

        add(new FooterArea(uiData));

        divStretch = new Div();
        divStretch.setWidthFull();
        add(divStretch);
        setFlexGrow(1, divStretch);

        UI.getCurrent().getPage().executeJs("onLoad()");
        if (sessionData.isLoggedIn()) {
            uiData.login(sessionData.getDiscordUser().get().getId());
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        divStretch.getStyle().set("background", "var(--lumo-base-color)");

        Class<?> cTemp = event.getNavigationTarget();

        if (PageLayout.class.isAssignableFrom(cTemp)) {
            Class<? extends PageLayout> c = (Class<? extends PageLayout>)cTemp;

            if (c.isAnnotationPresent(SetDivStretchBackground.class)) {
                final String background = c.getAnnotation(SetDivStretchBackground.class).background();
                divStretch.getStyle().set("background", background);
            }

            if (checkLiteModeAccess(event)) {
                return;
            }
            setPageTarget(c);
            if (checkBrowserIE(event)) {
                return;
            }
            checkLoginStatusChanged(event);
            headerComponent.setNavBarSolid(event.getNavigationTarget().isAnnotationPresent(NavBarSolid.class));
        }
    }

    @Override
    public void configurePage(InitialPageSettings settings) {
        settings.addMetaTag("og:type", "website");
        settings.addMetaTag("og:site_name", getTranslation("bot.name"));
        settings.addMetaTag("og:title", PageTitleGen.getPageTitle(target));
        settings.addMetaTag("og:description", getTranslation("bot.desc"));
        settings.addMetaTag("og:image", "http://lawlietbot.xyz/styles/img/bot_icon.webp");

        //Favicons
        settings.addLink("/apple-touch-icon.png", Map.of(
                "rel",  "apple-touch-icon",
                "sizes", "180x180"
        ));
        settings.addLink("manifest", "/site.webmanifest");
        settings.addMetaTag("theme-color", "#ffffff");
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

        LoginAccess loginAccess = target.getAnnotation(LoginAccess.class);
        if (!sessionData.isLoggedIn() && loginAccess != null) {
            if (PageLayout.class.isAssignableFrom(target))
                sessionData.setCurrentTarget((Class<? extends PageLayout>)target);

            new Redirector().redirect(sessionData.getLoginUrl());
            event.postpone();
        }
    }

    private void checkLoginStatusChanged(BeforeEnterEvent event) {
        LoginAccess loginAccess = event.getNavigationTarget().getAnnotation(LoginAccess.class);
        if (!sessionData.isLoggedIn() && loginAccess != null) {
            new Redirector().redirect(sessionData.getLoginUrl());
            return;
        }

        if (sessionData != null) {
            if ((sessionData.isLoggedIn() && !uiData.getUserId().isPresent()) ||
                    (!sessionData.isLoggedIn() && uiData.getUserId().isPresent()) ||
                    (sessionData.isLoggedIn() && !uiData.getUserId().get().equals(sessionData.getDiscordUser().get().getId()))
            ) {
                UI.getCurrent().getPage().reload();
            }
        }
    }

    private boolean checkBrowserIE(BeforeEnterEvent event) {
        if (VaadinSession.getCurrent().getBrowser().isIE() && event.getNavigationTarget() != IEView.class) {
            event.rerouteTo(IEView.class);
            return true;
        }
        return false;
    }

    private void setPageTarget(Class<? extends PageLayout> c) {
        target = PageLayout.getRouteStatic(c);
        if (c != PageNotFoundView.class) sessionData.setCurrentTarget(c);
    }

    private boolean checkLiteModeAccess(BeforeEnterEvent event) {
        if (uiData != null && uiData.isLite() && event.getNavigationTarget().isAnnotationPresent(NoLiteAccess.class)) {
            event.rerouteToError(Exception.class);
            return true;
        }
        return false;
    }

}
