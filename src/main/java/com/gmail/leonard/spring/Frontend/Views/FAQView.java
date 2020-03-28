package com.gmail.leonard.spring.Frontend.Views;

import com.github.appreciated.card.Card;
import com.gmail.leonard.spring.Backend.FAQ.FAQListContainer;
import com.gmail.leonard.spring.Backend.FAQ.FAQListSlot;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.HtmlText;
import com.gmail.leonard.spring.Frontend.Layouts.MainLayout;
import com.gmail.leonard.spring.Frontend.Layouts.PageLayout;
import com.gmail.leonard.spring.Frontend.Styles;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "faq", layout = MainLayout.class)
public class FAQView extends PageLayout {

    public FAQView(@Autowired UIData uiData) {
        setWidthFull();
        VerticalLayout mainContent = new VerticalLayout();

        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.setPadding(true);

        H1 title = new H1(getTitleText());
        title.getStyle().set("margin-bottom", "-16px");
        title.setWidthFull();
        mainContent.add(title);

        for(int i = 0; i < FAQListContainer.getInstance().size(); i++) {
            FAQListSlot slot = FAQListContainer.getInstance().get(i);

            if (i != 3 || !uiData.isNSFWDisabled()) {
                H4 h4 = new H4(new Text(slot.getQuestion().get(getLocale())));
                h4.getStyle().set("margin", "10px");
                Card question = new Card(h4);
                question.setWidthFull();
                question.getStyle().set("margin-top", "48px");

                HtmlText answer = new HtmlText(slot.getAnswer().get(getLocale()));
                mainContent.add(question, answer);
            }
        }

        add(mainContent);
    }

}
