package xyz.lawlietbot.spring.backend.payment;

import com.vaadin.flow.component.icon.VaadinIcon;

public enum SubLevel {

    BASIC(
            false,
            true,
            VaadinIcon.FIRE
    ),

    PRO(
            true,
            true,
            VaadinIcon.ROCKET
    ),

    ULTIMATE(
            false,
            false,
            VaadinIcon.DIAMOND
    );


    private final boolean recommended;
    private final boolean buyDirectly;
    private final VaadinIcon vaadinIcon;

    SubLevel(boolean recommended, boolean buyDirectly, VaadinIcon vaadinIcon) {
        this.recommended = recommended;
        this.buyDirectly = buyDirectly;
        this.vaadinIcon = vaadinIcon;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public boolean buyDirectly() {
        return buyDirectly;
    }

    public VaadinIcon getVaadinIcon() {
        return vaadinIcon;
    }
}
