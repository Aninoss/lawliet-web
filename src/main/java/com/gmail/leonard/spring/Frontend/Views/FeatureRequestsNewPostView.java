package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Backend.FeatureRequests.FRNewBean;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.Modules.FeatureRequests;
import com.gmail.leonard.spring.Frontend.Components.CustomNotification;
import com.gmail.leonard.spring.Frontend.Components.HtmlText;
import com.gmail.leonard.spring.Frontend.Components.PageHeader;
import com.gmail.leonard.spring.Frontend.Layouts.MainLayout;
import com.gmail.leonard.spring.Frontend.Layouts.PageLayout;
import com.gmail.leonard.spring.Frontend.Styles;
import com.gmail.leonard.spring.LoginAccess;
import com.gmail.leonard.spring.NoLiteAccess;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

@Route(value = "new", layout = MainLayout.class)
@RoutePrefix("featurerequests")
@NoLiteAccess
@LoginAccess
public class FeatureRequestsNewPostView extends PageLayout {

    private final static Logger LOGGER = LoggerFactory.getLogger(FeatureRequestsNewPostView.class);

    private final VerticalLayout mainContent = new VerticalLayout();
    private final FRNewBean newBean = new FRNewBean();
    private final Binder<FRNewBean> binder = new Binder<>(FRNewBean.class);

    public FeatureRequestsNewPostView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);

        add(new PageHeader(getTitleText(), getTranslation("fr.new.desc"), FeatureRequestsView.getRouteStatic(FeatureRequestsView.class)));

        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.getStyle().set("margin-top", "-16px");
        mainContent.setPadding(true);

        addTitle();
        addDescription();
        addHr();
        addButtons();

        add(mainContent);
    }

    private void addButtons() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setPadding(false);

        Button btSubmit = new Button(getTranslation("fr.new.submit"));
        btSubmit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btSubmit.addClickShortcut(Key.ENTER);
        btSubmit.addClickListener(event -> {
            try {
                binder.writeBean(newBean);
                onSubmit();
            } catch (ValidationException e) {
                //Ignore
            }
        });
        buttonLayout.add(btSubmit);

        Button btCancel = new Button(getTranslation("fr.new.cancel"));
        btCancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btSubmit.addClickShortcut(Key.ESCAPE);
        btCancel.addClickListener(event -> exit());
        buttonLayout.add(btCancel);

        mainContent.add(buttonLayout);
    }

    private void onSubmit() {
        if (getSessionData().isLoggedIn()) {
            long userId = getSessionData().getDiscordUser().get().getId();
            if (FeatureRequests.canPost(userId).join()) {
                try {
                    FeatureRequests.postNewFeatureRequest(userId, newBean.getTitle(newBean), newBean.getDescription(newBean)).get();
                    CustomNotification.showSuccess(getTranslation("fr.new.success"));
                    exit();
                } catch (InterruptedException | ExecutionException e) {
                    CustomNotification.showError(getTranslation("err.exception.des"));
                    LOGGER.error("Error on request submit", e);
                }
            } else {
                CustomNotification.showError(getTranslation("fr.post.block"));
            }
        } else {
            CustomNotification.showError(getTranslation("fr.boost.notloggedin"));
        }
    }

    private void exit() {
        UI.getCurrent().navigate(FeatureRequestsView.class);
    }

    private void addHr() {
        mainContent.add(new Hr());
    }

    private void addDescription() {
        final int maxChar = 500;

        TextArea txDesc = new TextArea(getTranslation("fr.new.description", maxChar));
        txDesc.setHeight("200px");
        txDesc.setWidthFull();

        binder.forField(txDesc)
                .withValidator(
                        s -> s.length() <= maxChar,
                        getTranslation("textfield.toolong", maxChar)
                )
                .withValidator(
                        s -> s.length() > 0,
                        getTranslation("fr.new.description.missing")
                )
                .bind(newBean::getDescription, newBean::setDescription);

        mainContent.add(txDesc);
    }

    private void addTitle() {
        final int maxChar = 100;

        TextField txTitle = new TextField(getTranslation("fr.new.title", maxChar));
        txTitle.setWidthFull();

        binder.forField(txTitle)
                .withValidator(
                        s -> s.length() <= maxChar,
                        getTranslation("textfield.toolong", maxChar)
                )
                .bind(newBean::getTitle, newBean::setTitle);

        mainContent.add(txTitle);
    }

}
