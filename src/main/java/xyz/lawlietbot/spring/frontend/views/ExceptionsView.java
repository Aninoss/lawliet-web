package xyz.lawlietbot.spring.frontend.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.LoginAccess;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.components.SpanWithLinebreaks;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;

@Route(value = "exceptions", layout = MainLayout.class)
@NoLiteAccess
@LoginAccess
public class ExceptionsView extends PageLayout {

    private final VerticalLayout mainLayout = new VerticalLayout();

    public ExceptionsView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);
        getStyle().set("margin-bottom", "48px");

        if (!sessionData.isLoggedIn() || sessionData.getDiscordUser().get().getId() != 272037078919938058L) {
            return;
        }

        VerticalLayout mainContent = new VerticalLayout(mainLayout);
        mainContent.setWidthFull();
        mainContent.setPadding(true);

        add(
                new PageHeader(getUiData(), getTitleText(), null, generateHeaderMenu()),
                mainContent
        );
    }

    private Component generateHeaderMenu() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidthFull();
        mainLayout.setPadding(false);

        HorizontalLayout optionsLayout = new HorizontalLayout();
        optionsLayout.setWidthFull();
        optionsLayout.setPadding(false);
        optionsLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        mainLayout.add(optionsLayout);

        List<String> options = listDirs();

        Select<String> dirSelect = new Select<>();
        dirSelect.setItems(options);
        dirSelect.setValue(options.get(0));
        dirSelect.setLabel("Service");
        optionsLayout.add(dirSelect);

        DatePicker datePickerFrom = new DatePicker();
        datePickerFrom.setMin(LocalDate.now().minusDays(30));
        datePickerFrom.setMax(LocalDate.now());
        datePickerFrom.setValue(LocalDate.now());
        datePickerFrom.setLabel("From");
        datePickerFrom.setLocale(Locale.UK);
        optionsLayout.add(datePickerFrom);

        DatePicker datePickerTo = new DatePicker();
        datePickerTo.setMin(LocalDate.now().minusDays(30));
        datePickerTo.setMax(LocalDate.now());
        datePickerTo.setValue(LocalDate.now());
        datePickerTo.setLabel("To");
        datePickerTo.setLocale(Locale.UK);
        optionsLayout.add(datePickerTo);

        HorizontalLayout operationsLayout = new HorizontalLayout();
        operationsLayout.setWidthFull();
        operationsLayout.setPadding(false);
        mainLayout.add(operationsLayout);

        TextArea textAreaHide = new TextArea();
        textAreaHide.setLabel("Hide");
        textAreaHide.setValue(getTextValueFromCookies("exceptions-hide-" + dirSelect.getValue()));
        dirSelect.addValueChangeListener(e -> textAreaHide.setValue(getTextValueFromCookies("exceptions-hide-" + e.getValue())));
        operationsLayout.add(textAreaHide);
        operationsLayout.setFlexGrow(1, textAreaHide);

        TextArea textAreaGroup = new TextArea();
        textAreaGroup.setLabel("Group");
        textAreaGroup.setValue(getTextValueFromCookies("exceptions-group-" + dirSelect.getValue()));
        dirSelect.addValueChangeListener(e -> textAreaGroup.setValue(getTextValueFromCookies("exceptions-group-" + e.getValue())));
        operationsLayout.add(textAreaGroup);
        operationsLayout.setFlexGrow(1, textAreaGroup);

        Button button = new Button("Fetch");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> {
            String dir = dirSelect.getValue();
            LocalDate dateFrom = datePickerFrom.getValue();
            LocalDate dateTo = datePickerTo.getValue();

            Cookie cookieHide = new Cookie("exceptions-hide-" + dir, textAreaHide.getValue().replace("\n", "\\n"));
            cookieHide.setHttpOnly(true);
            cookieHide.setSecure(true);
            cookieHide.setMaxAge(Integer.MAX_VALUE);
            VaadinService.getCurrentResponse().addCookie(cookieHide);

            Cookie cookieGroup = new Cookie("exceptions-group-" + dir, textAreaGroup.getValue().replace("\n", "\\n"));
            cookieGroup.setHttpOnly(true);
            cookieGroup.setSecure(true);
            cookieGroup.setMaxAge(Integer.MAX_VALUE);
            VaadinService.getCurrentResponse().addCookie(cookieGroup);

            List<String> headersHidden = Arrays.stream(textAreaHide.getValue().split("\n"))
                    .filter(h -> !h.isEmpty())
                    .collect(Collectors.toList());

            List<String> headersGrouped = Arrays.stream(textAreaGroup.getValue().split("\n"))
                    .filter(h -> !h.isEmpty())
                    .collect(Collectors.toList());

            updateMainLayout(dir, dateFrom, dateTo, headersHidden, headersGrouped);
        });
        mainLayout.add(button);

        return mainLayout;
    }

    private List<String> listDirs() {
        File dir = new File(System.getenv("LOGS_DIR"));
        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .map(File::getName)
                .collect(Collectors.toList());
    }

    private String getTextValueFromCookies(String name) {
        for (Cookie cookie : VaadinService.getCurrentRequest().getCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue().replace("\\n", "\n");
            }
        }
        return "";
    }

    private void updateMainLayout(String dir, LocalDate dateFrom, LocalDate dateTo, List<String> headersHidden,
                                  List<String> headersGrouped
    ) {
        mainLayout.removeAll();
        Map<String, List<String>> map = new LinkedHashMap<>();
        for (LocalDate date = dateTo; !date.isBefore(dateFrom); date = date.minusDays(1)) {
            File logFile = getLogFile(dir, date);
            List<String> logMessages = extractLogMessages(logFile);
            groupLogMessagesToMap(logMessages, map, headersGrouped);
        }

        int initialSize = map.size();
        applyHeaderHide(map, headersHidden);
        List<String> sortedMapKeys = map.keySet().stream()
                .sorted((s0, s1) -> Integer.compare(map.get(s1).size(), map.get(s0).size()))
                .collect(Collectors.toList());

        mainLayout.add(new Text(map.size() + " / " + initialSize + " Groups"));
        for (String header : sortedMapKeys) {
            FlexLayout contentLayout = new FlexLayout();
            contentLayout.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
            List<String> logMessageList = map.get(header);
            int limit = Math.min(25, logMessageList.size());
            for (int i = 0; i < limit; i++) {
                int spreadIndex = (int) Math.round(i * ((double) logMessageList.size() / limit));
                String logMessage = logMessageList.get(spreadIndex);
                contentLayout.add(new Hr(), new SpanWithLinebreaks(logMessage.replace("\t", "  ")));
            }

            Details details = new Details(new SpanWithLinebreaks("[" + logMessageList.size() + "] " + header), contentLayout);
            details.getStyle().set("width", "100%");
            details.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
            mainLayout.add(details);
        }
    }

    private File getLogFile(String dir, LocalDate date) {
        return new File(System.getenv("LOGS_DIR") + "/" + dir + "/stderr/" + date + ".log");
    }

    private List<String> extractLogMessages(File file) {
        ArrayList<String> messages = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty() && line.charAt(0) >= '0' && line.charAt(0) <= '9') {
                    if (sb.length() > 0) {
                        messages.add(0, sb.toString());
                    }
                    sb = new StringBuilder(line);
                } else {
                    sb.append("\n")
                            .append(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (sb.length() > 0) {
            messages.add(sb.toString());
        }

        return messages;
    }

    private void groupLogMessagesToMap(List<String> logMessages, Map<String, List<String>> map,
                                       List<String> headersGrouped
    ) {
        for (String logMessage : logMessages) {
            String header = extractLogHeader(logMessage);
            if (header == null) {
                continue;
            }
            for (String group : headersGrouped) {
                if (header.contains(group)) {
                    header = group;
                    break;
                }
            }
            List<String> messageList = map.computeIfAbsent(header, k -> new ArrayList<>());
            messageList.add(logMessage);
        }
    }

    private String extractLogHeader(String message) {
        String[] lines = message.split("\n");
        String header;
        if (lines.length >= 2 && !lines[1].startsWith("\t")) {
            header = lines[0] + "\n" + lines[1];
        } else {
            header = lines[0];
        }
        if (header.contains("[ERROR]")) {
            return header.substring(header.indexOf("[ERROR]") + 8);
        } else if (header.contains("[WARN]")) {
            return header.substring(header.indexOf("[WARN]") + 7);
        } else {
            return null;
        }
    }

    private void applyHeaderHide(Map<String, List<String>> map, List<String> headersHidden) {
        for (String header : new ArrayList<>(map.keySet())) {
            if (headersHidden.stream().anyMatch(header::contains)) {
                map.remove(header);
            }
        }
    }

}
