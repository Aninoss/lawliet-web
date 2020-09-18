package com.gmail.leonard.spring.backend;

import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class SecretManager {
    public static String getString(String key) throws IOException {
        ResourceBundle texts = PropertyResourceBundle.getBundle("secrets");
        if (!texts.containsKey(key)) {
            throw new IOException("Key " + key + " not found!");
        } else {
            String text = texts.getString(key);
            return text;
        }
    }
}
