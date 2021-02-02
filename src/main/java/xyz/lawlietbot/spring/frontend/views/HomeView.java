package xyz.lawlietbot.spring.frontend.views;

import xyz.lawlietbot.spring.SetDivStretchBackground;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.components.home.botinfo.BotInfoLayout;
import xyz.lawlietbot.spring.frontend.components.home.botpros.BotProsLayout;
import xyz.lawlietbot.spring.frontend.components.home.botstats.BotStatsLayout;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
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
