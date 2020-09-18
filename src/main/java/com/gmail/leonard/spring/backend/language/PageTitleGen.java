package com.gmail.leonard.spring.backend.language;

import com.vaadin.flow.component.UI;

public class PageTitleGen {

    public static String getPageTitle(String id) { return UI.getCurrent().getTranslation("pagetitle", getTitle(id)); }
    public static String getTitle(String id) {
        return UI.getCurrent().getTranslation("category." + id);
    }

}