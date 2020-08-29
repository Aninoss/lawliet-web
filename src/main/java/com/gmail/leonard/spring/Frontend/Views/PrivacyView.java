package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Backend.FileString;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.PageHeader;
import com.gmail.leonard.spring.Frontend.Layouts.MainLayout;
import com.gmail.leonard.spring.Frontend.Layouts.PageLayout;
import com.gmail.leonard.spring.Frontend.Styles;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;

@Route(value = "privacy", layout = MainLayout.class)
public class PrivacyView extends PageLayout {

    public PrivacyView(@Autowired SessionData sessionData, @Autowired UIData uiData) throws IOException {
        super(sessionData, uiData);
        String name = String.format("privacy_%s.html", getLocale().getLanguage());
        String pageString = new FileString(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(name)
        ).toString();

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setSpacing(false);
        mainContent.addClassName(Styles.APP_WIDTH);

        Div div = new Div();
        div.addClassName(Styles.APP_WIDTH);
        div.add(new Html(pageString));

        mainContent.add(div);
        add(new PageHeader(getTitleText(), null, null), mainContent);
    }

}
