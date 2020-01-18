package com.gmail.leonard.spring.Backend;

import org.apache.commons.lang3.RandomStringUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class StringTools {

    public static String getRandomString() {
        int length = 10;
        boolean useLetters = true;
        boolean useNumbers = false;
        return RandomStringUtils.random(length, useLetters, useNumbers);
    }

    public static String numToString(Locale locale, long n) {
        DecimalFormat formatter = new DecimalFormat("#,###", DecimalFormatSymbols.getInstance(Locale.US));
        String str = formatter.format(n);
        if (locale.getLanguage().equalsIgnoreCase("de")) str = str.replace(",",".");

        return str;
    }

    public static String numToString(Locale locale, int n) {
        return numToString(locale, (long) n);
    }

    public static String numToString(long n) {
        return numToString(Locale.US, n);
    }

    public static String numToString(int n) {
        return numToString((long) n);
    }

}
