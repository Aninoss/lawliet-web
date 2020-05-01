package com.gmail.leonard.spring.Frontend.Components;

import com.gmail.leonard.spring.ExternalLinks;
import com.gmail.leonard.spring.Frontend.Styles;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class FooterArea extends Footer {

    public FooterArea() {
        addClassName(Styles.FADE_IN);
        setWidth("100vw");
        setHeight("auto");
        getStyle().
                set("background-color", "var(--lumo-shade)").
                set("margin-top", "48px");

        Div mainContent = new Div();
        mainContent.addClassName(Styles.APP_WIDTH);

        String[][] links = {
                {"footer.upvote", ExternalLinks.UPVOTE_URL},
                {"footer.invite", ExternalLinks.BOT_INVITE_URL},
                {"footer.server", ExternalLinks.SERVER_INVITE_URL},
                {"footer.patreon", ExternalLinks.PATREON_PAGE}
        };

        Div buttonLayout = new Div();
        buttonLayout.addClassName(Styles.CENTER_TEXT);
        buttonLayout.getStyle().set("margin-top", "22px");
        for(int i = 0; i < links.length; i++) {
            String[] pair = links[i];
            if (i > 0) buttonLayout.add(new Text(" | "));

            Text text = new Text(getTranslation(pair[0]));
            Anchor link = new Anchor(pair[1], text);
            link.setTarget("_blank");

            buttonLayout.add(link);
        }

        mainContent.add(buttonLayout);

        Div bottomText = new Div(new Text(getTranslation("footer.text")));
        bottomText.getStyle()
                .set("margin-bottom", "48px");
        bottomText.addClassName(Styles.CENTER_TEXT);
        mainContent.add(bottomText);

        add(mainContent);
    }
}
