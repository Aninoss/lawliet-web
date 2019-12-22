package com.gmail.leonard.spring.Backend;

import org.apache.commons.lang3.RandomStringUtils;

public class StringTools {

    public static String getRandomString() {

        int length = 10;
        boolean useLetters = true;
        boolean useNumbers = false;
        return RandomStringUtils.random(length, useLetters, useNumbers);
    }

}
