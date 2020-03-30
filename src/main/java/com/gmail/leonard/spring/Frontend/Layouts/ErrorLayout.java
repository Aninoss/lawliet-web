package com.gmail.leonard.spring.Frontend.Layouts;

import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.*;
import com.gmail.leonard.spring.Frontend.Styles;
import com.gmail.leonard.spring.Frontend.Views.HomeView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.cookieconsent.CookieConsent;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.ParentLayout;

@ParentLayout(MainLayout.class)
public class ErrorLayout extends PageLayout {

    public ErrorLayout(SessionData sessionData, UIData uiData, String typeString) {
        super(sessionData, uiData);

        setWidthFull();
        VerticalLayout mainContent = new VerticalLayout();

        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.setPadding(true);

        add(new PageHeader(getTranslation("err." + typeString + ".title")));
        mainContent.add(new Paragraph(getTranslation("err." + typeString + ".des")));

        Button button = new Button(getTranslation("err.button.home"), new Icon(VaadinIcon.ARROW_LEFT));
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(click -> button.getUI().ifPresent(ui ->
                ui.navigate(HomeView.class))
        );

        mainContent.add(button);
        add(mainContent);
    }

}
