package xyz.lawlietbot.spring.backend.payment;

import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.Map;

public enum SubLevel {

    BASIC(
            Map.of(SubCurrency.USD, 299, SubCurrency.EUR, 299, SubCurrency.GBP, 249),
            Map.of(SubCurrency.USD, 2699, SubCurrency.EUR, 2699, SubCurrency.GBP, 2249),
            false,
            true,
            VaadinIcon.FIRE
    ),

    PRO(
            Map.of(SubCurrency.USD, 499, SubCurrency.EUR, 449, SubCurrency.GBP, 399),
            Map.of(SubCurrency.USD, 4499, SubCurrency.EUR, 4049, SubCurrency.GBP, 3599),
            true,
            true,
            VaadinIcon.ROCKET
    ),

    ULTIMATE(
            Map.of(SubCurrency.USD, 999, SubCurrency.EUR, 899, SubCurrency.GBP, 799),
            Map.of(SubCurrency.USD, 8999, SubCurrency.EUR, 8099, SubCurrency.GBP, 7199),
            false,
            false,
            VaadinIcon.DIAMOND
    );


    private final Map<SubCurrency, Integer> monthlyPriceMap;
    private final Map<SubCurrency, Integer> yearlyPriceMap;
    private final boolean recommended;
    private final boolean buyDirectly;
    private final VaadinIcon vaadinIcon;

    SubLevel(Map<SubCurrency, Integer> monthlyPriceMap, Map<SubCurrency, Integer> yearlyPriceMap, boolean recommended,
             boolean buyDirectly, VaadinIcon vaadinIcon
    ) {
        this.monthlyPriceMap = monthlyPriceMap;
        this.yearlyPriceMap = yearlyPriceMap;
        this.recommended = recommended;
        this.buyDirectly = buyDirectly;
        this.vaadinIcon = vaadinIcon;
    }

    public int getPrice(SubDuration duration, SubCurrency currency) {
        if (duration == SubDuration.MONTHLY) {
            return monthlyPriceMap.get(currency);
        } else {
            return yearlyPriceMap.get(currency);
        }
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
