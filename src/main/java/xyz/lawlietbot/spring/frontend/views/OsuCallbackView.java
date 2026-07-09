package xyz.lawlietbot.spring.frontend.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.json.JSONObject;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

import java.util.List;
import java.util.Map;

@Route(value = "osu_callback", layout = MainLayout.class)
public class OsuCallbackView extends PageLayout implements BeforeEnterObserver {

    public OsuCallbackView(SessionData sessionData, UIData uiData) {
        super(sessionData, uiData);

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setSpacing(false);
        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.add(new Text(getTranslation("osu.success")));
        add(new PageHeader(getUiData(), getTitleText(), null), mainContent);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> parametersMap = queryParameters.getParameters();

        JSONObject responseJson = SendEvent.sendToAnyCluster(EventOut.OSU_CALLBACK, Map.of(
                "code", parametersMap.getOrDefault("code", List.of("")).getFirst(),
                "encrypted_user_id", parametersMap.getOrDefault("state", List.of("")).getFirst()
        )).join();

        if (!responseJson.getBoolean("success")) {
            throw new RuntimeException("Invalid osu callback response:\n" + responseJson);
        }
    }
}