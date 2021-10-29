package xyz.lawlietbot.spring.frontend.views;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RoutePrefix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.LoginAccess;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.featurerequests.FRNewBean;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.syncserver.SendEvent;

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
        getStyle().set("margin-bottom", "48px");

        UnorderedList ul = new UnorderedList();
        ul.getStyle().set("margin-bottom", "-6px");
        Arrays.stream(getTranslation("fr.new.desc").split("\n"))
                .forEach(value -> ul.add(new ListItem(value )));
        add(new PageHeader(getUiData(), getTitleText(), null, null, ul));

        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.getStyle()
                .set("margin-top", "-20px")
                .set("margin-bottom", "-4px");
        mainContent.setPadding(true);

        addTitle();
        addDescription();
        //addNotifyCheckbox();
        addHr();
        addButtons();

        add(mainContent);
    }

    private void addButtons() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setPadding(false);

        Button btSubmit = new Button(getTranslation("fr.new.submit"));
        btSubmit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
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
            if (SendEvent.sendRequestCanPost(userId).join()) {
                try {
                    SendEvent.sendNewFeatureRequest(userId, newBean.getTitle(newBean), newBean.getDescription(newBean), newBean.getNotify(newBean)).get();
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

    private void addNotifyCheckbox() {
        Checkbox checkbox = new Checkbox(getTranslation("fr.new.notify"));
        checkbox.setValue(true);

        binder.forField(checkbox)
                .bind(newBean::getNotify, newBean::setNotify);

        mainContent.add(checkbox);
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
