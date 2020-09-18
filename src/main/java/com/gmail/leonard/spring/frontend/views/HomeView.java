package com.gmail.leonard.spring.frontend.views;

import com.gmail.leonard.spring.SetDivStretchBackground;
import com.gmail.leonard.spring.backend.userdata.SessionData;
import com.gmail.leonard.spring.backend.userdata.UIData;
import com.gmail.leonard.spring.frontend.components.home.botinfo.BotInfoLayout;
import com.gmail.leonard.spring.frontend.components.home.botpros.BotProsLayout;
import com.gmail.leonard.spring.frontend.components.home.botstats.BotStatsLayout;
import com.gmail.leonard.spring.frontend.layouts.MainLayout;
import com.gmail.leonard.spring.frontend.layouts.PageLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "", layout = MainLayout.class)
@CssImport("./styles/home.css")
@SetDivStretchBackground(background = "var(--lumo-secondary)")
public class HomeView extends PageLayout {

    public HomeView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);
        add(
                new BotInfoLayout(uiData),
                new BotProsLayout(uiData),
                new BotStatsLayout()
        );
    }

}
