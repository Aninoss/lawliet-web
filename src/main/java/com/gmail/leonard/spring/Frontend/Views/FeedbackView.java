package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Backend.FAQ.FAQListContainer;
import com.gmail.leonard.spring.Backend.FAQ.FAQListSlot;
import com.gmail.leonard.spring.Backend.StringTools;
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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextArea;
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

        setWidthFull();
        VerticalLayout mainContent = new VerticalLayout();

        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.setPadding(true);

        String[] options = getTranslation("feedback.options").split("\n");

        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
        radioGroup.setLabel(getTranslation("feedback.radiolabel"));
        radioGroup.setItems(options);
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.setValue(options[0]);
        mainContent.add(radioGroup);

        TextArea textArea = new TextArea(getTranslation("feedback.textarea"));
        textArea.setWidthFull();
        textArea.setHeight("200px");
        mainContent.add(textArea);

        Button submit = new Button(getTranslation("feedback.button"));
        submit.addThemeName(ButtonVariant.LUMO_PRIMARY.getVariantName());
        submit.addClickListener(event -> onSubmitPress(mainContent, radioGroup.getValue(), textArea.getValue()));
        mainContent.add(submit);

        add(mainContent);
    }

    private void onSubmitPress(VerticalLayout mainContent, String reason, String explanation) {
        mainContent.setEnabled(false);
        try {
            WebComClient.getInstance().sendFeedback(reason, explanation).get();
            CustomNotification.showSuccess(getTranslation("feedback.confirm"));
            mainContent.getUI().get().navigate(HomeView.class);
        } catch (InterruptedException | ExecutionException e) {
            CustomNotification.showError(getTranslation("feedback.error"));
            e.printStackTrace();
        }
    }

}
