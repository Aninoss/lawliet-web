package xyz.lawlietbot.spring.backend.dashboard;

import java.util.List;
import dashboard.container.DashboardContainer;

public class DashboardCategoryInitData {

    private final List<String> missingBotPermissions;
    private final List<String> missingUserPermissions;
    private final DashboardContainer components;
    private final boolean premiumUnlocked;

    public DashboardCategoryInitData(List<String> missingBotPermissions, List<String> missingUserPermissions, DashboardContainer components, boolean premiumUnlocked) {
        this.missingBotPermissions = missingBotPermissions;
        this.missingUserPermissions = missingUserPermissions;
        this.components = components;
        this.premiumUnlocked = premiumUnlocked;
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

    public boolean isPremiumUnlocked() {
        return premiumUnlocked;
    }

}
