package com.gmail.leonard.spring.Frontend.Components;

import com.gmail.leonard.spring.ExternalLinks;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class FooterArea extends Footer {

    public FooterArea() {
        setWidthFull();
        addClassName("fadein-class");
        getStyle().
                set("background-color", "var(--lumo-shade)").
                set("margin-top", "16px").
                set("height", "auto");

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.addClassName("app-width");
        mainContent.setPadding(true);

        String[][] links = {
                {"footer.upvote", ExternalLinks.UPVOTE_URL},
                {"footer.invite", ExternalLinks.BOT_INVITE_URL},
                {"footer.server", ExternalLinks.SERVER_INVITE_URL}
        };

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        for(int i = 0; i < links.length; i++) {
            String[] pair = links[i];
            if (i > 0) buttonLayout.add(new Paragraph("|"));

            Paragraph p = new Paragraph(getTranslation(pair[0]));
            p.addClassName("center-text");
            Anchor link = new Anchor(pair[1], p);
            link.setTarget("_blank");

            buttonLayout.add(link);
        }

        mainContent.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        mainContent.add(buttonLayout);

        Span bottomText = new Span(getTranslation("footer.text"));
        bottomText.getStyle()
                .set("margin-top", "-8px")
                .set("margin-bottom", "32px");
        bottomText.addClassName("center-text");
        mainContent.add(bottomText);

        add(mainContent);
    }
}
