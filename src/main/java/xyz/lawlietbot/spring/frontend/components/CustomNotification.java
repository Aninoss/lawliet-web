package xyz.lawlietbot.spring.frontend.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class CustomNotification {

    public static void showSuccess(String text) {
        Div div = new Div();
        div.setText(text);
        show(div, NotificationVariant.LUMO_SUCCESS);
    }

    public static void showError(String text) {
        Div div = new Div();
        div.setText(text);
        show(div, NotificationVariant.LUMO_ERROR);
    }

    private static void show(Div div, NotificationVariant... notificationVariants) {
        Notification notification = new Notification(div);
        notification.setDuration(5000);
        notification.addThemeVariants(notificationVariants);
        notification.open();
    }

}
