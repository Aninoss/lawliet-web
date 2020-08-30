package com.gmail.leonard.spring.Frontend.Components;

import com.vaadin.flow.component.html.Div;

public class LoadingIndicator extends Div {

    public LoadingIndicator() {
        super(new Div(), new Div(), new Div(), new Div());
        addClassName("lds-ring2");
    }

}
