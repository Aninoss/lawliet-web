package xyz.lawlietbot.spring.backend.dashboard;

import java.util.List;

public class DashboardInitData {

    private final List<Category> categories;
    private boolean premiumUnlocked;

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

        public Category(String id, String title) {
            this.id = id;
            this.title = title;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

    }

}
