package xyz.lawlietbot.spring.backend.payment;

import java.util.Locale;

public enum SubCurrency {

    USD('$'),
    EUR('€');


    private final char symbol;

    SubCurrency(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

    public static SubCurrency retrieveDefaultCurrency(Locale locale) {
        if (locale.getLanguage().equals("de")) {
            return EUR;
        }
        return USD;
    }

}
