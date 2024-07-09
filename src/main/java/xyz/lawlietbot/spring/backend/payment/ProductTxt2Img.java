package xyz.lawlietbot.spring.backend.payment;

public enum ProductTxt2Img {

    IMAGES_50(50),
    IMAGES_250(250),
    IMAGES_1000(1000),
    IMAGES_5000(5000);

    private final int number;

    ProductTxt2Img(int number) {
        this.number = number;
    }

    public String getPriceId() {
        return System.getenv("PADDLE_TXT2IMG_IDS").split(",")[ordinal()];
    }

    public int getNumber() {
        return number;
    }

    public static ProductTxt2Img fromPriceId(String priceId) {
        for (ProductTxt2Img value : values()) {
            if (value.getPriceId().equals(priceId)) {
                return value;
            }
        }
        return null;
    }

}
