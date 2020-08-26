package com.gmail.leonard.spring.Frontend.Components.FeatureRequests;

import com.gmail.leonard.spring.Backend.FeatureRequests.FRDynamicBean;
import com.gmail.leonard.spring.Backend.FeatureRequests.FREntry;
import com.gmail.leonard.spring.Backend.FeatureRequests.FRPanelType;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.ArrayList;

public class FeatureRequestEntries extends Div {

    private final SessionData sessionData;
    private final UIData uiData;
    private final FRDynamicBean frDynamicBean;
    private final Accordion accordion = new Accordion();

    public FeatureRequestEntries(SessionData sessionData, UIData uiData, FRDynamicBean frDynamicBean) {
        this.sessionData = sessionData;
        this.uiData = uiData;
        this.frDynamicBean = frDynamicBean;

        setWidthFull();
        getStyle().set("margin-top", "0");
        accordion.setWidthFull();

        for (FRPanelType panelType : FRPanelType.values()) {
            addPanel(panelType);
        }

        accordion.open(0);
        add(accordion);
    }

    private void addPanel(FRPanelType type) {
        ArrayList<FREntry> entries = frDynamicBean.getEntryCategoryMap(type);
        if (entries.size() > 0) {
            String summary = getTranslation("fr.panel." + type.name(), entries.size());
            accordion.add(new AccordionPanel(summary, new FeatureRequestPanel(sessionData, uiData, frDynamicBean, entries, type)));
        }
    }

}
