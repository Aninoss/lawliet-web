package com.gmail.leonard.spring.Frontend.Layouts;

import com.gmail.leonard.spring.Frontend.Components.*;
import com.gmail.leonard.spring.Frontend.Views.HomeView;
import com.vaadin.flow.component.cookieconsent.CookieConsent;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.ParentLayout;

@ParentLayout(MainLayout.class)
public class ErrorLayout extends PageLayout {

    public ErrorLayout(String typeString) {
        setWidthFull();
        VerticalLayout mainContent = new VerticalLayout();

        mainContent.addClassName("app-width");
        mainContent.setPadding(true);

        mainContent.add(new H1(getTranslation("err." + typeString + ".title")));
        mainContent.add(new Hr());
        mainContent.add(new Paragraph(getTranslation("err." + typeString + ".des")));

        CustomButton button = new CustomButton(getTranslation("err.button.home"), new Icon(VaadinIcon.ARROW_LEFT));
        button.addClickListener(click -> button.getUI().ifPresent(ui ->
                ui.navigate(HomeView.class))
        );

        mainContent.add(button);
        add(mainContent);
    }

}
