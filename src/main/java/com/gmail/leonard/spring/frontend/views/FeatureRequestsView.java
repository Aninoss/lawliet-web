package com.gmail.leonard.spring.frontend.views;

import com.gmail.leonard.spring.backend.featurerequests.FRDynamicBean;
import com.gmail.leonard.spring.backend.userdata.SessionData;
import com.gmail.leonard.spring.backend.userdata.UIData;
import com.gmail.leonard.spring.frontend.components.featurerequests.FeatureRequestMain;
import com.gmail.leonard.spring.frontend.components.featurerequests.FeatureRequestUserHeader;
import com.gmail.leonard.spring.frontend.components.PageHeader;
import com.gmail.leonard.spring.frontend.components.featurerequests.sort.*;
import com.gmail.leonard.spring.frontend.layouts.MainLayout;
import com.gmail.leonard.spring.frontend.layouts.PageLayout;
import com.gmail.leonard.spring.NoLiteAccess;
import com.gmail.leonard.spring.syncserver.SendEvent;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Route(value = "featurerequests", layout = MainLayout.class)
@CssImport("./styles/featurerequests.css")
@NoLiteAccess
public class FeatureRequestsView extends PageLayout implements HasUrlParameter<String> {

    private final VerticalLayout mainContent = new VerticalLayout();
    private final FRDynamicBean frDynamicBean;
    private FeatureRequestMain featureRequestMain = null;

    private final FeatureRequestSort[] comparators = new FeatureRequestSort[] {
            new FeatureRequestSortByPopular(),
            new FeatureRequestSortByBoosts(),
            new FeatureRequestSortByNewest(),
            new FeatureRequestSortByTitle()
    };

    public FeatureRequestsView(@Autowired SessionData sessionData, @Autowired UIData uiData) throws ExecutionException, InterruptedException {
        super(sessionData, uiData);
        getStyle().set("margin-bottom", "48px");

        frDynamicBean = SendEvent.sendRequestFeatureRequestMainData(sessionData).get();
        mainContent.setWidthFull();
        mainContent.setPadding(true);

        add(
                new PageHeader(getTitleText(), getTranslation("fr.desc"), null, uiData.isLite() ? null : new FeatureRequestUserHeader(getSessionData(), frDynamicBean)),
                mainContent
        );
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location
                .getQueryParameters();

        Map<String, List<String>> parametersMap =
                queryParameters.getParameters();

        int page = extractPage(parametersMap);
        FeatureRequestSort sort = extractSort(parametersMap, getFeatureRequestsMap());

        if (featureRequestMain != null) mainContent.remove(featureRequestMain);
        featureRequestMain = new FeatureRequestMain(getSessionData(), getUiData(), frDynamicBean, comparators, page, sort);
        mainContent.add(featureRequestMain);
    }

    private FeatureRequestSort extractSort(Map<String, List<String>> parametersMap, HashMap<String, FeatureRequestSort> featureRequestsMap) {
        if (parametersMap.containsKey("sortby") && parametersMap.get("sortby").size() > 0) {
            String sortby = parametersMap.get("sortby").get(0);
            if (featureRequestsMap.containsKey(sortby))
                return featureRequestsMap.get(sortby);
        }

        return featureRequestsMap.get("popular");
    }

    private HashMap<String, FeatureRequestSort> getFeatureRequestsMap() {
        HashMap<String, FeatureRequestSort> map = new HashMap<>();
        Arrays.stream(comparators).forEach(sort -> map.put(sort.getId(), sort));
        return map;
    }

    private int extractPage(Map<String, List<String>> parametersMap) {
        if (parametersMap.containsKey("page") && parametersMap.get("page").size() > 0) {
            try {
                return Integer.parseInt(parametersMap.get("page").get(0)) - 1;
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        return 0;
    }

}
