package xyz.lawlietbot.spring.frontend.views;

import java.io.IOException;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.backend.FileString;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;

@Route(value = "privacy", layout = MainLayout.class)
public class PrivacyView extends PageLayout {

    public PrivacyView(@Autowired SessionData sessionData, @Autowired UIData uiData) throws IOException {
        super(sessionData, uiData);
        getStyle().set("margin-bottom", "48px");
        String name = String.format("privacy_%s.html", getLocale().getLanguage());
        String pageString = new FileString(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(name)
        ).toString();

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setSpacing(false);
        mainContent.addClassName(Styles.APP_WIDTH);

        Div div = new Div();
        div.addClassName(Styles.APP_WIDTH);
        div.add(new Html(pageString));

        mainContent.add(div);
        add(new PageHeader(getUiData(), getTitleText(), null), mainContent);
    }

}
