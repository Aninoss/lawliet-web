package com.gmail.leonard.spring.Frontend.Components;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class IconLabel extends HorizontalLayout {

    private Label label;

    public IconLabel(Icon icon, String string) {
        setSpacing(false);
        setAlignItems(FlexComponent.Alignment.CENTER);

        icon.setSize("18px");
        icon.getStyle().set("margin-right", "6px");

        add(icon, createLabel(string));
    }

    public void setText(String string) {
        remove(label);
        add(createLabel(string));
    }

    private Label createLabel(String string) {
        label = new Label(string);
        label.getStyle().set("font-size", "80%");
        return label;
    }

}