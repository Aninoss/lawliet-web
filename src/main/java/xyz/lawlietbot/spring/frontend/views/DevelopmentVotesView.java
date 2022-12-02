package xyz.lawlietbot.spring.frontend.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

@Route(value = "developmentvotes", layout = MainLayout.class)
@NoLiteAccess
public class DevelopmentVotesView extends PageLayout {

    private final ConfirmationDialog confirmationDialog = new ConfirmationDialog();
    private final HashSet<String> selectedVotes = new HashSet<>();

    public DevelopmentVotesView(@Autowired SessionData sessionData, @Autowired UIData uiData) throws IOException, ExecutionException, InterruptedException {
        super(sessionData, uiData);
        getStyle().set("margin-bottom", "48px");
        boolean loggedIn = getSessionData().isLoggedIn();

        PageHeader pageHeader = new PageHeader(
                getUiData(),
                getTitleText(),
                getTranslation("devvotes.desc")
        );
        pageHeader.getStyle().set("padding-bottom", "42px")
                .set("margin-bottom", "59px");
        add(pageHeader);

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.getStyle()
                .set("margin-top", "-36px")
                .set("margin-bottom", "-4px");
        mainContent.setPadding(true);

        if (!loggedIn) {
            Component errorLabel = generateErrorLabel(getTranslation("devvotes.notloggedin"));
            mainContent.add(errorLabel);
            add(mainContent);
            return;
        }

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        Map<String, Object> requestMap = Map.of(
                "user_id", getSessionData().getDiscordUser().get().getId(),
                "year", year,
                "month", month,
                "locale", getLocale().getLanguage()
        );
        JSONObject dataJson = SendEvent.send(EventOut.DEV_VOTES_INIT, requestMap).get();

        if (!dataJson.getBoolean("premium")) {
            Component errorLabel = generateErrorLabel(getTranslation("devvotes.nopremium"));
            mainContent.add(errorLabel);
            add(mainContent);
            return;
        }

        pageHeader.getOuterLayout()
                .add(generateNotificationField(dataJson.getBoolean("reminder_active")));

        Button sendButton = new Button(getTranslation("devvotes.submit"));
        sendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        sendButton.addClickListener(e -> onButtonClick(year, month));

        JSONArray votesJson = dataJson.getJSONArray("votes");
        for (int i = 0; i < votesJson.length(); i++) {
            String vote = votesJson.getString(i);
            selectedVotes.add(vote);
        }

        mainContent.add(
                new H2(String.format("%02d / %d", month, year)),
                new Text(getTranslation("devvotes.help")),
                generateVotes(month, year),
                sendButton
        );
        add(mainContent, confirmationDialog);
    }

    private Component generateNotificationField(boolean selected) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setPadding(true);
        layout.setSpacing(false);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        layout.getStyle().set("border-radius", "6px")
                .set("background", "var(--lumo-tint-5pct)")
                .set("margin-top", "32px");
        layout.setWidthFull();

        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setValue(selected);
        toggleButton.addValueChangeListener(e -> {
            Map<String, Object> map = Map.of(
                    "user_id", getSessionData().getDiscordUser().get().getId(),
                    "active", e.getValue(),
                    "locale", getLocale().getLanguage()
            );
            SendEvent.send(EventOut.DEV_VOTES_UPDATE_REMINDER, map).join();
            CustomNotification.showSuccess(getTranslation("devvotes.reminderupdate." + e.getValue()));
        });
        layout.add(new Text(getTranslation("devvotes.remindswitch")), toggleButton);

        return layout;
    }

    private void onButtonClick(int year, int month) {
        JSONObject requestJson = new JSONObject();
        requestJson.put("user_id", getSessionData().getDiscordUser().get().getId());
        requestJson.put("year", year);
        requestJson.put("month", month);

        JSONArray votesJsonArray = new JSONArray();
        for (String selectedVote : selectedVotes) {
            votesJsonArray.put(selectedVote);
        }
        requestJson.put("votes", votesJsonArray);

        SendEvent.send(EventOut.DEV_VOTES_UPDATE_VOTES, requestJson).join();
        confirmationDialog.open(getTranslation("devvotes.confirm"), () -> {
        });
    }

    private Component generateErrorLabel(String error) {
        Span errorSpan = new Span(error);
        errorSpan.getStyle().set("color", "var(--lumo-error-text-color)")
                .set("margin-top", "35px");
        return errorSpan;
    }

    private Component generateVotes(int month, int year) throws IOException {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setPadding(false);
        mainLayout.getStyle().set("margin-top", "32px")
                .set("margin-bottom", "16px");

        String filename = System.getenv("DEVVOTES_DIR") + "/" + month + "_" + year + "_" + getLocale().getLanguage() + ".properties";
        File file = new File(filename);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String id = line.split("=")[0].trim();
                String title = line.split("=")[1].trim();

                Checkbox checkbox = new Checkbox(title);
                checkbox.setValue(selectedVotes.contains(id));
                checkbox.setWidthFull();
                checkbox.addValueChangeListener(e -> {
                    if (e.getValue()) {
                        selectedVotes.add(id);
                    } else {
                        selectedVotes.remove(id);
                    }
                });
                mainLayout.add(checkbox);
            }
        }

        return mainLayout;
    }

}
