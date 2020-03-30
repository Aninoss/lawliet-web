package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Layouts.ErrorLayout;
import com.gmail.leonard.spring.Frontend.Layouts.MainLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "exception", layout = MainLayout.class)
public class ExceptionView extends ErrorLayout {

    public ExceptionView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData, "exception");
    }

}