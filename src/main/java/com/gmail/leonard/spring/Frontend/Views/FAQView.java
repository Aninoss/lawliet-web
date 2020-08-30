package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Backend.FAQ.FAQListContainer;
import com.gmail.leonard.spring.Backend.FAQ.FAQListSlot;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.Card;
import com.gmail.leonard.spring.Frontend.Components.HtmlText;
import com.gmail.leonard.spring.Frontend.Components.PageHeader;
import com.gmail.leonard.spring.Frontend.Layouts.MainLayout;
import com.gmail.leonard.spring.Frontend.Layouts.PageLayout;
import com.gmail.leonard.spring.Frontend.Styles;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "faq", layout = MainLayout.class)
public class FAQView extends PageLayout {

    private final VerticalLayout mainContent = new VerticalLayout();

    public FAQView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);

        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.setPadding(true);

        addEntries();
        add(new PageHeader(getTitleText(), getTranslation("faq.desc"), getRoute()), mainContent);
    }

    private void addEntries() {
        for(int i = 0; i < FAQListContainer.getInstance().size(); i++) {
            FAQListSlot slot = FAQListContainer.getInstance().get(i);

            if (i != 3 || !getUiData().isNSFWDisabled()) {
                H4 question = new H4(new Text(slot.getQuestion().get(getLocale())));
                HtmlText answer = new HtmlText(slot.getAnswer().get(getLocale()));

                Card card = new Card(new VerticalLayout(question, new Hr(), answer));
                card.setWidthFull();
                mainContent.add(card);
            }
        }
    }

}
