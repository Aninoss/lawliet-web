package xyz.lawlietbot.spring.backend.dashboard;

import java.util.List;
import dashboard.container.DashboardContainer;

public class DashboardCategoryInitData {

    private final List<String> missingBotPermissions;
    private final List<String> missingUserPermissions;
    private final DashboardContainer components;

    public DashboardCategoryInitData(List<String> missingBotPermissions, List<String> missingUserPermissions, DashboardContainer components) {
        this.missingBotPermissions = missingBotPermissions;
        this.missingUserPermissions = missingUserPermissions;
        this.components = components;
    }

    public List<String> getMissingBotPermissions() {
        return missingBotPermissions;
    }

    public List<String> getMissingUserPermissions() {
        return missingUserPermissions;
    }

    public DashboardContainer getComponents() {
        return components;
    }

}
