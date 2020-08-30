package com.gmail.leonard.spring.Frontend.Components.FeatureRequests;

import com.gmail.leonard.spring.Backend.FeatureRequests.FRDynamicBean;
import com.gmail.leonard.spring.Backend.UserData.DiscordUser;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.Modules.FeatureRequests;
import com.gmail.leonard.spring.ExternalLinks;
import com.gmail.leonard.spring.Frontend.Components.Card;
import com.gmail.leonard.spring.Frontend.Components.CustomNotification;
import com.gmail.leonard.spring.Frontend.Styles;
import com.gmail.leonard.spring.Frontend.Views.FeatureRequestsNewPostView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class FeatureRequestUserHeader extends Card {

    final static Logger LOGGER = LoggerFactory.getLogger(FeatureRequestUserHeader.class);

    private final FlexLayout notLoggedInLayout = new FlexLayout();
    private final SessionData sessionData;
    private final FRDynamicBean frDynamicBean;
    private HorizontalLayout boostsLayout;

    public FeatureRequestUserHeader(SessionData sessionData, FRDynamicBean frDynamicBean) {
        this.frDynamicBean = frDynamicBean;
        this.sessionData = sessionData;

        setWidthFull();
        getStyle().set("margin-top", "32px");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        notLoggedInLayout.setId("fr-flex");
        notLoggedInLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        notLoggedInLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        notLoggedInLayout.addClassName(Styles.FLEX_MOBILE_SWITCH_COLUMN);
        notLoggedInLayout.setSizeFull();

        if (sessionData.isLoggedIn()) {
            addRemainingBoostsText();
            addBoostButtons();
        } else {
            addNotLoggedInText();
            addLogInButton();
        }

        mainLayout.add(notLoggedInLayout);
        add(mainLayout);
    }

    private void addBoostButtons() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(false);

        Button postButton = new Button(getTranslation("fr.post"), e -> onPostButtonClick());
        postButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button patreonButton = new Button(getTranslation("fr.patreon"));
        patreonButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Anchor patreonAnchor = new Anchor(ExternalLinks.PATREON_PAGE, patreonButton);
        patreonAnchor.setTarget("_blank");

        layout.add(patreonAnchor, postButton);
        notLoggedInLayout.add(layout);
    }

    private void onPostButtonClick() {
        try {
            if (FeatureRequests.canPost(sessionData.getDiscordUser().map(DiscordUser::getId).orElse(0L)).get()) {
                UI.getCurrent().navigate(FeatureRequestsNewPostView.class);
            } else {
                CustomNotification.showError(getTranslation("fr.post.block"));
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error in submit check", e);
            CustomNotification.showError(getTranslation("err.exception.des"));
        }
    }

    private void addRemainingBoostsText() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(false);

        Div boostText = new Div(new Text(getTranslation("fr.freeboosts")));
        boostText.setId("boost-text");
        frDynamicBean.setBoostChangeListener((boostsRemaining, boostsTotal) -> setBoostIcons());
        notLoggedInLayout.add(boostText);

        boostsLayout = new HorizontalLayout();
        boostsLayout.setPadding(false);
        boostsLayout.setSpacing(false);
        setBoostIcons();

        layout.add(boostText, boostsLayout);
        notLoggedInLayout.add(layout);
    }

    private void setBoostIcons() {
        boostsLayout.removeAll();
        for(int i = 0; i < frDynamicBean.getBoostsTotal(); i++) {
            Icon icon = VaadinIcon.FIRE.create();
            if (i < frDynamicBean.getBoostsRemaining())
                icon.setColor("var(--lumo-error-text-color)");
            else
                icon.setColor("var(--lumo-disabled-text-color)");
            icon.getStyle().set("margin-left", "8px");
            boostsLayout.add(icon);
        }
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