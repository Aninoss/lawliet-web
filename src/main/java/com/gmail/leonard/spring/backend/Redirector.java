package com.gmail.leonard.spring.backend;

import com.vaadin.flow.component.UI;

public class Redirector {

    public void redirect(String url) {
        UI.getCurrent().getPage().executeJs(String.format("window.location.replace(\"%s\");", url));
    }

}