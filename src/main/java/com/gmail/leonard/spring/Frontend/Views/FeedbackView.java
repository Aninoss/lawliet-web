package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Backend.Feedback.FeedbackBean;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.Modules.OneWayTransfers;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

@Route(value = "feedback", layout = MainLayout.class)
@NoLiteAccess
public class FeedbackView extends PageLayout implements HasUrlParameter<Long> {

    final static Logger LOGGER = LoggerFactory.getLogger(FeedbackView.class);

    private long serverId = 0;

    public FeedbackView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);

        add(new PageHeader(getTitleText(), getTranslation("feedback.desc"), getRoute()));

        FeedbackBean feedbackBean = new FeedbackBean();
        Binder<FeedbackBean> binder = new Binder<>(FeedbackBean.class);

        VerticalLayout mainContent = new VerticalLayout();

        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.getStyle().set("margin-top", "-16px");
        mainContent.setPadding(true);

        String[] options = getTranslation("feedback.options").split("\n");


        /* Cause */
        RadioButtonGroup<String> rbCause = new RadioButtonGroup<>();
        rbCause.setLabel(getTranslation("feedback.radiolabel"));
        rbCause.setItems(options);
        rbCause.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        rbCause.setValue(options[0]);

        binder.forField(rbCause)
                .bind(feedbackBean::getCause, feedbackBean::setCause);


        /* Reason */
        TextArea txReason = new TextArea(getTranslation("feedback.textarea"));
        txReason.setWidthFull();
        txReason.setHeight("200px");

        binder.forField(txReason)
                .withValidator(
                        reason -> reason.length() > 0 || !rbCause.getValue().equals(options[options.length - 1]),
                        getTranslation("feedback.textarea.invalid")
                )
                .bind(feedbackBean::getReason, feedbackBean::setReason);


        /* Send Server Details */
        Checkbox cbServerDetails = new Checkbox(getTranslation("feedback.serverDetails"), true);
        cbServerDetails.setWidthFull();

        binder.forField(cbServerDetails)
                .bind(feedbackBean::getServerDetails, feedbackBean::setServerDetails);


        /* Submit */
        Button btSubmit = new Button(getTranslation("feedback.button"));
        btSubmit.addThemeName(ButtonVariant.LUMO_PRIMARY.getVariantName());
        btSubmit.addClickListener(event -> {
            try {
                binder.writeBean(feedbackBean);
                onSubmitPress(mainContent, feedbackBean);
            } catch (ValidationException e) {
                //Ignore
            }
        });

        mainContent.add(rbCause, txReason, new Hr(), cbServerDetails, btSubmit);
        add(mainContent);
    }

    private void onSubmitPress(VerticalLayout mainContent, FeedbackBean feedbackBean) {
        mainContent.setEnabled(false);
        try {
            OneWayTransfers.sendFeedback(feedbackBean, feedbackBean.getServerDetails(feedbackBean) ? serverId : null).get();
            CustomNotification.showSuccess(getTranslation("feedback.confirm"));
            mainContent.getUI().get().navigate(HomeView.class);
        } catch (ExecutionException e) {
            CustomNotification.showError(getTranslation("err.exception.des"));
            LOGGER.error("Error while submitting feedback form", e);
            mainContent.setEnabled(true);
        } catch (InterruptedException e) {
            CustomNotification.showError(getTranslation("err.exception.des"));
            LOGGER.error("Interrupted");
            mainContent.setEnabled(true);
        }
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Long serverId) {
        this.serverId = serverId;
    }

}
