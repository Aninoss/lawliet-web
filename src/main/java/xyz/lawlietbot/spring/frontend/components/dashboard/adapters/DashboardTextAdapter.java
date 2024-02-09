package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import dashboard.component.DashboardText;
import xyz.lawlietbot.spring.RegexPatterns;
import xyz.lawlietbot.spring.backend.util.VaadinUtil;
import xyz.lawlietbot.spring.frontend.components.SpanWithLinebreaks;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.regex.Matcher;

public class DashboardTextAdapter extends Div {

    public DashboardTextAdapter(DashboardText dashboardText) {
        switch (dashboardText.getStyle()) {
            case ERROR:
                getStyle().set("color", "var(--lumo-error-text-color)");
                break;

            case SUCCESS:
                getStyle().set("color", "--lumo-success-text-color");
                break;

            case WARNING:
                getStyle().set("color", "rgb(var(--warning-color-rgb))");
                break;

            case SECONDARY:
                getStyle().set("color", "var(--secondary-text-color)");
                break;

            case BOLD:
                getStyle().set("font-weight", "bold");
                break;

            default:
        }

        String text = dashboardText.getText();
        if (text.contains("<t:")) {
            text = rewriteTimestamp(text);
        }

        if (dashboardText.getUrl() == null) {
            add(new SpanWithLinebreaks(text));
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

}
