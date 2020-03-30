package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Backend.Feedback.FeedbackBean;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.WebComClient;
import com.gmail.leonard.spring.Frontend.Components.CustomNotification;
import com.gmail.leonard.spring.Frontend.Components.HtmlText;
import com.gmail.leonard.spring.Frontend.Components.PageHeader;
import com.gmail.leonard.spring.Frontend.Layouts.MainLayout;
import com.gmail.leonard.spring.Frontend.Layouts.PageLayout;
import com.gmail.leonard.spring.Frontend.Styles;
import com.gmail.leonard.spring.NoLiteAccess;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

@Route(value = "feedback", layout = MainLayout.class)
@NoLiteAccess
public class FeedbackView extends PageLayout {

    public FeedbackView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);

        HtmlText htmlText = new HtmlText(getTranslation("feedback.desc"));
        add(new PageHeader(getTitleText(), htmlText));

        FeedbackBean feedbackBean = new FeedbackBean();
        Binder<FeedbackBean> binder = new Binder<>(FeedbackBean.class);

        setWidthFull();
        VerticalLayout mainContent = new VerticalLayout();

        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.setPadding(true);

        String[] options = getTranslation("feedback.options").split("\n");


        /* Cause */
        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
        radioGroup.setLabel(getTranslation("feedback.radiolabel"));
        radioGroup.setItems(options);
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.setValue(options[0]);

        binder.forField(radioGroup)
                .bind(feedbackBean::getCause, feedbackBean::setCause);


        /* Reason */
        TextArea textArea = new TextArea(getTranslation("feedback.textarea"));
        textArea.setWidthFull();
        textArea.setHeight("200px");

        binder.forField(textArea)
                .withValidator(
                        reason -> reason.length() > 0 || !radioGroup.getValue().equals(options[options.length - 1]),
                        getTranslation("feedback.textarea.invalid")
                )
                .bind(feedbackBean::getReason, feedbackBean::setReason);


        /* Contact */
        Checkbox checkbox = new Checkbox(getTranslation("feedback.username"));
        checkbox.setWidthFull();

        binder.forField(checkbox)
                .bind(feedbackBean::getContact, feedbackBean::setContact);


        /* Username */
        TextField textField = new TextField();
        textField.setPlaceholder(getTranslation("feedback.username.pre"));
        if (sessionData.isLoggedIn())
            textField.setValue(sessionData.getUserName().get() + "#" + sessionData.getDiscriminator().get());
        textField.setWidth("300px");
        textField.getStyle().set("margin-top", "0");
        textField.setEnabled(false);

        binder.forField(textField)
                .withValidator(
                        username -> !checkbox.getValue() || username.matches(".+#\\d{4}"),
                        getTranslation("feedback.username.invalid")
                )
                .bind(feedbackBean::getUsernameDiscriminated, feedbackBean::setUsernameDiscriminated);
        checkbox.addValueChangeListener(event -> textField.setEnabled(event.getValue()));


        /* Submit */
        Button submit = new Button(getTranslation("feedback.button"));
        submit.addThemeName(ButtonVariant.LUMO_PRIMARY.getVariantName());
        submit.addClickListener(event -> {
            try {
                binder.writeBean(feedbackBean);
                onSubmitPress(mainContent, feedbackBean);
            } catch (ValidationException e) {
                //Ignore
            }
        });

        mainContent.add(radioGroup, textArea, checkbox, textField, new Hr(), submit);
        add(mainContent);
    }

    private void onSubmitPress(VerticalLayout mainContent, FeedbackBean feedbackBean) {
        mainContent.setEnabled(false);
        try {
            WebComClient.getInstance().sendFeedback(feedbackBean).get();
            CustomNotification.showSuccess(getTranslation("feedback.confirm"));
            mainContent.getUI().get().navigate(HomeView.class);
        } catch (InterruptedException | ExecutionException e) {
            CustomNotification.showError(getTranslation("feedback.error"));
            e.printStackTrace();
            mainContent.setEnabled(true);
        }
    }

}
