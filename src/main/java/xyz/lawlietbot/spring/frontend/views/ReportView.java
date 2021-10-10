package xyz.lawlietbot.spring.frontend.views;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.syncserver.SendEvent;

@Route(value = "report", layout = MainLayout.class)
@NoLiteAccess
public class ReportView extends PageLayout implements HasUrlParameter<String> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReportView.class);

    private final VerticalLayout mainContent = new VerticalLayout();
    private String url;

    public ReportView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);
        getStyle().set("margin-bottom", "48px");
        PageHeader pageHeader = new PageHeader(getUiData(), getTitleText(), getTranslation("report.desc"), null);
        pageHeader.getStyle().set("padding-bottom", "42px")
                .set("margin-bottom", "59px");
        add(pageHeader);

        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.getStyle()
                .set("margin-top", "-20px")
                .set("margin-bottom", "-4px");
        mainContent.setPadding(true);

        add(mainContent);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location
                .getQueryParameters();
        Map<String, List<String>> parameters = queryParameters.getParameters();
        if (parameters.containsKey("content") && parameters.get("content").size() > 0) {
            url = URLDecoder.decode(parameters.get("content").get(0), StandardCharsets.UTF_8);
            mainContent.add(generateQuestion(), generateButtonLayout());
        } else {
            event.rerouteTo(PageNotFoundView.class);
        }
    }

    private Span generateQuestion() {
        String[] textParts = getTranslation("report.text").split("\\|");
        Span span = new Span();
        Anchor a = new Anchor(url, url);
        a.setTarget("_blank");
        span.setWidthFull();
        span.add(textParts[0]);
        span.add(a);
        span.add(textParts[1]);
        return span;
    }

    private HorizontalLayout generateButtonLayout() {
        HorizontalLayout content = new HorizontalLayout();
        content.setPadding(false);
        content.getStyle().set("margin-top", "22px");

        Button yes = new Button(getTranslation("report.yes"));
        yes.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        yes.addClickListener(e -> {
            try {
                SendEvent.sendReport(url).get();
                CustomNotification.showSuccess(getTranslation("report.success"));
                UI.getCurrent().navigate(HomeView.class);
            } catch (InterruptedException | ExecutionException ex) {
                LOGGER.error("Exception", ex);
                LOGGER.error(getTranslation("error"));
            }
        });

        Button no = new Button(getTranslation("report.no"));
        no.addClickListener(e -> UI.getCurrent().navigate(HomeView.class));

        content.add(yes, no);
        return content;
    }

}
