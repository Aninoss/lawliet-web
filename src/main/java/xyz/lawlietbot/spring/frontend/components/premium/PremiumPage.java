package xyz.lawlietbot.spring.frontend.components.premium;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public abstract class PremiumPage extends VerticalLayout {

    private boolean build = false;

    protected abstract void build();

    public void initialize() {
        if (build) {
            return;
        }

        build = true;
        build();
    }

}
