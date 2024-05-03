package xyz.lawlietbot.spring.backend.dashboard;

import java.util.List;

public class DashboardInitData {

    private final List<Category> categories;
    private final boolean premiumUnlocked;

    public DashboardInitData(List<Category> categories, boolean premiumUnlocked) {
        this.categories = categories;
        this.premiumUnlocked = premiumUnlocked;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public boolean isPremiumUnlocked() {
        return premiumUnlocked;
    }

    public static class Category {

        private final String id;
        private final String title;
        private final String description;

        public Category(String id, String title, String description) {
            this.id = id;
            this.title = title;
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }

}
