package xyz.lawlietbot.spring.frontend.views;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.Route;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.backend.util.StringUtil;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Route(value = "developmentvotes", layout = MainLayout.class)
@NoLiteAccess
public class DevelopmentVotesView extends PageLayout {

    private final ConfirmationDialog confirmationDialog = new ConfirmationDialog();
    private final HashSet<String> selectedVotes = new HashSet<>();

    public DevelopmentVotesView(@Autowired SessionData sessionData, @Autowired UIData uiData) throws IOException, ExecutionException, InterruptedException, JSONException {
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
        int tempMonth = calendar.get(Calendar.MONTH) + 1;
        int tempYear = calendar.get(Calendar.YEAR);
        int month = tempMonth == 1 ? 12 : tempMonth;
        int year = tempMonth == 1 ? tempYear - 1 : tempYear;

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

        boolean votesOpen = dataJson.has("votes");
        int totalVotes = dataJson.has("total_votes") ? dataJson.getInt("total_votes") : -1;
        mainContent.add(
                new H2(String.format("%02d / %d", month, year)),
                new Text(getTranslation(votesOpen ? "devvotes.help" : "devvotes.results.help")),
                generateVotes(month, year, dataJson, votesOpen, totalVotes)
        );

        if (votesOpen) {
            Button sendButton = new Button(getTranslation("devvotes.submit"));
            sendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            sendButton.addClickListener(e -> {
                try {
                    onButtonClick(year, month);
                } catch (JSONException ex) {
                    throw new RuntimeException(ex);
                }
            });
            mainContent.add(sendButton);
        }

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

    private void onButtonClick(int year, int month) throws JSONException {
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

    private Component generateVotes(int month, int year, JSONObject dataJson, boolean votesOpen, int totalVotes) throws IOException, JSONException {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setPadding(true);
        mainLayout.getStyle().set("margin-top", "32px")
                .set("margin-bottom", "16px")
                .set("border-radius", "8px")
                .set("border-style", "solid")
                .set("border-width", "2px")
                .set("border-color", "var(--lumo-contrast-10pct)");

        String filename = System.getenv("DEVVOTES_DIR") + "/" + month + "_" + year + "_" + getLocale().getLanguage() + ".properties";
        File file = new File(filename);

        if (votesOpen) {
            JSONArray votesJson = dataJson.getJSONArray("votes");
            for (int i = 0; i < votesJson.length(); i++) {
                String vote = votesJson.getString(i);
                selectedVotes.add(vote);
            }

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
        } else {
            HashMap<String, String> voteLabelMap = new HashMap<>();
            HashMap<String, Integer> votePositionMap = new HashMap<>();
            HashMap<String, Integer> voteNumberMap = new HashMap<>();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                int i = 0;
                String line;
                while ((line = br.readLine()) != null) {
                    String id = line.split("=")[0].trim();
                    String title = line.split("=")[1].trim();
                    voteLabelMap.put(id, title);
                    votePositionMap.put(id, i);
                    voteNumberMap.put(id, 0);
                    i++;
                }
            }

            JSONArray voteResultJsonArray = dataJson.getJSONArray("vote_result");
            for (int i = 0; i < voteResultJsonArray.length(); i++) {
                JSONObject voteResultJson = voteResultJsonArray.getJSONObject(i);
                String id = voteResultJson.getString("id");
                int number = voteResultJson.getInt("number");
                voteNumberMap.put(id, number);
            }

            voteLabelMap.keySet().stream()
                    .sorted((id0, id1) -> {
                        if (voteNumberMap.get(id0) < voteNumberMap.get(id1)) {
                            return 1;
                        } else if (voteNumberMap.get(id0) > voteNumberMap.get(id1)) {
                            return -1;
                        } else {
                            return Integer.compare(votePositionMap.get(id0), votePositionMap.get(id1));
                        }
                    })
                    .forEach(id -> {
                        String label = voteLabelMap.get(id);
                        int number = voteNumberMap.get(id);
                        String text = getTranslation(
                                "devvotes.results.slot",
                                label,
                                StringUtil.numToString(number),
                                StringUtil.numToString(totalVotes)
                        );
                        Div textDiv = new Div();
                        textDiv.setText(text);
                        ProgressBar progressBar = new ProgressBar();
                        progressBar.setValue((double) number / totalVotes);
                        progressBar.getStyle().set("margin-top", "8px");
                        mainLayout.add(textDiv, progressBar);
                    });
        }

        return mainLayout;
    }

}
