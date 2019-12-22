package com.gmail.leonard.spring.Frontend.Views;

import com.gmail.leonard.spring.Backend.Language.PageTitleFactory;
import com.gmail.leonard.spring.Frontend.Components.Commands.CommandIcon;
import com.gmail.leonard.spring.Frontend.Layouts.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;

@Route(value = FAQView.ID, layout = MainLayout.class)
public class FAQView extends Main implements HasDynamicTitle {

    public static final String ID = "faq";

    public FAQView() {
        setWidthFull();
        VerticalLayout mainContent = new VerticalLayout();

        mainContent.addClassName("app-width");
        mainContent.setPadding(true);

        H2 title = new H2(getTranslation("category." + ID));
        mainContent.add(title);

        Accordion accordion = new Accordion();
        accordion.setWidthFull();
        for(int i = 0; i < 7; i++) {
            Label header = new Label(getTranslation(String.format("faq.%d.question", i)));
            //header.getStyle().set("font-weight", "bold");

            Div labelDiv = new Div(new Label(getTranslation(String.format("faq.%d.answer", i))));
            labelDiv.getStyle()
                    //.set("margin-left", "14px")
                    .set("background-color", "var(--lumo-secondary)")
                    .set("padding", "12px")
                    .set("margin-left", "-16px")
                    .set("margin-right", "-16px")
                    .set("margin-bottom", "-8px");

            AccordionPanel accordionPanel = new AccordionPanel(header, labelDiv);
            accordion.add(accordionPanel)
                    .addThemeVariants(DetailsVariant.FILLED);
        }

        mainContent.add(accordion);
        add(mainContent);
    }

    @Override
    public String getPageTitle() {
        return PageTitleFactory.getPageTitle(ID);
    }

}
