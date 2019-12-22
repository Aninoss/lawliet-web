package com.gmail.leonard.spring;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vaadin.flow.i18n.I18NProvider;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class TranslationProvider implements I18NProvider {

    public static final String BUNDLE_PREFIX = "translate";

    public final Locale LOCALE_DE = new Locale("de");
    public final Locale LOCALE_EN = new Locale("en");

    private List<Locale> locales = Collections
            .unmodifiableList(Arrays.asList(LOCALE_EN, LOCALE_DE));

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
            value = bundle.getString(key)
                .replace("\n", "<br>");
        } catch (final MissingResourceException e) {
            LoggerFactory.getLogger(TranslationProvider.class.getName())
                    .warn("Missing resource", e);
            return "!" + locale.getLanguage() + ": " + key;
        }

        if (params.length > 0) {

            boolean secondOption = false;
            boolean hasChoice = false;

            if (params[0] instanceof Boolean) {
                hasChoice = true;
                secondOption = (boolean) params[0];
                Object[] newParams = new Object[params.length - 1];
                System.arraycopy(params, 1, newParams, 0, params.length - 1);
                params = newParams;
            }

            value = MessageFormat.format(value, params);

            if (hasChoice) {
                String[] parts = value.split("%\\[");
                for (int i = 1; i < parts.length; i++) {
                    String subText = parts[i];
                    if (subText.contains("]%")) {
                        subText = subText.split("]%")[0];
                        String[] options = subText.split("\\|");
                        value = value.replace("%[" + subText + "]%", options[secondOption ? 1 : 0]);
                    }
                }
            }
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
            propertiesBundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale,
                    cl);
        } catch (final MissingResourceException e) {
            LoggerFactory.getLogger(TranslationProvider.class.getName())
                    .warn("Missing resource", e);
        }

        return propertiesBundle;
    }
}