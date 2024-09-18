package xyz.lawlietbot.spring.backend;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class LanguageString {

    Map<String, String> map = new HashMap<>();

    public void set(Locale locale, String string) {
        map.put(parseLocale(locale), string);
    }

    public void set(JSONObject languagePack) {
        for (Iterator it = languagePack.keys(); it.hasNext(); ) {
            String key = (String) it.next();
            try {
                set(new Locale(key), languagePack.getString(key));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
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
