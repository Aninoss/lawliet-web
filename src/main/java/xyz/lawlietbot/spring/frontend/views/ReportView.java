package xyz.lawlietbot.spring.frontend.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.report.ContentType;
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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Route(value = "report", layout = MainLayout.class)
@NoLiteAccess
public class ReportView extends PageLayout implements HasUrlParameter<String> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReportView.class);

    private final VerticalLayout mainContent = new VerticalLayout();
    private Select<String> urlSelect;
    private Div previewDiv;
    private Anchor link;
    private TextField reason;
    private String[] urls;

    public ReportView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);
        getStyle().set("margin-bottom", "48px");
        PageHeader pageHeader = new PageHeader(getUiData(), getTitleText(), getTranslation("report.desc"));
        pageHeader.getStyle().set("padding-bottom", "42px")
                .set("margin-bottom", "59px");
        add(pageHeader);

        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.getStyle()
                .set("margin-top", "-20px")
                .set("margin-bottom", "-4px");
        mainContent.setPadding(true);

        add(mainContent);

        if (sessionData.isLoggedIn()) {
            ConfirmationDialog confirmationDialog = new ConfirmationDialog();
            confirmationDialog.open(getTranslation("report.confirmationdialog"), () -> {
            });
            add(confirmationDialog);
        }
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location
                .getQueryParameters();
        Map<String, List<String>> parameters = queryParameters.getParameters();
        if (parameters.containsKey("content") && !parameters.get("content").isEmpty()) {
            String encodedImageUrl = URLDecoder.decode(parameters.get("content").get(0), StandardCharsets.UTF_8);
            urls = new String(Base64.getDecoder().decode(encodedImageUrl.getBytes())).split(",");
            translateUrls();
            mainContent.add(
                    generateComboBoxAndLink(),
                    generateContentPreview(),
                    generateSeparator(),
                    generateBottomComponents()
            );
        } else {
            event.rerouteTo(PageNotFoundView.class);
        }
    }

    private void translateUrls() {
        for (int i = 0; i < urls.length; i++) {
            if (!urls[i].startsWith("https://")) {
                urls[i] = "https://" + urls[i];
            }
            urls[i] = urls[i].replace("#", "/images/")
                    .replace("|", "/data/")
                    .replace("\\", "/original/")
                    .replace("<", "/_images/");
        }
    }

    private Component generateComboBoxAndLink() {
        HorizontalLayout content = new HorizontalLayout();
        content.setPadding(false);
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.add(generateUrlComboBox(), generateContentLink());
        return content;
    }

    private Component generateUrlComboBox() {
        urlSelect = new Select<>();
        urlSelect.setItemLabelGenerator((ItemLabelGenerator<String>) url -> getTranslation("report.combobox", indexOfUrl(url) + 1));
        urlSelect.setItems(urls);
        urlSelect.setValue(urls[0]);
        urlSelect.addValueChangeListener(e -> updateUrlSelection(e.getValue()));
        return urlSelect;
    }

    private Component generateContentLink() {
        link = new Anchor(urls[0], urls[0]);
        link.setTarget("_blank");
        link.addClassNames(Styles.VISIBLE_NOT_SMALL);
        return link;
    }

    private Component generateContentPreview() {
        previewDiv = new Div();
        previewDiv.setWidthFull();
        updateUrlSelection(urls[0]);
        return previewDiv;
    }

    private Component generateSeparator() {
        Hr hr = new Hr();
        hr.setWidthFull();
        hr.getStyle().set("margin-top", "20px")
                .set("margin-bottom", "-14px");
        return hr;
    }

    private Component generateBottomComponents() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setPadding(false);

        if (getSessionData().isLoggedIn()) {
            mainLayout.add(
                    generateTextFieldReason(),
                    generateSubmitButton()
            );
        } else {
            mainLayout.add(generateNotLoggedInComponents());
        }

        return mainLayout;
    }

    private Component generateTextFieldReason() {
        reason = new TextField();
        reason.setLabel(getTranslation("report.reason"));
        reason.setWidthFull();
        reason.setMaxWidth("400px");
        reason.setMaxLength(500);
        return reason;
    }

    private Component generateSubmitButton() {
        Button submitButton = new Button(getTranslation("report.yes"));
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClickShortcut(Key.ENTER);
        submitButton.addClickListener(e -> {
            if (!reason.getValue().replaceAll("\\s", "").isEmpty()) {
                reason.setInvalid(false);
                try {
                    JSONObject json = new JSONObject();
                    json.put("url", urlSelect.getValue());
                    json.put("text", reason.getValue());
                    json.put("ip_hash", String.valueOf(getSessionData().getDiscordUser().get().getId()).hashCode());
                    SendEvent.send(EventOut.REPORT, json).get(5, TimeUnit.SECONDS);

                    CustomNotification.showSuccess(getTranslation("report.success"));
                    UI.getCurrent().navigate(HomeView.class);
                } catch (InterruptedException | ExecutionException | TimeoutException | JSONException ex) {
                    LOGGER.error("Exception", ex);
                    CustomNotification.showError(getTranslation("error"));
                }
            } else {
                reason.setValue("");
                reason.setErrorMessage(getTranslation("report.noreason"));
                reason.setInvalid(true);
            }
        });

        return submitButton;
    }

    private Component generateNotLoggedInComponents() {
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setPadding(false);
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        mainLayout.getStyle().set("margin-top", "32px");

        Button loginButton = new Button(getTranslation("login"));
        Anchor loginAnchor = new Anchor(getSessionData().getLoginUrl(), loginButton);
        mainLayout.add(loginAnchor);

        Span loginText = new Span(getTranslation("report.notloggedin"));
        loginText.getStyle().set("color", "var(--lumo-error-text-color)");
        mainLayout.add(loginText);

        return mainLayout;
    }

    private void updateUrlSelection(String url) {
        link.setHref(url);
        link.setText(url);
        previewDiv.removeAll();
        ContentType contentType = ContentType.parseFromUrl(url);
        if (contentType != null) {
            if (contentType.isVideo()) {
                String videoHtml = String.format("<video style=\"max-width: 100%%; max-height: 500px;\" controls>\n<source src=\"%s\" type=video/%s>\n</video>", url, contentType.getExt());
                Html video = new Html(videoHtml);
                previewDiv.add(video);
            } else {
                Image image = new Image(url, "");
                image.setMaxWidth("100%");
                image.setMaxHeight("500px");
                previewDiv.add(image);
            }
        }
    }

    private int indexOfUrl(String url) {
        for (int i = 0; i < urls.length; i++) {
            if (url.equals(urls[i])) {
                return i;
            }
        }
        return -1;
    }

}
