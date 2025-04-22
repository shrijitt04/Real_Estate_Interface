package com.example.application.views;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;  
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;   

public class NotificationUtils {

    public static void showStyledNotification(String message, int durationMs) {
        Notification notification = new Notification();
        notification.setDuration(durationMs);
        notification.setPosition(Notification.Position.TOP_CENTER);

        Div content = new Div();
        content.setText(message);
        content.getStyle()
            .set("background-color", "#2C3E50")
            .set("color", "white")
            .set("padding", "10px")
            .set("border-radius", "8px")
            .set("font-weight", "bold");

        notification.add(content);
        notification.open();
    }
}
