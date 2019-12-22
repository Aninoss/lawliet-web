package com.gmail.leonard.spring.Frontend;

import com.vaadin.flow.component.HtmlComponent;

public class ComponentChanger {

    public static void setNotInteractive(HtmlComponent component) {
        component.addClassName("unselectable");
        component.getElement()
                .setAttribute("ondragstart", "return false;")
                .setAttribute("ondrop", "return false;")
                .setAttribute("onmousedown", "return false;");
    }

}
