package com.gmail.leonard.spring.Backend.Language;

import com.vaadin.flow.component.UI;

public class PageTitleFactory {

    public static String getPageTitle(String id) { return UI.getCurrent().getTranslation("pagetitle", getTitle(id)); }
    public static String getTitle(String id) {
        return UI.getCurrent().getTranslation("category." + id);
    }

}