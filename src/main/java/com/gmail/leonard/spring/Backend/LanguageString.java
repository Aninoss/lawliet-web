package com.gmail.leonard.spring.Backend;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LanguageString {

    Map<String, String> map = Collections.synchronizedMap(new HashMap<>());

    public void set(Locale locale, String string) {
        map.put(parseLocale(locale), string);
    }

    public void set(JSONObject languagePack) {
        for(String key: languagePack.keySet()) {
            set(new Locale(key), languagePack.getString(key));
        }
    }

    public String get(Locale locale) {
        return map.get(parseLocale(locale));
    }

    private String parseLocale(Locale locale) {
        String str = locale.getLanguage();
        if (str.contains("_")) str = str.split("_")[0];

        return str;
    }

}
