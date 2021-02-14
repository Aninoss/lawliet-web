package xyz.lawlietbot.spring.frontend.layouts;

import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.views.HomeView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import xyz.lawlietbot.spring.frontend.components.PageHeader;

public class ErrorLayout extends PageLayout {

    public ErrorLayout(SessionData sessionData, UIData uiData, String typeString) {
        super(sessionData, uiData);
        getStyle().set("margin-bottom", "48px");
        VerticalLayout mainContent = new VerticalLayout();

        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.setPadding(true);

        add(new PageHeader(getUiData(), getTranslation("err." + typeString + ".title"), getTranslation("err." + typeString + ".des"), "error"));

        Button button = new Button(getTranslation("err.button.home"), new Icon(VaadinIcon.ARROW_LEFT));
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(click -> button.getUI().ifPresent(ui ->
                ui.navigate(HomeView.class))
        );

        mainContent.add(button);
        add(mainContent);
    }

}
