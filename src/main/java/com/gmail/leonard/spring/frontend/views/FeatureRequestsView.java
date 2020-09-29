package com.gmail.leonard.spring.frontend.views;

import com.gmail.leonard.spring.backend.featurerequests.FRDynamicBean;
import com.gmail.leonard.spring.backend.userdata.SessionData;
import com.gmail.leonard.spring.backend.userdata.UIData;
import com.gmail.leonard.spring.backend.webcomclient.modules.FeatureRequests;
import com.gmail.leonard.spring.frontend.components.featurerequests.FeatureRequestMain;
import com.gmail.leonard.spring.frontend.components.featurerequests.FeatureRequestUserHeader;
import com.gmail.leonard.spring.frontend.components.PageHeader;
import com.gmail.leonard.spring.frontend.layouts.MainLayout;
import com.gmail.leonard.spring.frontend.layouts.PageLayout;
import com.gmail.leonard.spring.NoLiteAccess;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

@Route(value = "featurerequests", layout = MainLayout.class)
@CssImport("./styles/featurerequests.css")
@NoLiteAccess
public class FeatureRequestsView extends PageLayout {

    public FeatureRequestsView(@Autowired SessionData sessionData, @Autowired UIData uiData) throws ExecutionException, InterruptedException {
        super(sessionData, uiData);
        getStyle().set("margin-bottom", "48px");
        FRDynamicBean frDynamicBean = FeatureRequests.fetchFeatureRequestMainData(sessionData).get();

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setWidthFull();
        mainContent.setPadding(true);
        mainContent.add(new FeatureRequestMain(getSessionData(), getUiData(), frDynamicBean));

        add(
                new PageHeader(getTitleText(), getTranslation("fr.desc"), null, uiData.isLite() ? null : new FeatureRequestUserHeader(getSessionData(), frDynamicBean)),
                mainContent
        );
    }

}
