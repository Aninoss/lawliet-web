package xyz.lawlietbot.spring.frontend.layouts;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import jakarta.servlet.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.LoginAccess;
import xyz.lawlietbot.spring.NavBarSolid;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.SetDivStretchBackground;
import xyz.lawlietbot.spring.backend.Redirector;
import xyz.lawlietbot.spring.backend.payment.Subscription;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.CookieConsent;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.frontend.components.FooterArea;
import xyz.lawlietbot.spring.frontend.components.LocaleSelect;
import xyz.lawlietbot.spring.frontend.components.header.HeaderComponent;
import xyz.lawlietbot.spring.frontend.components.header.VerticalMenuBarComponent;
import xyz.lawlietbot.spring.frontend.views.ExceptionView;
import xyz.lawlietbot.spring.frontend.views.IEView;
import xyz.lawlietbot.spring.syncserver.SyncUtil;

import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

@CssImport("./styles/styles.css")
@CssImport("./styles/styles-reversed.css")
@CssImport("./styles/bootstrap.css")
@CssImport("./styles/main.css")
public class MainLayout extends FlexLayout implements RouterLayout, BeforeEnterObserver, HasErrorParameter<Exception>, BeforeLeaveObserver {

    private final static Logger LOGGER = LoggerFactory.getLogger(MainLayout.class);

    private final SessionData sessionData;
    private final UIData uiData;
    private final Div divStretch = new Div();
    private HeaderComponent headerComponent;

    public MainLayout(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        this.sessionData = sessionData;
        this.uiData = uiData;
        if (VaadinService.getCurrentRequest().getCookies() != null) {
            for (Cookie cookie : VaadinService.getCurrentRequest().getCookies()) {
                if (cookie.getName().equals(LocaleSelect.LOCALE_COOKIE_NAME)) {
                    UI.getCurrent().setLocale(new Locale(cookie.getValue()));
                    break;
                }
            }
        }

        if (VaadinSession.getCurrent().getBrowser().isIE()) {
            return;
        }

        setMinHeight("100vh");
        setWidthFull();
        setId("main-page");
        getStyle().set("flex-direction", "column-reverse")
                .set("overflow-y", "hidden");

        /* black background in mobile burger menu */
        Div blackscreen = new Div();
        blackscreen.setId("blackscreen");
        blackscreen.addClassName(Styles.VISIBLE_SMALL);
        blackscreen.addClassName("expandable");
        blackscreen.getElement().setAttribute("onclick", "verticalBarHide()");

        VerticalMenuBarComponent verticalMenuBarComponent = new VerticalMenuBarComponent(sessionData, uiData);

        headerComponent = new HeaderComponent(sessionData, uiData);
        UI.getCurrent().getElement().appendChild(headerComponent.getElement());
        UI.getCurrent().getElement().appendChild(blackscreen.getElement());
        UI.getCurrent().getElement().appendChild(verticalMenuBarComponent.getElement());
        UI.getCurrent().getElement().appendChild(new CookieConsent().getElement());

        add(new FooterArea(uiData));

        divStretch.setWidthFull();
        add(divStretch);
        setFlexGrow(1, divStretch);

        UI.getCurrent().getPage().executeJs("onLoad()");
        if (sessionData.isLoggedIn()) {
            uiData.login(sessionData.getDiscordUser().get().getId());
        }

        showNotifications(sessionData);

        Long userId = sessionData.getDiscordUser().map(DiscordUser::getId).orElse(null);
        startProfitWell(userId);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        divStretch.getStyle().set("background", "var(--lumo-base-color)");
        Class<?> cTemp = event.getNavigationTarget();

        if (PageLayout.class.isAssignableFrom(cTemp)) {
            Class<? extends PageLayout> c = (Class<? extends PageLayout>) cTemp;

            if (c.isAnnotationPresent(SetDivStretchBackground.class)) {
                final String background = c.getAnnotation(SetDivStretchBackground.class).background();
                divStretch.getStyle().set("background", background);
            }

            if (checkLiteModeAccess(event)) {
                return;
            }
            if (checkBrowserIE(event)) {
                return;
            }
            sessionData.setCurrentTarget(event.getLocation());
            checkLoginStatusChanged(event);
            headerComponent.setNavBarSolid(event.getNavigationTarget().isAnnotationPresent(NavBarSolid.class));
        }
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
            sessionData.setCurrentTarget(event.getLocation());
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

        if ((sessionData.isLoggedIn() && !uiData.getUserId().isPresent()) ||
                (!sessionData.isLoggedIn() && uiData.getUserId().isPresent()) ||
                (sessionData.isLoggedIn() && !uiData.getUserId().get().equals(sessionData.getDiscordUser().get().getId()))
        ) {
            UI.getCurrent().getPage().reload();
        }
    }

    private boolean checkBrowserIE(BeforeEnterEvent event) {
        if (VaadinSession.getCurrent().getBrowser().isIE() && event.getNavigationTarget() != IEView.class) {
            event.rerouteTo(IEView.class);
            return true;
        }
        return false;
    }

    private boolean checkLiteModeAccess(BeforeEnterEvent event) {
        if (uiData != null && uiData.isLite() && event.getNavigationTarget().isAnnotationPresent(NoLiteAccess.class)) {
            event.rerouteToError(Exception.class);
            return true;
        }
        return false;
    }

    private void showNotifications(SessionData sessionData) {
        for (String messageKey : sessionData.flushErrorMessages()) {
            CustomNotification.showError(getTranslation(messageKey));
        }
    }

    private void startProfitWell(Long userId) {
        String email = null;
        if (userId != null) {
            try {
                Subscription subscription = SyncUtil.retrievePaddleSubscriptions(userId, 0).get()
                        .stream()
                        .max(Comparator.comparingLong(Subscription::getPlanId))
                        .orElse(null);
                if (subscription != null) {
                    email = subscription.getEmail();
                }
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Profit well exception", e);
                return;
            }
        }

        if (email != null) {
            UI.getCurrent().getPage().executeJs("profitwell('start', { 'user_email': '" + email + "' })");
        } else {
            UI.getCurrent().getPage().executeJs("profitwell('start', {})");
        }
    }

}
