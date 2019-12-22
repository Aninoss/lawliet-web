package com.gmail.leonard.spring.Frontend.Components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;

public class CustomButton extends Button {

    public CustomButton() {
        addClassName("pointer");
    }

    public CustomButton(String text) {
        super(text);
        addClassName("pointer");
    }

    public CustomButton(Component icon) {
        super(icon);
        addClassName("pointer");
    }

    public CustomButton(String text, Component icon) {
        super(text, icon);
        addClassName("pointer");
    }

    public CustomButton(String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, clickListener);
        addClassName("pointer");
    }

    public CustomButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(icon, clickListener);
        addClassName("pointer");
    }

    public CustomButton(String text, Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(text, icon, clickListener);
        addClassName("pointer");
    }
}
