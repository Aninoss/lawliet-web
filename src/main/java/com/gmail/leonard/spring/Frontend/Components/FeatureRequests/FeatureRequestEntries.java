package com.gmail.leonard.spring.Frontend.Components.FeatureRequests;

import com.gmail.leonard.spring.Backend.CustomThread;
import com.gmail.leonard.spring.Backend.FeatureRequests.FRDynamicBean;
import com.gmail.leonard.spring.Backend.FeatureRequests.FREntry;
import com.gmail.leonard.spring.Backend.FeatureRequests.FRPanelType;
import com.gmail.leonard.spring.Backend.UserData.SessionData;
import com.gmail.leonard.spring.Backend.UserData.UIData;
import com.gmail.leonard.spring.Frontend.Components.LoadingIndicator;
import com.gmail.leonard.spring.Frontend.Styles;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.Push;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class FeatureRequestEntries extends Div {

    private final static Logger LOGGER = LoggerFactory.getLogger(FeatureRequestEntries.class);

    private final SessionData sessionData;
    private final UIData uiData;
    private final FRDynamicBean frDynamicBean;
    private final Accordion accordion = new Accordion();
    private final Div contentDiv = new Div();
    private final LoadingIndicator loadingIndicator = new LoadingIndicator();
    private PanelsPushThread panelsPushThread;

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

        contentDiv.setWidthFull();
        contentDiv.add(accordion);
        contentDiv.addClassName(Styles.FADE_IN);

        panelsPushThread = new PanelsPushThread(this, UI.getCurrent());
        panelsPushThread.start();

        loadingIndicator.getStyle().set("margin-top", "64px");
        add(loadingIndicator);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        panelsPushThread.interrupt();
        panelsPushThread = null;
    }

    public Div getContentDiv() {
        return contentDiv;
    }

    public void removeLoadingIndicator() {
        remove(loadingIndicator);
    }

    private void addPanel(FRPanelType type) {
        ArrayList<FREntry> entries = frDynamicBean.getEntryCategoryMap(type);
        if (entries.size() > 0) {
            String summary = getTranslation("fr.panel." + type.name(), entries.size());
            accordion.add(new AccordionPanel(summary, new FeatureRequestPanel(sessionData, uiData, frDynamicBean, entries, type)));
        }
    }

    private static class PanelsPushThread extends Thread {

        private final FeatureRequestEntries parent;
        private final UI ui;

        public PanelsPushThread(FeatureRequestEntries parent, UI ui) {
            this.parent = parent;
            this.ui = ui;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(100);
                ui.access(() -> {
                    parent.removeLoadingIndicator();
                    parent.add(parent.getContentDiv());
                });
            } catch (InterruptedException e) {
                //Ignore
            }
        }

    }

}
