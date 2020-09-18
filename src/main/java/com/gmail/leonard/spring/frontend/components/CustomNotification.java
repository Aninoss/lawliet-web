package com.gmail.leonard.spring.frontend.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;

public class CustomNotification {

    public static void showSuccess(String text) {
        Div div = new Div();
        div.setText(text);
        div.getStyle().set("color", "var(--lumo-success-text-color)");
        show(div);
    }

    public static void showError(String text) {
        Div div = new Div();
        div.setText(text);
        div.getStyle().set("color", "var(--lumo-error-text-color)");
        show(div);
    }

    private static void show(Div div) {
        Notification notification = new Notification(div);
        notification.setDuration(5000);
        notification.open();
    }

}
