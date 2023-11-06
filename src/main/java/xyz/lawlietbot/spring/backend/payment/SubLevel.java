package xyz.lawlietbot.spring.backend.payment;

import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.Map;

public enum SubLevel {

    BASIC(Map.of(SubCurrency.USD, 300, SubCurrency.EUR, 300, SubCurrency.GBP, 250), false, true, VaadinIcon.FIRE),
    PRO(Map.of(SubCurrency.USD, 500, SubCurrency.EUR, 450, SubCurrency.GBP, 400), true, true, VaadinIcon.ROCKET),
    ULTIMATE(Map.of(SubCurrency.USD, 1000, SubCurrency.EUR, 900, SubCurrency.GBP, 800), false, false, VaadinIcon.DIAMOND);

    private final Map<SubCurrency, Integer> priceMap;
    private final boolean recommended;
    private final boolean buyDirectly;
    private final VaadinIcon vaadinIcon;

    SubLevel(Map<SubCurrency, Integer> priceMap, boolean recommended, boolean buyDirectly, VaadinIcon vaadinIcon) {
        this.priceMap = priceMap;
        this.recommended = recommended;
        this.buyDirectly = buyDirectly;
        this.vaadinIcon = vaadinIcon;
    }

    public int getPrice(SubCurrency currency) {
        return priceMap.get(currency);
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
