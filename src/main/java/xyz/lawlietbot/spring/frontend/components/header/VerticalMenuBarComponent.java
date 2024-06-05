package xyz.lawlietbot.spring.frontend.components.header;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.DiscordIcon;
import xyz.lawlietbot.spring.frontend.components.LocaleSelect;

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
            VerticalLayout settingsLayout = new VerticalLayout();
            settingsLayout.setWidthFull();
            settingsLayout.setPadding(true);

            LocaleSelect localeSelect = new LocaleSelect();
            settingsLayout.add(localeSelect, new Hr());

            if (sessionData.isLoggedIn()) {
                DiscordUser discordUser = sessionData.getDiscordUser().get();

                Div status = new Div(new Text(getTranslation("login.status", discordUser.getUsername())));
                status.setWidthFull();
                status.addClassName(Styles.CENTER_TEXT);
                status.getStyle().set("margin-bottom", "-12px");
                settingsLayout.add(status);

                Button logout = new Button(getTranslation("logout"), VaadinIcon.SIGN_OUT_ALT.create());
                logout.setWidthFull();
                Anchor logoutAnchor = new Anchor("/discordlogout", logout);
                logoutAnchor.setWidthFull();
                settingsLayout.add(logoutAnchor);
            } else {
                Div status = new Div(new Text((getTranslation("logout.status"))));
                status.setWidthFull();
                status.addClassName(Styles.CENTER_TEXT);
                status.getStyle().set("margin-bottom", "-12px");
                settingsLayout.add(status);

                Button login = new Button(getTranslation("login"), new DiscordIcon());
                login.setWidthFull();
                login.setHeight("42px");
                login.getStyle().set("color", "white");
                Anchor loginAnchor = new Anchor(sessionData.getLoginUrl(), login);
                loginAnchor.setWidthFull();
                settingsLayout.add(loginAnchor);
            }

            add(settingsLayout);
        }

        setFlexGrow(1, nav);
    }
}
