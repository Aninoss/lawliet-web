package xyz.lawlietbot.spring;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vaadin.flow.i18n.I18NProvider;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TranslationProvider implements I18NProvider {

    public static final String BUNDLE_PREFIX = "translate";

    public final Locale LOCALE_EN = new Locale("en");
    public final Locale LOCALE_DE = new Locale("de");
    public final Locale LOCALE_RU = new Locale("ru");

    private final List<Locale> locales = Collections
            .unmodifiableList(Arrays.asList(LOCALE_EN, LOCALE_RU, LOCALE_DE));

    private static final LoadingCache<Locale, ResourceBundle> bundleCache = CacheBuilder
            .newBuilder().expireAfterWrite(1, TimeUnit.DAYS)
            .build(new CacheLoader<Locale, ResourceBundle>() {
                @Override
                public ResourceBundle load(final Locale key) throws Exception {
                    return initializeBundle(key);
                }
            });

    @Override
    public List<Locale> getProvidedLocales() {
        return locales;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) {
            LoggerFactory.getLogger(TranslationProvider.class.getName())
                    .warn("Got lang request for key with null value!");
            return "";
        }

        final ResourceBundle bundle = bundleCache.getUnchecked(locale);

        if (bundle == null) return "!" + key + "!";

        String value;
        try {
            value = bundle.getString(key);
        } catch (final MissingResourceException e) {
            LoggerFactory.getLogger(TranslationProvider.class.getName())
                    .warn("Missing resource", e);
            return "!" + locale.getLanguage() + ": " + key;
        }

        String[] placeholders = extractGroups(RegexPatterns.TEXT_PLACEHOLDER_PATTERN, value);
        value = processReferences(value, placeholders, locale);
        if (params.length > 0) {
            if (params[0] instanceof Boolean) {
                value = processMultiOptions(value, (boolean)params[0] ? 1 : 0);
                System.arraycopy(params, 1, params, 0, params.length - 1);
            }
            value = MessageFormat.format(value, params);
        }
        return value;
    }

    private static ResourceBundle initializeBundle(final Locale locale) {
        return readProperties(locale);
    }

    private static ResourceBundle readProperties(final Locale locale) {
        final ClassLoader cl = TranslationProvider.class.getClassLoader();

        ResourceBundle propertiesBundle = null;
        try {
            propertiesBundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale, cl, new UTF8Control());
        } catch (final MissingResourceException e) {
            LoggerFactory.getLogger(TranslationProvider.class.getName())
                    .warn("Missing resource", e);
        }

        return propertiesBundle;
    }

    private String[] extractGroups(Pattern pattern, String text) {
        ArrayList<String> placeholderList = new ArrayList<>();
        Matcher m = pattern.matcher(text);
        while(m.find()) {
            placeholderList.add(m.group("inner"));
        }
        return placeholderList.toArray(new String[0]);
    }

    private String processMultiOptions(String text, int option) {
        String[] groups = extractGroups(RegexPatterns.TEXT_MULTIOPTION_PATTERN, text);

        for (String group : groups) {
            if (group.contains("|")) {
                text = text.replace("[" + group + "]", group.split("\\|")[option]);
            }
        }

        return text.replace("\\[", "[").replace("\\]", "]");
    }

    private String processReferences(String text, String[] placeholders, Locale locale) {
        for (String placeholder : placeholders) {
            if (placeholder.startsWith("this.")) {
                String key = placeholder.substring(5);
                String newValue = getTranslation(key, locale);
                text = text.replace("{" + placeholder + "}", newValue);
            }
        }

        return text;
    }

}