package com.gmail.leonard.spring.Frontend.Views;

import com.github.appreciated.card.Card;
import com.gmail.leonard.spring.Backend.FAQ.FAQListContainer;
import com.gmail.leonard.spring.Backend.FAQ.FAQListSlot;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.FeatureRequests.FeatureRequestUserHeader;
import com.gmail.leonard.spring.Frontend.Components.HtmlText;
import com.gmail.leonard.spring.Frontend.Components.PageHeader;
import com.gmail.leonard.spring.Frontend.Layouts.MainLayout;
import com.gmail.leonard.spring.Frontend.Layouts.PageLayout;
import com.gmail.leonard.spring.Frontend.Styles;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "featurerequests", layout = MainLayout.class)
@CssImport("./styles/featurerequests.css")
public class FeatureRequestsView extends PageLayout {

    private final VerticalLayout mainContent = new VerticalLayout();

    public FeatureRequestsView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);


        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.setPadding(true);

        addContent();

        HtmlText htmlText = new HtmlText(getTranslation("fr.desc"));
        add(new PageHeader(getTitleText(), htmlText), mainContent);
    }

    private void addContent() {
        mainContent.add(new FeatureRequestUserHeader(getSessionData()));
    }

}
