package xyz.lawlietbot.spring.frontend.views;

import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.layouts.ErrorLayout;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "notfound", layout = MainLayout.class)
public class PageNotFoundView extends ErrorLayout {

    public PageNotFoundView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData, "404");
    }

}