package xyz.lawlietbot.spring.frontend.views;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.featurerequests.FRDynamicBean;
import xyz.lawlietbot.spring.backend.featurerequests.FRPanelType;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.backend.util.StringUtil;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.components.featurerequests.FeatureRequestMain;
import xyz.lawlietbot.spring.frontend.components.featurerequests.FeatureRequestUserHeader;
import xyz.lawlietbot.spring.frontend.components.featurerequests.sort.*;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

import java.time.LocalDate;
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

        JSONObject jsonObject = new JSONObject();
        if (sessionData.isLoggedIn()) {
            jsonObject.put("user_id", sessionData.getDiscordUser().get().getId());
        }

        JSONObject responseJson = SendEvent.send(EventOut.FR_FETCH, jsonObject).get();
        int boostsTotal = responseJson.getInt("boosts_total");
        int boostsRemaining = responseJson.getInt("boosts_remaining");
        int completed = responseJson.has("completed") ? responseJson.getInt("completed") : 0;

        frDynamicBean = new FRDynamicBean(boostsRemaining, boostsTotal);
        JSONArray jsonEntriesArray = responseJson.getJSONArray("data");
        for (int j = 0; j < jsonEntriesArray.length(); j++) {
            JSONObject jsonEntry = jsonEntriesArray.getJSONObject(j);
            FRPanelType type = FRPanelType.valueOf(jsonEntry.getString("type"));
            boolean pub = jsonEntry.getBoolean("public");
            frDynamicBean.addEntry(
                    jsonEntry.getInt("id"),
                    jsonEntry.getString("title"),
                    jsonEntry.getString("description"),
                    type == FRPanelType.PENDING && pub ? jsonEntry.getInt("boosts") : null,
                    type == FRPanelType.PENDING && pub ? jsonEntry.getInt("recent_boosts") : null,
                    pub,
                    type,
                    LocalDate.ofEpochDay(jsonEntry.getLong("date"))
            );
        }

        mainContent.setWidthFull();
        mainContent.setPadding(false);

        add(
                new PageHeader(getUiData(), getTitleText(), getTranslation("fr.desc", StringUtil.numToString(completed)), uiData.isLite() ? null : new FeatureRequestUserHeader(getSessionData(), frDynamicBean)),
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
        String search = extractSearch(parametersMap);

        if (featureRequestMain != null) mainContent.remove(featureRequestMain);
        featureRequestMain = new FeatureRequestMain(getSessionData(), getUiData(), frDynamicBean, comparators, page,
                sort, search
        );
        mainContent.add(featureRequestMain);
    }

    private FeatureRequestSort extractSort(Map<String, List<String>> parametersMap, HashMap<String, FeatureRequestSort> featureRequestsMap) {
        if (parametersMap.containsKey("sortby") && parametersMap.get("sortby").size() > 0) {
            String sortby = parametersMap.get("sortby").get(0);
            if (featureRequestsMap.containsKey(sortby)) {
                return featureRequestsMap.get(sortby);
            }
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

    private String extractSearch(Map<String, List<String>> parametersMap) {
        if (parametersMap.containsKey("search") && parametersMap.get("search").size() > 0) {
            return parametersMap.get("search").get(0);
        }

        return "";
    }

}
