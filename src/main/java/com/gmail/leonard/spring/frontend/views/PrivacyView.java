package com.gmail.leonard.spring.frontend.views;

import com.gmail.leonard.spring.backend.FileString;
import com.gmail.leonard.spring.backend.userdata.SessionData;
import com.gmail.leonard.spring.backend.userdata.UIData;
import com.gmail.leonard.spring.frontend.components.PageHeader;
import com.gmail.leonard.spring.frontend.layouts.MainLayout;
import com.gmail.leonard.spring.frontend.layouts.PageLayout;
import com.gmail.leonard.spring.frontend.Styles;
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
        getStyle().set("margin-bottom", "48px");
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
