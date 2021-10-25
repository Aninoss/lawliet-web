package xyz.lawlietbot.spring.backend.payment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum SubLevel {

    BASIC_USD(SubLevelType.BASIC, SubCurrency.USD, 300),
    PRO_USD(SubLevelType.PRO, SubCurrency.USD, 500),

    BASIC_EUR(SubLevelType.BASIC, SubCurrency.EUR, 300),
    PRO_EUR(SubLevelType.PRO, SubCurrency.EUR, 450),

    BASIC_GBP(SubLevelType.BASIC, SubCurrency.GBP, 250),
    PRO_GBP(SubLevelType.PRO, SubCurrency.GBP, 400);


    private final SubLevelType subLevelType;
    private final SubCurrency currency;
    private final int price;

    SubLevel(SubLevelType subLevelType, SubCurrency currency, int price) {
        this.subLevelType = subLevelType;
        this.currency = currency;
        this.price = price;
    }

    public SubLevelType getSubLevelType() {
        return subLevelType;
    }

    public SubCurrency getCurrency() {
        return currency;
    }

    public int getPrice() {
        return price;
    }

    public static List<SubLevel> getSubLevelsOfCurrency(SubCurrency currency) {
        return Arrays.stream(SubLevel.values())
                .filter(subLevel -> subLevel.getCurrency() == currency)
                .collect(Collectors.toList());
    }

}
