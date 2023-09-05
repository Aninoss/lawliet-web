package xyz.lawlietbot.spring.frontend.components.dashboard.adapters;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import dashboard.component.DashboardText;
import org.apache.commons.lang3.StringEscapeUtils;

public class DashboardTextAdapter extends Div {

    public DashboardTextAdapter(DashboardText dashboardText) {
        switch (dashboardText.getStyle()) {
            case ERROR:
                getStyle().set("color", "var(--lumo-error-text-color)");
                break;

            case SUCCESS:
                getStyle().set("color", "--lumo-success-text-color");
                break;

            default:
        }

        if (dashboardText.getUrl() == null) {
            getElement().setProperty("innerHTML", StringEscapeUtils.escapeHtml4(dashboardText.getText()).replace("\n", "<br>"));
        } else {
            Anchor a = new Anchor(dashboardText.getUrl(), dashboardText.getText());
            a.setTarget("_blank");
            add(a);
        }
    }

}
