package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Backend.FeatureRequests.FRDynamicBean;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.Modules.FeatureRequests;
import com.gmail.leonard.spring.Backend.WebCommunicationClient.WebComClient;
import com.gmail.leonard.spring.Frontend.Components.FeatureRequests.FeatureRequestEntries;
import com.gmail.leonard.spring.Frontend.Components.FeatureRequests.FeatureRequestUserHeader;
import com.gmail.leonard.spring.Frontend.Components.HtmlText;
import com.gmail.leonard.spring.Frontend.Components.PageHeader;
import com.gmail.leonard.spring.Frontend.Layouts.MainLayout;
import com.gmail.leonard.spring.Frontend.Layouts.PageLayout;
import com.gmail.leonard.spring.Frontend.Styles;
import com.gmail.leonard.spring.LoginAccess;
import com.gmail.leonard.spring.NoLiteAccess;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

@Route(value = "featurerequests", layout = MainLayout.class)
@CssImport("./styles/featurerequests.css")
public class FeatureRequestsView extends PageLayout {

    public FeatureRequestsView(@Autowired SessionData sessionData, @Autowired UIData uiData) throws ExecutionException, InterruptedException {
        super(sessionData, uiData);
        FRDynamicBean frDynamicBean = FeatureRequests.fetchFeatureRequestMainData(sessionData).get();

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.setPadding(true);
        mainContent.add(new FeatureRequestEntries(getSessionData(), getUiData(), frDynamicBean));

        HtmlText htmlText = new HtmlText(getTranslation("fr.desc"));
        add(
                new PageHeader(getTitleText(), getTranslation("fr.desc"), getRoute(), uiData.isLite() ? null : new FeatureRequestUserHeader(getSessionData(), frDynamicBean)),
                mainContent
        );
    }

}
