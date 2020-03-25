package com.gmail.leonard.spring.Frontend.Components.Header;

import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.CustomButton;
import com.gmail.leonard.spring.Frontend.Components.DiscordIcon;
import com.gmail.leonard.spring.Frontend.Views.DiscordLogout;
import com.gmail.leonard.spring.Frontend.Views.HomeView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;

public class HeaderComponent extends Header {

    public HeaderComponent(SessionData sessionData, UIData uiData)  {
        getStyle()
                .set("position", "fixed")
                .set("z-index", "6");
        addClassName("app-width");
        addClassName("center-fixed");
        addClassName("fadein-class");

        HorizontalLayout content = new HorizontalLayout();
        content.setId("header-size");

        content.setPadding(false);
        content.setAlignItems(FlexComponent.Alignment.CENTER);

        //Show Menu Button
        CustomButton showMenu = new CustomButton("");
        showMenu.getElement().setAttribute("onclick", "verticalBarSwitch()");
        showMenu.addClassName("visible-xsmall");
        showMenu.setId("menu-toggle");
        showMenu.setIcon(new Icon(VaadinIcon.MENU));
        content.add(showMenu);

        //Logo
        String logoString = VaadinServletService.getCurrent()
                .resolveResource("/styles/img/logo.png",
                        VaadinSession.getCurrent().getBrowser());

        Image logo = new Image(logoString, "");
        logo.setHeightFull();
        logo.addClassName("pointer");
        logo.addClickListener(click -> UI.getCurrent().navigate(HomeView.class));
        content.add(logo);

        //Navigation Bar
        NavigationBar tabs = new NavigationBar(uiData);
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        tabs.addClassName("visible-not-xsmall");

        Nav nav = new Nav(tabs);
        nav.setMinWidth("0px");
        content.add(nav);

        //Login Elements
        if (!uiData.isLite()) {
            if (sessionData.isLoggedIn()) {
                Image userIcon = new Image(sessionData.getUserAvatar().get(), "");
                userIcon.setHeightFull();
                userIcon.addClassName("round");
                content.add(userIcon);

                VerticalLayout accountName = new VerticalLayout();
                accountName.setSpacing(false);
                accountName.setPadding(false);
                accountName.setSizeUndefined();
                accountName.addClassName("visible-not-xsmall");

                Span username = new Span(sessionData.getUserName().get());
                username.getStyle()
                        .set("margin-top", "-3px")
                        .set("margin-left", "-4px")
                        .set("color", "white");
                username.addClassName("visible-not-xsmall");
                RouterLink link = new RouterLink(getTranslation("logout"), DiscordLogout.class);
                link.getStyle()
                        .set("margin-top", "-4px")
                        .set("font-size", "80%")
                        .set("margin-left", "-4px");
                link.addClassName("visible-not-xsmall");

                accountName.add(username, link);
                content.add(accountName);
            } else {
                CustomButton login = new CustomButton(getTranslation("login"), new DiscordIcon());
                login.setHeight("42px");
                login.getStyle().set("color", "white");
                login.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                Anchor loginAnchor = new Anchor(sessionData.getLoginUrl(), login);
                loginAnchor.addClassName("visible-not-xsmall");
                content.add(loginAnchor);
            }
        }

        content.setFlexGrow(1, nav);
        add(content);
    }

}
