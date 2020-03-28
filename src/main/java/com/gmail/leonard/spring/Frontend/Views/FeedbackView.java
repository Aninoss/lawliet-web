package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Backend.FAQ.FAQListContainer;
import com.gmail.leonard.spring.Backend.FAQ.FAQListSlot;
import com.gmail.leonard.spring.Backend.StringTools;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.WebComClient;
import com.gmail.leonard.spring.Frontend.Components.CustomNotification;
import com.gmail.leonard.spring.Frontend.Components.HtmlText;
import com.gmail.leonard.spring.Frontend.Layouts.MainLayout;
import com.gmail.leonard.spring.Frontend.Layouts.PageLayout;
import com.gmail.leonard.spring.Frontend.Styles;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "feedback", layout = MainLayout.class)
public class FeedbackView extends PageLayout {

    public FeedbackView() {
        setWidthFull();
        VerticalLayout mainContent = new VerticalLayout();

        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.setPadding(true);

        H1 title = new H1(getTitleText());
        title.setWidthFull();
        mainContent.add(title, new HtmlText(getTranslation("feedback.desc")));

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
        WebComClient.getInstance().sendFeedback(reason, explanation);
        CustomNotification.showSuccess(getTranslation("feedback.confirm"));
        mainContent.getUI().get().navigate(HomeView.class);
    }

}
