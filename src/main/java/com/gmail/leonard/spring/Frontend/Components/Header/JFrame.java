package com.gmail.leonard.spring.Frontend.Components.Header;

import com.vaadin.flow.component.html.Div;

public class JFrame extends Div {

    public JFrame(String url) {
        String html = " <iframe src=\"" + url + "\"></iframe> ";
        getElement().setProperty("innerHTML", html);
    }

}
