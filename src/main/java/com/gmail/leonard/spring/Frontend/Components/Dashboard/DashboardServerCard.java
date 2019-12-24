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
                .set("font-size", "120%")
                .set("width", "100%")
                .set("text-align", "center")
                .set("margin-top", "6px")
                .set("font-weight", "bold");

        //ComponentChanger.setNotInteractive(image, titleLabel);
        content.add(image, titleLabel);
        content.setHeightFull();
        content.setFlexGrow(1, titleLabel);

        add(content);
    }

}