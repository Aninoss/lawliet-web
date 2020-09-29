package com.gmail.leonard.spring.frontend.components.featurerequests;

import com.gmail.leonard.spring.backend.StringUtil;
import com.gmail.leonard.spring.backend.featurerequests.FREntry;
import com.gmail.leonard.spring.backend.featurerequests.FRPanelType;
import com.gmail.leonard.spring.backend.userdata.DiscordUser;
import com.gmail.leonard.spring.backend.userdata.SessionData;
import com.gmail.leonard.spring.backend.userdata.UIData;
import com.gmail.leonard.spring.frontend.components.Card;
import com.gmail.leonard.spring.frontend.components.ConfirmationDialog;
import com.gmail.leonard.spring.frontend.components.CustomNotification;
import com.gmail.leonard.spring.frontend.Styles;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class FeatureRequestCard extends Card {

    private final FREntry frEntry;
    private final SessionData sessionData;
    private final UIData uiData;
    private final VerticalLayout content = new VerticalLayout();
    private Div description;
    private Button boostButton = null;
    private ConfirmationDialog confirmationDialog = null;

    public FeatureRequestCard(FREntry frEntry, SessionData sessionData, UIData uiData) {
        this.frEntry = frEntry;
        this.sessionData = sessionData;
        this.uiData = uiData;
        setHeightFull();
        content.setAlignItems(FlexComponent.Alignment.CENTER);

        addTitle();
        addDescription();
        addDateString();
        if (frEntry.getBoosts().isPresent()) {
            addBoostButton(frEntry.getBoosts().get());

            Label warningLabel = new Label(getTranslation("fr.boost.confirm.notpush"));
            warningLabel.getStyle().set("color", "black")
                    .set("margin-bottom", "32px");
            this.confirmationDialog = new ConfirmationDialog(getTranslation("fr.boost.confirm", frEntry.getTitle()), this::onBoostConfirm, this::onBoostCancel, warningLabel);
            add(confirmationDialog);
        } else {
            addNoPublicText(frEntry.getType() == FRPanelType.PENDING);
        }

        content.setHeightFull();
        content.setFlexGrow(1, description);

        add(content);
    }

    private void addTitle() {
        if (frEntry.getTitle().length() > 0) {
            H3 title = new H3((frEntry.getTitle()));
            title.setWidthFull();
            content.add(title);
        }
    }

    private void addDateString() {
        String formattedDate;
        int daysBetween = (int)ChronoUnit.DAYS.between(frEntry.getDate(), LocalDate.now());
        switch (daysBetween) {
            case 0:
                formattedDate = getTranslation("fr.card.today");
                break;

            case 1:
                formattedDate = getTranslation("fr.card.yesterday");
                break;

            default:
                formattedDate = getTranslation("fr.card.ago", StringUtil.numToString(getLocale(), daysBetween));
        }

        Div dateString = new Div(new Text(formattedDate));
        dateString.setWidthFull();
        dateString.getStyle().set("font-size", "80%")
                .set("color", "var(--lumo-disabled-text-color)");
        content.add(dateString);
    }

    private void addDescription() {
        description = new Div(new Text(frEntry.getDescription()));
        description.setWidthFull();
        content.add(description);
    }

    private void addNoPublicText(boolean pending) {
        addSeperator();
        Div notPublicText;
        if (pending) {
            notPublicText = new Div(new Text(getTranslation("fr.notpublic.pending")));
            notPublicText.getStyle().set("color", "var(--lumo-disabled-text-color)");
        } else {
            notPublicText = new Div(new Text(getTranslation("fr.notpublic.rejected")));
            notPublicText.getStyle().set("color", "var(--lumo-error-color)");
        }
        notPublicText.setWidthFull();
        notPublicText.addClassName(Styles.CENTER_TEXT);
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