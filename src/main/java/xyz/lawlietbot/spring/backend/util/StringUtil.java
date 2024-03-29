package xyz.lawlietbot.spring.backend.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.apache.commons.lang3.RandomStringUtils;

public class StringUtil {

    private StringUtil(){}

    public static String getRandomString() {
        int length = 10;
        boolean useLetters = true;
        boolean useNumbers = false;
        return RandomStringUtils.random(length, useLetters, useNumbers);
    }

    public static String numToString(long n) {
        DecimalFormat formatter = new DecimalFormat("#,###", DecimalFormatSymbols.getInstance(Locale.US));
        return formatter.format(n).replace(",", " ");
    }

    public static String numToString(int n) {
        return numToString((long) n);
    }

    public static boolean stringIsDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean stringIsLong(String string) {
        try {
            Long.parseLong(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean stringIsInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String shortenString(String str, int limit) {
        if (str.length() > limit) {
            str = str.substring(0, limit - 4);

            if (str.contains("\n")) {
                int pos = str.lastIndexOf("\n");
                str = str.substring(0, pos);
            } else {
                if (str.contains(" ")) {
                    int pos = str.lastIndexOf(" ");
                    str = str.substring(0, pos);
                }
            }
            while (str.length() > 0 && (str.charAt(str.length() - 1) == '.' || str.charAt(str.length() - 1) == ' ' || str.charAt(str.length() - 1) == '\n')) str = str.substring(0, str.length() - 1);

            str = str + " (…)";
        }
        return str;
    }

}
