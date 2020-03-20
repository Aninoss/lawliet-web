package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.Home.BotInfo.BotInfoLayout;
import com.gmail.leonard.spring.Frontend.Components.Home.BotPros.BotProsLayout;
import com.gmail.leonard.spring.Frontend.Layouts.MainLayout;
import com.gmail.leonard.spring.Frontend.Layouts.PageLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "", layout = MainLayout.class)
public class HomeView extends PageLayout {

    public HomeView(@Autowired UIData uiData) {
        setWidthFull();

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setPadding(false);
        verticalLayout.setSizeFull();
        verticalLayout.setSpacing(false);

        verticalLayout.add(new BotInfoLayout(uiData));
        verticalLayout.add(new BotProsLayout(uiData));

        add(verticalLayout);
    }

}
