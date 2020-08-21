package com.gmail.leonard.spring.Frontend.Components.FeatureRequests;

import com.github.appreciated.card.Card;
import com.gmail.leonard.spring.Backend.StringUtil;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.DiscordIcon;
import com.gmail.leonard.spring.Frontend.Styles;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Objects;
import java.util.stream.Stream;

public class FeatureRequestUserHeader extends Card {

    private final VerticalLayout mainLayout = new VerticalLayout();
    private final FlexLayout notLoggedInLayout = new FlexLayout();
    private final SessionData sessionData;

    public FeatureRequestUserHeader(SessionData sessionData) {
        setWidthFull();
        this.sessionData = sessionData;
        mainLayout.setAlignItems(Alignment.CENTER);

        if (sessionData.isLoggedIn()) {

        } else {
            notLoggedInLayout.setId("fr-flex");
            notLoggedInLayout.setAlignItems(Alignment.CENTER);
            notLoggedInLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
            notLoggedInLayout.addClassName(Styles.FLEX_MOBILE_SWITCH_COLUMN);
            notLoggedInLayout.setSizeFull();
            addNotLoggedInText();
            addLogInButton();
        }

        mainLayout.add(notLoggedInLayout);
        add(mainLayout);
    }

    private void addNotLoggedInText() {
        Div notLoggedInText = new Div(new Text(getTranslation("fr.notloggedin")));
        notLoggedInText.setId("not-logged-in-text");
        notLoggedInLayout.add(notLoggedInText);
    }

    private void addLogInButton() {
        Button login = new Button(getTranslation("login"));
        login.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Anchor loginAnchor = new Anchor(sessionData.getLoginUrl(), login);
        notLoggedInLayout.add(loginAnchor);
    }

}