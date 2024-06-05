package xyz.lawlietbot.spring.frontend.components.header;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.DiscordIcon;
import xyz.lawlietbot.spring.frontend.components.LawlietBotLogo;
import xyz.lawlietbot.spring.frontend.components.LocaleSelect;
import xyz.lawlietbot.spring.frontend.views.DiscordLogout;
import xyz.lawlietbot.spring.frontend.views.HomeView;

public class HeaderComponent extends Header {

    public HeaderComponent(SessionData sessionData, UIData uiData) {
        setId("header-out");
        if (uiData.isLite()) {
            addClassName("lite");
        }

        HorizontalLayout content = new HorizontalLayout();
        content.setId("header-content");
        content.setPadding(false);
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.addClassNames(Styles.FADE_IN);

        //Show Menu Button
        Button showMenu = new Button("");
        showMenu.getElement().setAttribute("onclick", "verticalBarSwitch()");
        showMenu.addClassName(Styles.VISIBLE_SMALL);
        showMenu.setId("menu-toggle");
        showMenu.setIcon(VaadinIcon.MENU.create());
        content.add(showMenu);

        //Logo
        Button logo = new Button(new LawlietBotLogo());
        logo.setMinWidth("200px");
        logo.getStyle().set("margin-left", "8px");
        logo.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logo.addClickListener(click -> UI.getCurrent().navigate(HomeView.class));
        content.add(logo);

        //Navigation Bar
        NavigationBar navigationBar = new NavigationBar(uiData);
        navigationBar.setOrientation(Tabs.Orientation.HORIZONTAL);

        Nav nav = new Nav(navigationBar);
        nav.setMinWidth("0px");
        nav.addClassName(Styles.VISIBLE_NOT_SMALL);
        content.add(nav);

        //Login Elements
        if (!uiData.isLite()) {
            if (sessionData.isLoggedIn()) {
                DiscordUser discordUser = sessionData.getDiscordUser().get();

                Image userIcon = new Image(discordUser.getUserAvatar(), "");
                userIcon.setHeight("48px");
                userIcon.addClassName(Styles.ROUND);
                content.add(userIcon);

                VerticalLayout accountName = new VerticalLayout();
                accountName.setSpacing(false);
                accountName.setPadding(false);
                accountName.setSizeUndefined();
                accountName.addClassName(Styles.VISIBLE_NOT_SMALL);

                Div username = new Div(new Text(discordUser.getUsername()));
                username.getStyle()
                        .set("margin-left", "-4px")
                        .set("color", "white");
                username.addClassName(Styles.VISIBLE_NOT_SMALL);
                RouterLink link = new RouterLink(getTranslation("logout"), DiscordLogout.class);
                link.getStyle()
                        .set("margin-top", "-4px")
                        .set("font-size", "80%")
                        .set("margin-left", "-4px");
                link.addClassName(Styles.VISIBLE_NOT_SMALL);

                accountName.add(username, link);
                content.add(accountName);
            } else {
                Button login = new Button(getTranslation("login"), new DiscordIcon());
                login.getStyle().set("color", "white")
                        .set("margin-right", "-6px");
                login.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                Anchor loginAnchor = new Anchor(sessionData.getLoginUrl(), login);
                loginAnchor.addClassName(Styles.VISIBLE_NOT_SMALL);
                content.add(loginAnchor);
            }

            LocaleSelect localeSelect = new LocaleSelect();
            localeSelect.addClassNames(Styles.VISIBLE_NOT_SMALL);
            content.add(localeSelect);
        }

        content.setFlexGrow(1, nav);
        add(content);
    }

    public void setNavBarSolid(boolean solid) {
        setClassName("solid", solid);
    }

}
