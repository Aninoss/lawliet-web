package com.gmail.leonard.spring.Frontend.Components;

import com.gmail.leonard.spring.ExternalLinks;
import com.gmail.leonard.spring.Frontend.Layouts.PageLayout;
import com.gmail.leonard.spring.Frontend.Styles;
import com.gmail.leonard.spring.Frontend.Views.PrivacyView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.RouterLink;

public class FooterArea extends Footer {

    public FooterArea() {
        addClassName(Styles.FADE_IN);
        setWidthFull();
        setHeight("auto");
        getStyle().
                set("background-color", "var(--lumo-shade)").
                set("margin-top", "48px");

        Div mainContent = new Div();
        mainContent.addClassName(Styles.APP_WIDTH);

        Object[][] links = {
                {"footer.upvote", ExternalLinks.UPVOTE_URL},
                {"footer.invite", ExternalLinks.BOT_INVITE_URL},
                {"footer.server", ExternalLinks.SERVER_INVITE_URL},
                {"footer.patreon", ExternalLinks.PATREON_PAGE},
                {"footer.privacy", PrivacyView.class}
        };

        Div buttonLayout = new Div();
        buttonLayout.addClassName(Styles.CENTER_TEXT);
        buttonLayout.getStyle().set("margin-top", "22px");
        for(int i = 0; i < links.length; i++) {
            Object[] pair = links[i];
            if (i > 0) buttonLayout.add(new Text(" | "));

            String string = getTranslation(pair[0].toString());
            Text text = new Text(string);

            Object value = pair[1];
            if (value instanceof String) {
                Anchor link = new Anchor((String)value, text);
                link.setTarget("_blank");
                buttonLayout.add(link);
            } else if (value instanceof Class) {
                RouterLink link = new RouterLink(string, (Class)value);
                buttonLayout.add(link);
            }
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
