package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Backend.FAQ.FAQListContainer;
import com.gmail.leonard.spring.Backend.FAQ.FAQListSlot;
import com.gmail.leonard.spring.Backend.Language.PageTitleGen;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.HtmlText;
import com.gmail.leonard.spring.Frontend.Layouts.MainLayout;
import com.gmail.leonard.spring.Frontend.Layouts.PageLayout;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "faq", layout = MainLayout.class)
public class FAQView extends PageLayout {

    public FAQView(@Autowired UIData uiData) {
        setWidthFull();
        VerticalLayout mainContent = new VerticalLayout();

        mainContent.addClassName("app-width");
        mainContent.setPadding(true);

        H2 title = new H2(getTitleText());
        mainContent.add(title);

        Accordion accordion = new Accordion();
        accordion.setWidthFull();
        for(int i = 0; i < FAQListContainer.getInstance().size(); i++) {
            FAQListSlot slot = FAQListContainer.getInstance().get(i);

            if (i != 3 || !uiData.isNSFWDisabled()) {
                Label header = new Label(slot.getQuestion().get(getLocale()));

                Div labelDiv = new Div(new HtmlText(slot.getAnswer().get(getLocale())));
                labelDiv.getStyle()
                        .set("background-color", "var(--lumo-secondary)")
                        .set("padding", "12px")
                        .set("margin-left", "-16px")
                        .set("margin-right", "-16px")
                        .set("margin-bottom", "-8px");

                AccordionPanel accordionPanel = new AccordionPanel(header, labelDiv);
                accordionPanel.getElement().getStyle().set("margin-bottom", "12px");
                accordion.add(accordionPanel)
                        .addThemeVariants(DetailsVariant.FILLED);
            }
        }

        mainContent.add(accordion);
        add(mainContent);
    }

}
