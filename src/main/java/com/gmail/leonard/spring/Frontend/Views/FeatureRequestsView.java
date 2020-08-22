package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Backend.FeatureRequests.FRDynamicBean;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.WebComClient;
import com.gmail.leonard.spring.Frontend.Components.FeatureRequests.FeatureRequestUserHeader;
import com.gmail.leonard.spring.Frontend.Components.HtmlText;
import com.gmail.leonard.spring.Frontend.Components.PageHeader;
import com.gmail.leonard.spring.Frontend.Layouts.MainLayout;
import com.gmail.leonard.spring.Frontend.Layouts.PageLayout;
import com.gmail.leonard.spring.Frontend.Styles;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

@Route(value = "featurerequests", layout = MainLayout.class)
@CssImport("./styles/featurerequests.css")
public class FeatureRequestsView extends PageLayout {

    private final VerticalLayout mainContent = new VerticalLayout();
    private final FRDynamicBean frDynamicBean;

    public FeatureRequestsView(@Autowired SessionData sessionData, @Autowired UIData uiData) throws ExecutionException, InterruptedException {
        super(sessionData, uiData);
        frDynamicBean = WebComClient.getInstance().getFRFetch(sessionData).get();

        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.setPadding(true);

        addContent();

        HtmlText htmlText = new HtmlText(getTranslation("fr.desc"));
        add(new PageHeader(getTitleText(), htmlText), mainContent);
    }

    private void addContent() {
        mainContent.add(new FeatureRequestUserHeader(getSessionData(), frDynamicBean));
    }

}
