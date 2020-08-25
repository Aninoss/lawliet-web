package com.gmail.leonard.spring.Frontend.Components.FeatureRequests;

import com.github.appreciated.card.Card;
import com.gmail.leonard.spring.Backend.FeatureRequests.FREntry;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Frontend.Components.ConfirmationDialog;
import com.gmail.leonard.spring.Frontend.Components.CustomNotification;
import com.gmail.leonard.spring.Frontend.Styles;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.checkerframework.checker.units.qual.C;

public class FeatureRequestCard extends Card {

    private final FREntry frEntry;
    private final SessionData sessionData;
    private final VerticalLayout content = new VerticalLayout();
    private Div description;
    private Button boostButton = null;
    private ConfirmationDialog confirmationDialog = null;

    public FeatureRequestCard(FREntry frEntry, SessionData sessionData) {
        this.frEntry = frEntry;
        this.sessionData = sessionData;
        setHeightFull();
        content.setAlignItems(FlexComponent.Alignment.CENTER);

        addDescription();
        if (frEntry.isPublic()) {
            frEntry.getBoosts().ifPresent(this::addBoostButton);
        } else {
            addNoPublicText();
        }

        content.setHeightFull();
        content.setFlexGrow(1, description);

        add(content);

        if (frEntry.isPublic() && frEntry.getBoosts().isPresent()) {
            this.confirmationDialog = new ConfirmationDialog(getTranslation("fr.boost.confirm"), this::onBoostConfirm, this::onBoostCancel);
            add(confirmationDialog);
        }
    }

    private void addDescription() {
        description = new Div(new Text(frEntry.getDescription()));
        content.add(description);
    }

    private void addNoPublicText() {
        addSeperator();
        Div notPublicText = new Div(new Text(getTranslation("fr.notpublic")));
        notPublicText.setWidthFull();
        notPublicText.addClassName(Styles.CENTER_TEXT);
        notPublicText.getStyle().set("color", "var(--lumo-disabled-text-color)");
        content.add(notPublicText);
    }

    private void addBoostButton(int boosts) {
        addSeperator();
        boostButton = new Button(String.valueOf(boosts), VaadinIcon.FIRE.create(), (e) -> onBoostClick());
        boostButton.setWidthFull();
        boostButton.setDisableOnClick(true);
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
        if (confirmationDialog != null) {
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
        if (sessionData.isLoggedIn() && frEntry.boost(sessionData.getUserId().get())) {
            boostButton.setText(String.valueOf(frEntry.getBoosts().get()));
            turnRed();
        } else {
            CustomNotification.showError(getTranslation("fr.boost.noboosts"));
        }
    }

    private void turnRed() {
        boostButton.getStyle().set("color", "var(--lumo-error-color)");
    }

}