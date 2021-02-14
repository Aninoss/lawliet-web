package xyz.lawlietbot.spring.frontend.components;

import com.vaadin.flow.component.html.Div;
import xyz.lawlietbot.spring.backend.userdata.UIData;

public class HeaderDummy extends Div {

    public HeaderDummy(UIData uiData) {
        setWidthFull();
        setId("header-dummy");
        if (uiData.isLite())
            addClassName("lite");
    }

}
