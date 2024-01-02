package xyz.lawlietbot.spring.backend.payment.paddle;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class PaddleBillingWebhookVerifier {

    private final String secretKey;

    public PaddleBillingWebhookVerifier(String secretKey) {
        this.secretKey = secretKey;
    }

    public boolean verify(String body, String paddleSignature) throws InvalidKeyException {
        String[] split = paddleSignature.split(";");
        String ts = split[0].split("=")[1];
        String h1 = split[1].split("=")[1];

        String payload = ts + ":" + body;
        String hmac = hmacSha256(payload, secretKey);
        return hmac.equals(h1);
    }

    private String hmacSha256(String data, String key) throws InvalidKeyException {
        String algorithm = "HmacSHA256";
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), algorithm);
        Mac mac;
        try {
            mac = Mac.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        mac.init(secretKeySpec);
        return hex(mac.doFinal(data.getBytes()));
    }

    public static String hex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(String.format("%02x", aByte));
        }
        return result.toString();
    }

}
