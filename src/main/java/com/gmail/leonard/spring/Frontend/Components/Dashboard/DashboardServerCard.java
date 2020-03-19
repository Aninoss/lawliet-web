package com.gmail.leonard.spring.Frontend.Components.Dashboard;

import com.github.appreciated.card.Card;
import com.gmail.leonard.spring.Frontend.ComponentChanger;
import com.gmail.leonard.spring.Frontend.Views.HomeView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;

import java.util.Objects;
import java.util.stream.Stream;

public class DashboardServerCard extends Card {

    public DashboardServerCard(String iconURL, String name) {
        setHeightFull();

        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(Alignment.CENTER);

        Image image = new Image(iconURL, "");
        image.setWidth("70%");
        image.addClassName("round");

        Span titleLabel = new Span(name);
        titleLabel.getElement().getStyle()
                .set("width", "100%")
                .set("text-align", "center")
                .set("font-weight", "bold")
                .set("overflow", "hidden")
                .set("white-space", "nowrap");

        if (name.length() < 14) titleLabel.getElement().getStyle().set("font-size", "100%");
        else if (name.length() < 18) titleLabel.getElement().getStyle().set("font-size", "90%");
        else titleLabel.getElement().getStyle().set("font-size", "80%");

        content.add(image, titleLabel);
        content.setHeightFull();
        content.setFlexGrow(1, titleLabel);

        add(content);
    }

}