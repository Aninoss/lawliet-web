package xyz.lawlietbot.spring.frontend.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import xyz.lawlietbot.spring.TranslationProvider;

import java.util.Locale;

public class LocaleSelect extends Select<Locale> {

    public static final String LOCALE_COOKIE_NAME = "locale";

    public LocaleSelect() {
        setWidth("95px");
        setRenderer(new ComponentRenderer<>(this::resolveFlagImage));
        setItems(TranslationProvider.PROVIDED_LOCALES);
        setValue(getLocale());
        addValueChangeListener(e -> {
            UI.getCurrent().getPage().executeJs("createCookie($0, $1, 400)", LOCALE_COOKIE_NAME, e.getValue().getLanguage());
            UI.getCurrent().getPage().reload();
        });
    }

    private Component resolveFlagImage(Locale locale) {
        String emoji = "";
        switch (locale.getLanguage()) {
            case "en":
                emoji = "🇬🇧";
                break;
            case "de":
                emoji = "🇩🇪";
                break;
            case "es":
                emoji = "🇪🇸";
                break;
            case "ru":
                emoji = "🇷🇺";
                break;
        }
        return new Text(emoji + " " + locale.getLanguage().toUpperCase());
    }
}
