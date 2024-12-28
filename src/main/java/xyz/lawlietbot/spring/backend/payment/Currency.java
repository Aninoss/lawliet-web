package xyz.lawlietbot.spring.backend.payment;

public enum Currency {

    USD("$", 2),
    EUR("€", 2),
    GBP("£", 2),
    ARS("Arg$", 2),
    AUD("A$", 2),
    BRL("R$", 2),
    CAD("C$", 2),
    CHF("CHF", 2),
    COP("Col$", 2),
    CZK("Kč", 2),
    DKK("DKr", 2),
    HKD("HK$", 2),
    HUF("Ft", 2),
    INR("₹", 2),
    ILS("₪", 2),
    JPY("¥", 0),
    KRW("₩", 0),
    MXN("Mex$", 2),
    NOK("NKr", 2),
    NZD("$NZ", 2),
    PLN("zł", 2),
    RUB("₽", 2),
    SEK("SKr", 2),
    SGD("S$", 2),
    THB("฿", 2),
    TRY("₺", 2),
    TWD("NT$", 2),
    UAH("₴", 2),
    CNY("¥", 2),
    VND("₫", 0),
    ZAR("R", 2);


    private final String symbol;
    private final int decimalPlaces;

    Currency(String symbol, int decimalPlaces) {
        this.symbol = symbol;
        this.decimalPlaces = decimalPlaces;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }
}
