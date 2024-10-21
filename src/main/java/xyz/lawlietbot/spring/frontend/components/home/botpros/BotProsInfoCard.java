package xyz.lawlietbot.spring.frontend.components.home.botpros;

import xyz.lawlietbot.spring.backend.util.StringUtil;
import xyz.lawlietbot.spring.frontend.components.Card;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.Objects;
import java.util.stream.Stream;

public class BotProsInfoCard extends Card {

    private boolean longVersion = false;
    private Button buttonMore = null;
    private final Text text;
    private final String longText;
    private final String shortText;

    public BotProsInfoCard(String title, String subtitle, String desc, Icon icon, int characterLimit, Component... components) {
        longText = desc;
        shortText = characterLimit > 0 ? StringUtil.shortenString(longText, characterLimit) : longText;
        setHeightFull();

        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout headerContent = new VerticalLayout();
        headerContent.setPadding(false);
        headerContent.setSpacing(false);
        headerContent.setAlignItems(FlexComponent.Alignment.CENTER);

        H3 titleLabel = new H3(new Text(title));
        titleLabel.getElement().getStyle()
                .set("font-size", "120%")
                .set("width", "100%")
                .set("text-align", "center")
                .set("margin-top", "6px")
                .set("font-weight", "bold");

        Div subtitleLabel = new Div(new Text(subtitle));
        subtitleLabel.setWidthFull();
        subtitleLabel.addClassName("center");
        subtitleLabel.getElement().getStyle()
                .set("font-size", "80%")
                .set("text-align", "center")
                .set("color", "var(--lumo-disabled-text-color)")
                .set("margin-top", "2px");

        text = new Text(shortText);
        Div descLabel = new Div(text);
        descLabel.setWidthFull();
        descLabel.addClassName("center");

        Hr seperator = new Hr();
        seperator.getStyle()
                .set("margin-top", "12px")
                .set("margin-bottom", "-4px");

        headerContent.add(icon, titleLabel, subtitleLabel, seperator);
        content.add(headerContent, descLabel);
        Stream.of(components).filter(Objects::nonNull).forEach(content::add);

        if (shortText.length() < longText.length()) {
            buttonMore = new Button(getTranslation("bot.card.more"), VaadinIcon.ARROW_DOWN.create());
            buttonMore.setIconAfterText(true);
            buttonMore.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            buttonMore.addClickListener(click -> onButtonMoreClick());
            content.add(buttonMore);
        }

        content.setHeightFull();
        content.setFlexGrow(1, descLabel);

        add(content);
    }

    private void onButtonMoreClick() {
        if (longVersion) {
            text.setText(shortText);
            buttonMore.setText(getTranslation("bot.card.more"));
            buttonMore.setIcon(VaadinIcon.ARROW_DOWN.create());
            longVersion = false;
        } else {
            text.setText(longText);
            buttonMore.setText(getTranslation("bot.card.less"));
            buttonMore.setIcon(VaadinIcon.ARROW_UP.create());
            longVersion = true;
        }
    }

}