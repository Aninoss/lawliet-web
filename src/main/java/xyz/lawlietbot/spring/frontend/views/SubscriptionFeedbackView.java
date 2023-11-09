package xyz.lawlietbot.spring.frontend.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.payment.WebhookNotifier;
import xyz.lawlietbot.spring.backend.subscriptionfeedback.SubscriptionFeedbackIdManager;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;

import java.util.List;
import java.util.Map;

@Route(value = "subscriptionfeedback", layout = MainLayout.class)
@NoLiteAccess
public class SubscriptionFeedbackView extends PageLayout implements HasUrlParameter<String> {

    private String id = "";

    public SubscriptionFeedbackView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.setPadding(true);
        mainContent.add(generateForm());

        add(
                new PageHeader(getUiData(), getTitleText(), getTranslation("subfeedback.desc")),
                mainContent
        );
    }

    private Component generateForm() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        Label label = new Label(getTranslation("subfeedback.label"));
        layout.add(label);

        TextArea textArea = new TextArea();
        textArea.setMaxLength(1024);
        textArea.setWidthFull();
        textArea.getStyle().set("margin-top", "0");
        layout.add(textArea);

        HorizontalLayout buttonLayout = new HorizontalLayout();

        Button sendButton = new Button(getTranslation("subfeedback.send"));
        sendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        sendButton.addClickListener(e -> {
            if (SubscriptionFeedbackIdManager.checkId(id)) {
                WebhookNotifier.newSubFeedback(textArea.getValue());
                CustomNotification.showSuccess(getTranslation("subfeedback.confirm"));
            } else {
                CustomNotification.showError(getTranslation("subfeedback.used"));
            }
            navigateBack();
        });
        buttonLayout.add(sendButton);

        Button cancelButton = new Button(getTranslation("subfeedback.cancel"));
        cancelButton.addClickListener(e -> navigateBack());
        buttonLayout.add(cancelButton);

        layout.add(buttonLayout);
        return layout;
    }

    private void navigateBack() {
        QueryParameters queryParameters = new QueryParameters(Map.of("tab", List.of("2")));
        UI.getCurrent().navigate("/premium", queryParameters);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location
                .getQueryParameters();

        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        if (parametersMap.containsKey("id") && parametersMap.get("id").size() == 1) {
            this.id = parametersMap.get("id").get(0);
        }
    }

}