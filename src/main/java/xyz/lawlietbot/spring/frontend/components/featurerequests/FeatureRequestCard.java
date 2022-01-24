package xyz.lawlietbot.spring.frontend.components.featurerequests;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import xyz.lawlietbot.spring.backend.featurerequests.FREntry;
import xyz.lawlietbot.spring.backend.featurerequests.FRPanelType;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.backend.util.StringUtil;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.LineBreak;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;

public class FeatureRequestCard extends Div {

    private final FREntry frEntry;
    private final SessionData sessionData;
    private final UIData uiData;
    private final VerticalLayout content = new VerticalLayout();
    private final VerticalLayout action = new VerticalLayout();
    private Div description;
    private Div popular;
    private Button boostButton = null;
    private ConfirmationDialog confirmationDialog = null;

    public FeatureRequestCard(FREntry frEntry, ConfirmationDialog confirmationDialog, SessionData sessionData, UIData uiData) {
        this.frEntry = frEntry;
        this.sessionData = sessionData;
        this.uiData = uiData;
        setHeightFull();
        getStyle().set("display", "flex")
                .set("flex-direction", "column")
                .set("border-radius", "8px");

        content.setAlignItems(FlexComponent.Alignment.CENTER);
        action.setAlignItems(FlexComponent.Alignment.CENTER);

        addTitle();
        addDescription();
        if (frEntry.getBoosts().isPresent()) {
            addFooter();
            addSeperator(action);
            addBoostButton(frEntry.getBoosts().get());
            this.confirmationDialog = confirmationDialog;
            add(confirmationDialog);
        } else {
            addNoPublicText(frEntry.getType() == FRPanelType.PENDING);
        }

        content.getStyle()
                .set("flex-grow", "1")
                .set("background", "var(--lumo-tint-5pct)")
                .set("border-top-left-radius", "8px")
                .set("border-top-right-radius", "8px");
        content.setFlexGrow(1, description);

        action.getStyle()
                .set("background", "var(--lumo-secondary)")
                .set("border-bottom-left-radius", "8px")
                .set("border-bottom-right-radius", "8px");

        add(content, action);
    }

    private void addTitle() {
        if (frEntry.getTitle().length() > 0) {
            H3 title = new H3((frEntry.getTitle()));
            title.setWidthFull();
            content.add(title);
        }
    }

    private void addFooter() {
        Div footerDiv = new Div();
        footerDiv.setWidthFull();
        footerDiv.getStyle().set("display", "flex")
                .set("flex-direction", "row")
                .set("justify-content", "space-between");

        Div ageString = new Div(new Text(getAgeText()));
        ageString.getStyle().set("font-size", "80%");
        footerDiv.add(ageString);

        popular = new Div(new Text(getPopularString()));
        popular.getStyle().set("font-size", "80%");
        footerDiv.add(popular);

        action.add(footerDiv);
    }

    private String getAgeText() {
        StringBuilder footer = new StringBuilder();
        int daysBetween = (int)ChronoUnit.DAYS.between(frEntry.getDate(), LocalDate.now());
        switch (daysBetween) {
            case 0:
                footer.append(getTranslation("fr.card.today"));
                break;

            case 1:
                footer.append(getTranslation("fr.card.yesterday"));
                break;

            default:
                footer.append(getTranslation("fr.card.ago", StringUtil.numToString(daysBetween)));
        }

        return footer.toString();
    }

    public String getPopularString() {
        int recentBoosts = frEntry.getRecentBoosts().orElse(0);
        return recentBoosts > 0 ? getTranslation("fr.card.recent", recentBoosts) : "";
    }

    private void addDescription() {
        description = new Div(new Text(frEntry.getDescription()));
        description.setWidthFull();
        content.add(description);
    }

    private void addNoPublicText(boolean pending) {
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
        action.add(notPublicText);
    }

    private void addBoostButton(int boosts) {
        boostButton = new Button(String.valueOf(boosts), VaadinIcon.FIRE.create(), (e) -> onBoostClick());
        boostButton.setWidthFull();
        if (uiData.isLite()) boostButton.setEnabled(false);
        else boostButton.setDisableOnClick(true);
        action.add(boostButton);
    }

    private void addSeperator(VerticalLayout div) {
        Hr seperator = new Hr();
        seperator.getStyle()
                .set("margin-top", "12px")
                .set("margin-bottom", "-4px");
        div.add(seperator);
    }

    private void onBoostClick() {
        if (confirmationDialog != null && !uiData.isLite()) {
            Label label = new Label(getTranslation("fr.boost.confirm", frEntry.getTitle()));
            label.add(new LineBreak());
            label.add(getTranslation("fr.boost.confirm.notpush"));
            label.getStyle().set("color", "black");
            confirmationDialog.open(label, this::onBoostConfirm, this::onBoostCancel);
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
                popular.setText(getPopularString());
                CustomNotification.showSuccess(getTranslation("fr.boost.success", frEntry.getTitle()));
            } else {
                CustomNotification.showError(getTranslation("fr.boost.noboosts"));
            }
        } else {
            CustomNotification.showError(getTranslation("fr.boost.notloggedin"));
        }
    }

}