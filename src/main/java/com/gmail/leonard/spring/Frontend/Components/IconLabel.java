package com.gmail.leonard.spring.Frontend.Components;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class IconLabel extends HorizontalLayout {

    public IconLabel(Icon icon, String string) {
        setSpacing(false);
        setAlignItems(FlexComponent.Alignment.CENTER);

        Label hideLabel = new Label(string);
        hideLabel.getStyle().set("font-size", "80%");

        icon.setSize("18px");
        icon.getStyle().set("margin-right", "6px");

        add(icon, hideLabel);
    }

}