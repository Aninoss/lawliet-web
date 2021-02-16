package xyz.lawlietbot.spring.backend;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LanguageString {

    Map<String, String> map = new HashMap<>();

    public void set(Locale locale, String string) {
        map.put(parseLocale(locale), string);
    }

    public void set(JSONObject languagePack) {
        for(String key: languagePack.keySet()) {
            set(new Locale(key), languagePack.getString(key));
        }
    }

    public String get(Locale locale) {
        String text = map.get(parseLocale(locale));
        return text != null ? text : map.get("en");
    }

    private String parseLocale(Locale locale) {
        String str = locale.getLanguage();
        if (str.contains("_")) str = str.split("_")[0];

        return str;
    }

}
