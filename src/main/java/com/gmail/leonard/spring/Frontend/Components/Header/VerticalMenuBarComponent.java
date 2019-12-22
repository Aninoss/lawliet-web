package com.gmail.leonard.spring.Frontend.Components.Header;

import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.CustomButton;
import com.gmail.leonard.spring.Frontend.Components.DiscordIcon;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;

public class VerticalMenuBarComponent extends VerticalLayout {

    public VerticalMenuBarComponent(SessionData sessionData, UIData uiData) {
        setId("vertical-menu");
        addClassName("expandable");
        setHeightFull();
        setPadding(false);

        NavigationBar verticalTabs = new NavigationBar(uiData);
        verticalTabs.setOrientation(Tabs.Orientation.VERTICAL);
        verticalTabs.getStyle()
                .set("position", "relative")
                .set("top", "60px");
        verticalTabs.getElement().setAttribute("onclick", "verticalBarHide()");

        Nav nav = new Nav(verticalTabs);
        add(nav);

        if (!uiData.isLite()) {
            if (sessionData.isLoggedIn()) {
                Span status = new Span(getTranslation("login.status", sessionData.getUserName()));
                status.setWidthFull();
                status.addClassName("center-text");
                status.getStyle().set("margin-bottom", "-4px");
                add(status);

                CustomButton logout = new CustomButton(getTranslation("logout"), VaadinIcon.SIGN_OUT_ALT.create());
                logout.setWidthFull();
                Anchor logoutAnchor = new Anchor("/discordlogout", logout);
                logoutAnchor.getStyle().set("margin-bottom", "16px");
                logoutAnchor.setWidthFull();
                add(logoutAnchor);
            } else {
                Span status = new Span(getTranslation("logout.status"));
                status.setWidthFull();
                status.addClassName("center-text");
                status.getStyle().set("margin-bottom", "-12px");
                add(status);

                CustomButton login = new CustomButton(getTranslation("login"), new DiscordIcon());
                login.setWidthFull();
                login.setHeight("42px");
                login.getStyle().set("color", "white");
                Anchor loginAnchor = new Anchor(sessionData.getLoginUrl(), login);
                loginAnchor.getStyle().set("margin-bottom", "16px");
                loginAnchor.setWidthFull();
                add(loginAnchor);
            }
        }

        setFlexGrow(1, nav);
    }
}
