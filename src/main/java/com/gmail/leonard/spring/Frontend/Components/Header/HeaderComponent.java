package com.gmail.leonard.spring.Frontend.Components.Header;

import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.DiscordIcon;
import com.gmail.leonard.spring.Frontend.Styles;
import com.gmail.leonard.spring.Frontend.Views.DiscordLogout;
import com.gmail.leonard.spring.Frontend.Views.HomeView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
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
        addClassName("app-width-wide");
        addClassNames(Styles.CENTER_FIXED_WIDTH, Styles.FADE_IN);

        HorizontalLayout content = new HorizontalLayout();
        content.setId("header-size");

        content.setPadding(false);
        content.setAlignItems(FlexComponent.Alignment.CENTER);

        //Show Menu Button
        Button showMenu = new Button("");
        showMenu.getElement().setAttribute("onclick", "verticalBarSwitch()");
        showMenu.addClassName(Styles.VISIBLE_MOBILE);
        showMenu.setId("menu-toggle");
        showMenu.setIcon(new Icon(VaadinIcon.MENU));
        content.add(showMenu);

        //Logo
        String logoString = VaadinServletService.getCurrent()
                .resolveResource("/styles/img/logo.png",
                        VaadinSession.getCurrent().getBrowser());

        Image logo = new Image(logoString, "");
        logo.setHeightFull();
        logo.addClassName(Styles.POINTER);
        logo.addClickListener(click -> UI.getCurrent().navigate(HomeView.class));
        content.add(logo);

        //Navigation Bar
        NavigationBar tabs = new NavigationBar(uiData);
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        tabs.addClassName(Styles.VISIBLE_NOTMOBILE);

        Nav nav = new Nav(tabs);
        nav.setMinWidth("0px");
        content.add(nav);

        //Login Elements
        if (!uiData.isLite()) {
            if (sessionData.isLoggedIn()) {
                Image userIcon = new Image(sessionData.getUserAvatar().get(), "");
                userIcon.setHeightFull();
                userIcon.addClassName(Styles.ROUND);
                content.add(userIcon);

                VerticalLayout accountName = new VerticalLayout();
                accountName.setSpacing(false);
                accountName.setPadding(false);
                accountName.setSizeUndefined();
                accountName.addClassName(Styles.VISIBLE_NOTMOBILE);

                Div username = new Div(new Text(sessionData.getUserName().get()));
                username.getStyle()
                        .set("margin-top", "-3px")
                        .set("margin-left", "-4px")
                        .set("color", "white");
                username.addClassName(Styles.VISIBLE_NOTMOBILE);
                RouterLink link = new RouterLink(getTranslation("logout"), DiscordLogout.class);
                link.getStyle()
                        .set("margin-top", "-4px")
                        .set("font-size", "80%")
                        .set("margin-left", "-4px");
                link.addClassName(Styles.VISIBLE_NOTMOBILE);

                accountName.add(username, link);
                content.add(accountName);
            } else {
                Button login = new Button(getTranslation("login"), new DiscordIcon());
                login.setHeight("42px");
                login.getStyle().set("color", "white");
                login.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                Anchor loginAnchor = new Anchor(sessionData.getLoginUrl(), login);
                loginAnchor.addClassName(Styles.VISIBLE_NOTMOBILE);
                content.add(loginAnchor);
            }
        }

        content.setFlexGrow(1, nav);
        add(content);
    }

}
