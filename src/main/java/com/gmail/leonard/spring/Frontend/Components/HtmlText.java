package com.gmail.leonard.spring.Frontend.Components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import org.apache.commons.lang3.StringEscapeUtils;

public class HtmlText extends Div {

    public HtmlText(String html) {
        getElement().setProperty("innerHTML", StringEscapeUtils.escapeHtml4(html).replace("\n", "<br>"));
    }

}
