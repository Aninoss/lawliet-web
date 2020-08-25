package com.gmail.leonard.spring.Frontend.Components.FeatureRequests;

import com.gmail.leonard.spring.Backend.FeatureRequests.FRDynamicBean;
import com.gmail.leonard.spring.Backend.FeatureRequests.FRPanelType;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.html.Div;

public class FeatureRequestEntries extends Div {

    private final SessionData sessionData;
    private final FRDynamicBean frDynamicBean;
    private final Accordion accordion = new Accordion();

    public FeatureRequestEntries(SessionData sessionData, FRDynamicBean frDynamicBean) {
        this.sessionData = sessionData;
        this.frDynamicBean = frDynamicBean;

        setWidthFull();
        getStyle().set("margin-top", "0");
        accordion.setWidthFull();

        for (FRPanelType panelType : FRPanelType.values()) {
            addPanel(getTranslation("fr.panel." + panelType), panelType);
        }
        accordion.open(0);
        add(accordion);
    }

    private void addPanel(String summary, FRPanelType type) {
        accordion.add(new AccordionPanel(summary, new FeatureRequestPanel(sessionData, frDynamicBean, type)));
    }

}
