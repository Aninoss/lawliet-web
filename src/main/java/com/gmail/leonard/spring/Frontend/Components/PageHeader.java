package com.gmail.leonard.spring.Frontend.Components;

import com.gmail.leonard.spring.Frontend.Styles;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class PageHeader extends Div {

    private VerticalLayout mainLayout = new VerticalLayout();

    public PageHeader(Component... components) {
        this(null, components);
    }

    public PageHeader(String title, Component... components) {
        setWidthFull();
        setId("page-header");
        addClassName("only-pc");

        mainLayout.addClassName(Styles.APP_WIDTH);
        mainLayout.setPadding(true);

        mainLayout.add(new HeaderDummy());
        if (title != null) {
            H1 h1 = new H1(title);
            h1.setWidthFull();
            h1.getStyle().set("margin-bottom", "8px");
            mainLayout.add(h1);
        }

        mainLayout.add(components);
        add(mainLayout);
    }

    public VerticalLayout getMainLayout() {
        return mainLayout;
    }

    protected void removeOnlyPC() {
        removeClassName("only-pc");
    }

}
