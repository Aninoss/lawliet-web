package xyz.lawlietbot.spring.frontend.components.premium;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.lawlietbot.spring.ExternalLinks;
import xyz.lawlietbot.spring.backend.commandlist.CommandListContainer;
import xyz.lawlietbot.spring.backend.payment.SubDuration;
import xyz.lawlietbot.spring.backend.payment.SubTier;
import xyz.lawlietbot.spring.backend.util.StringUtil;
import xyz.lawlietbot.spring.frontend.PatreonIcon;
import xyz.lawlietbot.spring.frontend.Styles;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class PremiumTiersSection extends VerticalLayout {

    private final static Logger LOGGER = LoggerFactory.getLogger(PremiumTiersSection.class);

    private final Select<SubDuration> durationSelect = new Select<>();
    private final Map<SubTier, H2> priceTextMap = new HashMap<>();
    private final Map<SubTier, Span> pricePeriodTextMap = new HashMap<>();
    private final Map<SubTier, Anchor> buttonAnchorMap = new HashMap<>();
    private HorizontalLayout yearlySuggestionField;
    private NumberField proQuantityNumberField;
    private NumberField proPlusQuantityNumberField;

    public PremiumTiersSection() {
        setPadding(true);
        addClassName("section");
        add(generateMainContent());
        refreshPremiumTiers();
    }

    public Component generateMainContent() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setPadding(false);
        mainLayout.addClassName(Styles.APP_WIDTH);
        mainLayout.add(generateTitle());
        mainLayout.add(generateYearlySuggestionField());
        mainLayout.add(generateTiersCurrencyDurationField());
        mainLayout.add(generateTiersTiers());
        return mainLayout;
    }

    private Component generateTitle() {
        String text = getTranslation("premium.step1");
        H2 title = new H2(text);
        title.addClassName("section-title");
        return title;
    }

    private Component generateYearlySuggestionField() {
        yearlySuggestionField = new HorizontalLayout();
        yearlySuggestionField.setPadding(false);
        yearlySuggestionField.setId("notification-field");

        Icon icon = VaadinIcon.INFO_CIRCLE_O.create();
        icon.setId("notification-icon");
        yearlySuggestionField.add(icon);

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);

        Span text = new Span(getTranslation("premium.suggestyearly.text"));
        content.add(text);

        Button switchButton = new Button(getTranslation("premium.suggestyearly.button"));
        switchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        switchButton.getStyle().set("margin-top", "8px");
        switchButton.addClickListener(e -> durationSelect.setValue(SubDuration.ANNUALLY));
        content.add(switchButton);

        yearlySuggestionField.add(content);
        return yearlySuggestionField;
    }

    private Component generateTiersCurrencyDurationField() {
        HorizontalLayout content = new HorizontalLayout();
        content.setWidthFull();
        content.setSpacing(false);
        content.setPadding(false);
        content.getStyle().set("margin-top", "12px");
        content.add(generateTiersTitleDuration());
        return content;
    }

    private Component generateTiersTitleDuration() {
        HorizontalLayout content = new HorizontalLayout();
        content.setSpacing(false);
        content.setPadding(false);
        content.getStyle().set("margin-left", "auto");

        durationSelect.setItemLabelGenerator((ItemLabelGenerator<SubDuration>) duration -> getTranslation("premium.duration." + duration.name()));
        durationSelect.setItems(SubDuration.values());
        durationSelect.setValue(SubDuration.MONTHLY);
        durationSelect.addValueChangeListener(e -> {
            if (e.getValue() == SubDuration.ANNUALLY && yearlySuggestionField != null) {
                yearlySuggestionField.getStyle().set("display", "none");
            }
            refreshPremiumTiers();
        });
        durationSelect.setMaxWidth("150px");
        content.add(durationSelect);

        return content;
    }

    private void refreshPremiumTiers() {
        SubDuration duration = durationSelect.getValue();

        for (SubTier tier : SubTier.values()) {
            double currentPrice = tier.getPrice() * duration.getPriceFactor();
            int quantity = switch (tier) {
                case PRO -> proQuantityNumberField != null ? proQuantityNumberField.getValue().intValue() : 1;
                case PRO_PLUS -> proPlusQuantityNumberField != null ? proPlusQuantityNumberField.getValue().intValue() : 1;
                default -> 1;
            };
            currentPrice *= quantity;

            NumberFormat formatter = NumberFormat.getCurrencyInstance(getLocale());
            formatter.setMaximumFractionDigits(0);

            String currentPriceString = formatter.format(currentPrice)
                    .replace("¤", "$");

            priceTextMap.get(tier)
                    .setText(currentPriceString);
            pricePeriodTextMap.get(tier)
                    .setText(getTranslation("premium.priceperiod", duration == SubDuration.ANNUALLY));
            buttonAnchorMap.get(tier)
                    .setHref("https://www.patreon.com/checkout/lawlietbot?rid=" + tier.getIds()[quantity - 1] + "&cadence=" + duration.getCadence());
        }
    }

    private int extractValueFromQuantity(Double value) {
        value = value != null ? value : 0;
        return Math.max(Math.min((int) Math.floor(value), 99), 1);
    }

    private Component generateTiersTiers() {
        Div tiersContent = new Div();
        tiersContent.setId("premium-tiers");
        for (SubTier tier : SubTier.values()) {
            tiersContent.add(generateTiersCard(tier));
        }
        return tiersContent;
    }

    private Component generateTiersCard(SubTier tier) {
        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.addClassNames("tier-card");
        content.setId("card" + tier.ordinal());
        if (tier.isRecommended()) {
            content.getStyle().set("border-color", "rgb(var(--warning-color-rgb))");
        }

        Icon icon = tier.getVaadinIcon().create();
        icon.setSize("64px");
        icon.getStyle().set("margin-top", "24px")
                .set("margin-bottom", "12px");
        content.add(icon);

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setPadding(false);
        titleLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        H2 title = new H2(getTranslation("premium.tier." + tier.name()));
        title.getStyle().set("margin-top", "4px")
                .set("margin-bottom", "4px")
                .set("font-size", "125%");
        titleLayout.add(title);

        if (tier.isRecommended()) {
            Span recommended = new Span(getTranslation("premium.tier.recommended").toUpperCase());
            recommended.getStyle().set("margin-top", "0")
                    .set("margin-bottom", "0")
                    .set("margin-left", "8px")
                    .set("font-size", "75%")
                    .set("background", "rgb(var(--warning-color-rgb))")
                    .set("padding", "0 5px")
                    .set("border-radius", "4px")
                    .set("color", "var(--lumo-shade)");
            titleLayout.add(recommended);
        }
        content.add(titleLayout);

        HorizontalLayout priceLayout = new HorizontalLayout();
        priceLayout.setAlignItems(Alignment.END);
        priceLayout.setPadding(false);
        priceLayout.setSpacing(false);
        priceLayout.getStyle().set("margin", "0");

        H2 price = new H2("");
        price.getStyle().set("margin", "0")
                .set("font-size", "225%")
                .set("text-align", "center");
        priceTextMap.put(tier, price);
        priceLayout.add(price);

        Span period = new Span("");
        period.getStyle().set("margin", "0 0 0 0.3em")
                .set("color", "var(--secondary-text-color)")
                .set("text-align", "center");
        pricePeriodTextMap.put(tier, period);
        priceLayout.add(period);
        content.add(priceLayout, generateButtonSeparator());

        switch (tier) {
            case PRO -> {
                proQuantityNumberField = generateQuantityNumberField(SubTier.PRO.getIds().length);
                content.add(proQuantityNumberField);
            }
            case PRO_PLUS -> {
                proPlusQuantityNumberField = generateQuantityNumberField(SubTier.PRO_PLUS.getIds().length);
                content.add(proPlusQuantityNumberField);
            }
        }

        Span desc = new Span(getTranslation("premium.desc." + tier.name()));
        desc.getStyle().set("text-align", "center");
        content.add(generateBuyButton(tier), desc, generateButtonSeparator(), generateTierPerks(tier));
        return content;
    }

    private NumberField generateQuantityNumberField(int size) {
        NumberField quantityNumberField = new NumberField();
        quantityNumberField.getStyle().set("margin-top", "-6px");
        quantityNumberField.setWidthFull();
        quantityNumberField.setValue(1d);
        quantityNumberField.setStepButtonsVisible(true);
        quantityNumberField.setMin(1);
        quantityNumberField.setMax(size);
        quantityNumberField.setStep(1);
        quantityNumberField.setLabel(getTranslation("premium.servers"));
        quantityNumberField.addValueChangeListener(e -> {
            int value = extractValueFromQuantity(e.getValue());
            quantityNumberField.setValue((double) value);
            refreshPremiumTiers();
        });
        return quantityNumberField;
    }

    private Component generateBuyButton(SubTier tier) {
        Button buyButton = new Button(getTranslation("premium.buy"), new PatreonIcon());
        buyButton.setWidthFull();
        buyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buyButton.setHeight("43px");

        Anchor a = new Anchor("", buyButton);
        a.setWidthFull();
        a.setTarget("_blank");
        buttonAnchorMap.put(tier, a);
        return a;
    }

    private Component generateButtonSeparator() {
        Hr hr = new Hr();
        hr.setWidthFull();
        hr.getStyle().set("margin-top", "16px");
        return hr;
    }


    private Component generateTierPerks(SubTier tier) {
        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setPadding(false);
        content.addClassName("tier-perks-layout");
        content.getStyle().set("margin-bottom", "24px")
                .set("margin-top", "16px");

        String[] perks = getTranslation("premium.perks." + tier.name(), StringUtil.numToString(countPremiumCommands())).split("\n");
        for (int i = 0; i < perks.length; i++) {
            String perk = perks[i];
            Icon icon = VaadinIcon.CHECK_CIRCLE.create();
            icon.addClassName("prop-check");
            String linkUrl = null;
            if (i == 2 && tier == SubTier.PRO) {
                linkUrl = ExternalLinks.LAWLIET_PREMIUM_COMMANDS;
            } else if (i == 2 && tier == SubTier.PRO_PLUS) {
                linkUrl = ExternalLinks.LAWLIET_WEB_DASHBOARD;
            } else if (i == 3 && tier == SubTier.BASIC) {
                linkUrl = ExternalLinks.LAWLIET_DEVELOPMENT_VOTES;
            } else if (i == 4 && tier == SubTier.BASIC) {
                linkUrl = ExternalLinks.LAWLIET_FEATURE_REQUESTS;
            }

            String[] subTexts = null;
            if (i == 2 && tier == SubTier.BASIC) {
                subTexts = getTranslation("premium.perks.autofeatures").split("\n");
            } else if (i == 3 && tier == SubTier.PRO) {
                subTexts = getTranslation("premium.perks.premiumfeatures").split("\n");
            }

            content.add(generateTierPerk(icon, perk, linkUrl, subTexts));
        }
        if (tier == SubTier.BASIC) {
            Icon icon = VaadinIcon.CLOSE_CIRCLE.create();
            icon.addClassName("prop-notcheck");
            content.add(generateTierPerk(icon, getTranslation("premium.perks.BASIC.notpremium")));
        }
        return content;
    }

    private int countPremiumCommands() {
        try {
            return CommandListContainer.getInstance().getCategories().stream()
                    .filter(c -> c.getId().equals("patreon_only"))
                    .mapToInt(category -> (int) category.getSlots().size())
                    .sum();
        } catch (Throwable e) {
            LOGGER.error("Error", e);
            return -1;
        }
    }

    private Component generateTierPerk(Icon icon, String text) {
        return generateTierPerk(icon, text, null);
    }

    private Component generateTierPerk(Icon icon, String text, String[] subTexts) {
        return generateTierPerk(icon, text, null, subTexts);
    }

    private Component generateTierPerk(Icon icon, String text, String linkUrl, String[] subTexts) {
        FlexLayout content = new FlexLayout();
        content.setFlexDirection(FlexLayout.FlexDirection.ROW);
        content.add(icon, new Text(text));
        content.getStyle().set("color", "var(--lumo-body-text-color)");

        if (linkUrl != null) {
            Anchor a = new Anchor(linkUrl, content);
            a.setWidthFull();
            a.setTarget("_blank");
            return a;
        } else {
            if (subTexts != null) {
                UnorderedList unorderedList = new UnorderedList();
                for (String subText : subTexts) {
                    ListItem item = new ListItem(subText);
                    item.addClassName("premium-entry");
                    unorderedList.add(item);
                }

                AccordionPanel accordionPanel = new AccordionPanel(content, unorderedList);
                accordionPanel.addThemeVariants(DetailsVariant.LUMO_REVERSE);
                accordionPanel.getStyle()
                        .set("width", "100%")
                        .set("border", "0");
                return accordionPanel;
            } else {
                return content;
            }
        }
    }

}
