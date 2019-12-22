package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Frontend.Layouts.ErrorLayout;
import com.gmail.leonard.spring.Frontend.Layouts.MainLayout;
import com.vaadin.flow.router.Route;

@Route(value = "notfound", layout = MainLayout.class)
public class PageNotFoundView extends ErrorLayout {

    public PageNotFoundView() {
        super("404");
    }

}