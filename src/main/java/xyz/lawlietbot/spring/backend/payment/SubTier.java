package xyz.lawlietbot.spring.backend.payment;

import com.vaadin.flow.component.icon.VaadinIcon;

public enum SubTier {

    BASIC(3, new int[]{ 4928466 }, false, VaadinIcon.FIRE),
    PRO(5, new int[]{ 5074151, 5074320, 5080986 }, true, VaadinIcon.ROCKET);

    private final int price;
    private final int[] ids;
    private final boolean recommended;
    private final VaadinIcon vaadinIcon;

    SubTier(int price, int[] ids, boolean recommended, VaadinIcon vaadinIcon) {
        this.price = price;
        this.ids = ids;
        this.recommended = recommended;
        this.vaadinIcon = vaadinIcon;
    }

    public int getPrice() {
        return price;
    }

    public int[] getIds() {
        return ids;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public VaadinIcon getVaadinIcon() {
        return vaadinIcon;
    }

}