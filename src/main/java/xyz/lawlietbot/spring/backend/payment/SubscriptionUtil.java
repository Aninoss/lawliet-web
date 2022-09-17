package xyz.lawlietbot.spring.backend.payment;

import xyz.lawlietbot.spring.backend.util.StringUtil;

public class SubscriptionUtil {

    public static int getPrice(SubDuration duration, SubLevel level, SubCurrency currency) {
        return level.getPrice(currency) * duration.getPriceFactor();
    }

    public static String generatePriceString(int price) {
        if (price % 100 == 0) {
            return StringUtil.numToString(price / 100);
        } else {
            String decimal = String.format("%02d", price % 100);
            return StringUtil.numToString(price / 100) + "." + decimal;
        }
    }

}
