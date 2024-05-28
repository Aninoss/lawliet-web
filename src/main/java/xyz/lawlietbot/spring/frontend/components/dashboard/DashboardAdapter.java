package xyz.lawlietbot.spring.frontend.components.dashboard;

import dashboard.DashboardComponent;
import org.json.JSONObject;

public interface DashboardAdapter<T extends DashboardComponent> {

    void update(T newComponen);

    boolean equalsType(DashboardComponent dashboardComponent);

    default boolean dashboardComponentsAreEqual(DashboardComponent one, DashboardComponent two) {
        if (one == null) {
            return false;
        }

        JSONObject jsonOne = one.toJSON();
        jsonOne.remove("id");

        JSONObject jsonTwo = two.toJSON();
        jsonTwo.remove("id");

        return jsonOne.toString().equals(jsonTwo.toString());
    }

}
