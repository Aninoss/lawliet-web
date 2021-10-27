package xyz.lawlietbot.spring.backend;

import com.vaadin.flow.component.UI;

public class Redirector {

    public void redirect(String url) {
        UI.getCurrent().getPage().executeJs(String.format("window.location.href = '%s';", url));
    }

}