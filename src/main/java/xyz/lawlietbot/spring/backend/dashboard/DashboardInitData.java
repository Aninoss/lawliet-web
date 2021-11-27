package xyz.lawlietbot.spring.backend.dashboard;

import java.util.List;

public class DashboardInitData {

    private final List<Category> categories;

    public DashboardInitData(List<Category> categories) {
        this.categories = categories;
    }

    public List<Category> getCategories() {
        return categories;
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
