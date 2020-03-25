package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Frontend.Layouts.ErrorLayout;
import com.gmail.leonard.spring.Frontend.Layouts.MainLayout;
import com.vaadin.flow.router.Route;

@Route(value = "exception", layout = MainLayout.class)
public class ExceptionView extends ErrorLayout {

    public ExceptionView() {
        super("exception");
    }

}