package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.*;
import dashboard.component.DashboardText;
import xyz.lawlietbot.spring.RegexPatterns;
import xyz.lawlietbot.spring.backend.util.VaadinUtil;
import xyz.lawlietbot.spring.frontend.components.LineBreak;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.regex.Matcher;

public class DashboardTextAdapter extends Div {

    public DashboardTextAdapter(DashboardText dashboardText, boolean titleText) {
        switch (dashboardText.getStyle()) {
            case ERROR:
                addClassName("dashboard-text-error");
                break;

            case SUCCESS:
                addClassName("dashboard-text-success");
                break;

            case WARNING:
                addClassName("dashboard-text-warning");
                break;

            case SECONDARY:
                addClassName("dashboard-text-secondary");
                break;

            case BOLD:
                addClassName("dashboard-text-bold");
                break;

            case HINT:
                addClassName("dashboard-text-hint");
                getStyle().set("margin-top", "0.5rem");
                break;

            default:
        }

        if (titleText) {
            addClassName("dashboard-subheader-description");
        }

        String text = dashboardText.getText();
        if (text.contains("<t:")) {
            text = rewriteTimestamp(text);
        }

        if (dashboardText.getUrl() == null) {
            add(generateText(text));
        } else {
            Anchor a = new Anchor(dashboardText.getUrl(), text);
            a.setTarget("_blank");
            add(a);
        }
    }

    private String rewriteTimestamp(String text) {
        boolean matches;
        do {
            Matcher matcher = RegexPatterns.TIMESTAMP_GROUP_PATTERN.matcher(text);
            matches = matcher.matches();
            if (matches) {
                String timestamp = matcher.group("timestamp");
                Instant instant = Instant.ofEpochSecond(Long.parseLong(timestamp));

                ZoneId zoneId = VaadinUtil.getCurrentZoneId();
                ZonedDateTime eventTime = ZonedDateTime.ofInstant(instant, zoneId);
                ZonedDateTime now = ZonedDateTime.now(zoneId);

                DateTimeFormatter dateTimeFormatter;
                if (eventTime.getDayOfYear() == now.getDayOfYear() && eventTime.getYear() == now.getYear()) {
                    dateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(UI.getCurrent().getLocale());
                } else {
                    dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(UI.getCurrent().getLocale());
                }

                text = text.replace("<t:" + timestamp + ":f>", eventTime.format(dateTimeFormatter));
            }
        } while (matches);

        return text;
    }

    private Span generateText(String text) {
        Span span = new Span();

        String[] lines = text.split("\n");
        UnorderedList ul = null;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            if (line.startsWith("- ")) {
                line = line.substring(2);
                if (ul == null) {
                    ul = new UnorderedList();
                }
                ul.add(new ListItem(generateTextLine(line)));
            } else {
                if (ul != null) {
                    span.add(ul);
                    ul = null;
                }

                if (i > 0) {
                    span.add(new LineBreak());
                }
                span.add(generateTextLine(line));
            }
        }

        if (ul != null) {
            span.add(ul);
        }

        return span;
    }

    private Span generateTextLine(String text) {
        Span span = new Span();

        Matcher matcher = RegexPatterns.URL_PATTERN.matcher(text);
        int index = 0;
        while(matcher.find()) {
            span.add(new Span(text.substring(index, matcher.start())));

            Anchor a = new Anchor(matcher.group(), matcher.group());
            a.setTarget("_blank");
            span.add(a);

            index = matcher.end();
        }

        if (index <= text.length()) {
            span.add(new Span(text.substring(index)));
        }
        return span;
    }

}
