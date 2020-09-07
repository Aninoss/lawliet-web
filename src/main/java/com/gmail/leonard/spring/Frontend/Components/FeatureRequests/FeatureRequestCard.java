package com.gmail.leonard.spring.Frontend.Components.FeatureRequests;

import com.gmail.leonard.spring.Backend.FeatureRequests.FREntry;
import com.gmail.leonard.spring.Backend.FeatureRequests.FRPanelType;
import com.gmail.leonard.spring.Backend.UserData.DiscordUser;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.Card;
import com.gmail.leonard.spring.Frontend.Components.ConfirmationDialog;
import com.gmail.leonard.spring.Frontend.Components.CustomNotification;
import com.gmail.leonard.spring.Frontend.Styles;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class FeatureRequestCard extends Card {

    private final FREntry frEntry;
    private final SessionData sessionData;
    private final UIData uiData;
    private final VerticalLayout content = new VerticalLayout();
    private Div description;
    private Button boostButton = null;
    private ConfirmationDialog confirmationDialog = null;

    public FeatureRequestCard(FRPanelType type, FREntry frEntry, SessionData sessionData, UIData uiData) {
        this.frEntry = frEntry;
        this.sessionData = sessionData;
        this.uiData = uiData;
        setHeightFull();
        content.setAlignItems(FlexComponent.Alignment.CENTER);

        addTitle();
        addDescription();
        if (frEntry.isPublic()) {
            frEntry.getBoosts().ifPresent(this::addBoostButton);
        } else {
            addNoPublicText(type == FRPanelType.PENDING);
        }

        content.setHeightFull();
        content.setFlexGrow(1, description);

        add(content);

        if (frEntry.isPublic() && frEntry.getBoosts().isPresent()) {
            this.confirmationDialog = new ConfirmationDialog(getTranslation("fr.boost.confirm", frEntry.getTitle()), this::onBoostConfirm, this::onBoostCancel);
            add(confirmationDialog);
        }
    }

    private void addTitle() {
        if (frEntry.getTitle().length() > 0) {
            H3 title = new H3((frEntry.getTitle()));
            title.setWidthFull();
            content.add(title);
        }
    }

    private void addDescription() {
        description = new Div(new Text(frEntry.getDescription()));
        description.setWidthFull();
        content.add(description);
    }

    private void addNoPublicText(boolean pending) {
        addSeperator();
        Div notPublicText = new Div(new Text(getTranslation(pending ? "fr.notpublic.pending" : "fr.notpublic")));
        notPublicText.setWidthFull();
        notPublicText.addClassName(Styles.CENTER_TEXT);
        notPublicText.getStyle().set("color", "var(--lumo-disabled-text-color)");
        content.add(notPublicText);
    }

    private void addBoostButton(int boosts) {
        addSeperator();
        boostButton = new Button(String.valueOf(boosts), VaadinIcon.FIRE.create(), (e) -> onBoostClick());
        boostButton.setWidthFull();
        if (uiData.isLite()) boostButton.setEnabled(false);
        else boostButton.setDisableOnClick(true);
        content.add(boostButton);
    }

    private void addSeperator() {
        Hr seperator = new Hr();
        seperator.getStyle()
                .set("margin-top", "12px")
                .set("margin-bottom", "-4px");
        content.add(seperator);
    }

    private void onBoostClick() {
        if (confirmationDialog != null && !uiData.isLite()) {
            confirmationDialog.open();
        }
    }

    private void onBoostCancel() {
        boostButton.setEnabled(true);
        boostButton.setDisableOnClick(true);
    }

    private void onBoostConfirm() {
        boostButton.setEnabled(true);
        boostButton.setDisableOnClick(true);
        if (sessionData.isLoggedIn()) {
            if (frEntry.boost(sessionData.getDiscordUser().map(DiscordUser::getId).orElse(0L))) {
                boostButton.setText(String.valueOf(frEntry.getBoosts().get()));
                CustomNotification.showSuccess(getTranslation("fr.boost.success", frEntry.getTitle()));
                //turnRed();
            } else {
                CustomNotification.showError(getTranslation("fr.boost.noboosts"));
            }
        } else {
            CustomNotification.showError(getTranslation("fr.boost.notloggedin"));
        }
    }

    private void turnRed() {
        boostButton.getStyle().set("color", "var(--lumo-error-color)");
    }

}