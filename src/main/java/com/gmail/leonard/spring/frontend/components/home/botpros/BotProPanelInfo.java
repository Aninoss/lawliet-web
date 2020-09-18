package com.gmail.leonard.spring.frontend.components.home.botpros;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;

public class BotProPanelInfo {

    private final String id;
    private final boolean visible;
    private final Icon icon;
    private final int characterLimit;
    private final Component[] components;

    public BotProPanelInfo(String id, boolean visible, Icon icon, Component... components) {
        this(id, visible, icon, -1, components);
    }

    public BotProPanelInfo(String id, boolean visible, Icon icon, int characterLimit, Component... components) {
        this.id = id;
        this.visible = visible;
        this.icon = icon;
        this.characterLimit = characterLimit;
        this.components = components;
    }

    public String getId() {
        return id;
    }

    public boolean isVisible() {
        return visible;
    }

    public Icon getIcon() {
        return icon;
    }

    public int getCharacterLimit() {
        return characterLimit;
    }

    public Component[] getComponents() {
        return components;
    }
}
