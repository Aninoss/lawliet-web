package com.gmail.leonard.spring.Frontend.Components.FeatureRequests;

import com.gmail.leonard.spring.Backend.FeatureRequests.FRDynamicBean;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.html.Div;

public class FeatureRequestEntries extends Div {

    private final FRDynamicBean frDynamicBean;
    private final Accordion accordion = new Accordion();

    public FeatureRequestEntries(FRDynamicBean frDynamicBean) {
        this.frDynamicBean = frDynamicBean;
        setWidthFull();
        getStyle().set("margin-top", "0");
        accordion.setWidthFull();

        final String[] PANEL_TYPES = { "PENDING", "COMPLETED", "REJECTED" };
        for (String panelType : PANEL_TYPES) {
            addPanel(getTranslation("fr.panel." + panelType), panelType);
        }
        accordion.open(0);
        add(accordion);
    }

    private void addPanel(String summary, String type) {
        accordion.add(new AccordionPanel(summary, new FeatureRequestPanel(frDynamicBean, type)));
    }

}
