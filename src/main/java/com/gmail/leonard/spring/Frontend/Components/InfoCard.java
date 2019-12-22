package com.gmail.leonard.spring.Frontend.Components;

import com.github.appreciated.card.Card;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.Objects;
import java.util.stream.Stream;

public class InfoCard extends Card {

    public InfoCard(String title, String subtitle, String desc, Icon icon, Component... components) {
        setHeightFull();
        //setBorderRadius("10px");

        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);

        Span titleLabel = new Span(title);
        titleLabel.getElement().getStyle()
                .set("font-size", "120%")
                .set("width", "100%")
                .set("text-align", "center")
                .set("margin-top", "6px")
                .set("font-weight", "bold");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setWidthFull();
        subtitleLabel.addClassName("center");
        subtitleLabel.getElement().getStyle()
                .set("font-size", "80%")
                .set("text-align", "center")
                .set("opacity", "0.5")
                .set("margin-top", "2px");

        Label descLabel = new Label(desc);
        descLabel.setWidthFull();
        descLabel.addClassName("center");

        Hr seperator = new Hr();
        seperator.getStyle()
                .set("margin-top", "12px")
                .set("margin-bottom", "-4px");

        content.add(icon, titleLabel, subtitleLabel, seperator, descLabel);
        Stream.of(components).filter(Objects::nonNull).forEach(content::add);
        content.setHeightFull();
        content.setFlexGrow(1, descLabel);

        add(content);
    }

}