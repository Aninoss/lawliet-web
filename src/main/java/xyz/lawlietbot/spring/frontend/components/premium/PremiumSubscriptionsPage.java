package xyz.lawlietbot.spring.frontend.components.premium;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.server.VaadinRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.lawlietbot.spring.ExternalLinks;
import xyz.lawlietbot.spring.backend.Redirector;
import xyz.lawlietbot.spring.backend.UICache;
import xyz.lawlietbot.spring.backend.commandlist.CommandListContainer;
import xyz.lawlietbot.spring.backend.payment.Currency;
import xyz.lawlietbot.spring.backend.payment.SubDuration;
import xyz.lawlietbot.spring.backend.payment.SubLevel;
import xyz.lawlietbot.spring.backend.payment.paddle.PaddleManager;
import xyz.lawlietbot.spring.backend.payment.paddle.PaddlePriceOverview;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.util.StringUtil;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.frontend.components.GuildComboBox;
import xyz.lawlietbot.spring.frontend.views.PremiumView;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PremiumSubscriptionsPage extends PremiumPage {

    public final static boolean PRICE_TESTING = Boolean.parseBoolean(System.getenv("PADDLE_PRICE_TESTING"));
    private final static Logger LOGGER = LoggerFactory.getLogger(PremiumSubscriptionsPage.class);
    private final static HashSet<Long> visitedUserIds = new HashSet<>();

    private final SessionData sessionData;
    private final ConfirmationDialog dialog;
    private final PremiumView premiumView;
    private final Select<SubDuration> durationSelect = new Select<>();
    private NumberField quantityNumberField;
    private final Map<SubLevel, H2> priceTextMap = new HashMap<>();
    private final Map<SubLevel, Span> previousPriceTextMap = new HashMap<>();
    private final Map<SubLevel, Span> pricePeriodTextMap = new HashMap<>();
    private VerticalLayout preselectGuildsLayout;
    private HorizontalLayout yearlySuggestionField;
    private int group = 0;

    public PremiumSubscriptionsPage(SessionData sessionData, ConfirmationDialog dialog, PremiumView premiumView) {
        this.sessionData = sessionData;
        this.dialog = dialog;
        this.premiumView = premiumView;
        setPadding(true);

        if (PRICE_TESTING && sessionData.isLoggedIn()) {
            long userId = sessionData.getDiscordUser().get().getId();
            group = (int) ((userId >> 22) % 2);
            if (visitedUserIds.add(userId)) {
                LOGGER.info("First premium page visit of an user (group {})", group);
            }
        }
    }

    @Override
    public void build() {
        if (System.getenv("PADDLE_SALE_CODE") != null) {
            add(premiumView.generateCouponField());
        } else {
            add(generateYearlySuggestionField());
        }
        if (!PRICE_TESTING || sessionData.isLoggedIn()) {
            add(generateTiersCurrencyDurationField());
        }
        add(generateTiersTiers());
        refreshPremiumTiers();
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
        switchButton.addClickListener(e -> durationSelect.setValue(SubDuration.YEARLY));
        content.add(switchButton);

        yearlySuggestionField.add(content);
        if (!sessionData.isLoggedIn()) {
            yearlySuggestionField.getStyle().set("display", "none");
        }

        return yearlySuggestionField;
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
            if (e.getValue() == SubDuration.YEARLY && yearlySuggestionField != null) {
                yearlySuggestionField.getStyle().set("display", "none");
            }
            refreshPremiumTiers();
        });
        durationSelect.setMaxWidth("150px");
        content.add(durationSelect);

        return content;
    }

    private Component generateTiersTiers() {
        Div tiersContent = new Div();
        tiersContent.setId("premium-tiers");
        for (SubLevel level : SubLevel.values()) {
            tiersContent.add(generateTiersCard(level));
        }
        return tiersContent;
    }

    private Component generateTiersCard(SubLevel level) {
        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.addClassNames("tier-card");
        content.setId("card" + level.ordinal());
        if (level.isRecommended()) {
            content.getStyle().set("border-color", "rgb(var(--warning-color-rgb))");
        }

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setPadding(false);
        titleLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        H2 title = new H2(getTranslation("premium.tier." + level.name()));
        title.getStyle().set("margin-top", "4px")
                .set("margin-bottom", "4px")
                .set("font-size", "125%");
        titleLayout.add(title);

        if (level.isRecommended()) {
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

        Icon icon = level.getVaadinIcon().create();
        icon.setSize("64px");
        icon.getStyle().set("margin-bottom", "24px")
                .set("margin-top", "24px");
        content.add(icon);

        if (!PRICE_TESTING || sessionData.isLoggedIn()) {
            HorizontalLayout priceLayout = new HorizontalLayout();
            priceLayout.setAlignItems(Alignment.END);
            priceLayout.setPadding(false);
            priceLayout.setSpacing(false);
            priceLayout.getStyle().set("margin", "0");

            H2 price = new H2("");
            price.getStyle().set("margin", "0")
                    .set("font-size", "225%")
                    .set("text-align", "center");
            priceTextMap.put(level, price);
            priceLayout.add(price);

            Span previousPrice = new Span("");
            previousPrice.addClassName("previous-price");
            previousPrice.getStyle()
                    .set("font-size", "150%")
                    .setMarginLeft("8px")
                    .set("text-align", "center");
            previousPrice.setVisible(false);
            previousPriceTextMap.put(level, previousPrice);
            priceLayout.add(previousPrice);
            content.add(priceLayout);

            Span period = new Span("");
            period.getStyle().set("margin", "0")
                    .set("color", "var(--secondary-text-color)")
                    .set("text-align", "center");
            pricePeriodTextMap.put(level, period);
            content.add(period);
        }
        content.add(generateBuyButton(level));

        Span desc = new Span(getTranslation("premium.desc." + level.name()));
        desc.getStyle().set("text-align", "center");
        content.add(desc, generateButtonSeparator());

        if (level == SubLevel.PRO && sessionData.isLoggedIn()) {
            content.add(generateQuantityLayout());
        }

        content.add(generateTierPerks(level));
        return content;
    }

    private Component generateTierPerks(SubLevel level) {
        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setPadding(false);
        content.addClassName("tier-perks-layout");
        content.getStyle().set("margin-bottom", "24px")
                .set("margin-top", "16px");

        String[] perks = getTranslation("premium.perks." + level.name(), StringUtil.numToString(countPremiumCommands())).split("\n");
        for (int i = 0; i < perks.length; i++) {
            String perk = perks[i];
            Icon icon = VaadinIcon.CHECK_CIRCLE.create();
            icon.addClassName("prop-check");
            String linkUrl = null;
            if (i == 2 && level == SubLevel.PRO) {
                linkUrl = ExternalLinks.LAWLIET_PREMIUM_COMMANDS;
            } else if (i == 3 && level == SubLevel.BASIC) {
                linkUrl = ExternalLinks.LAWLIET_DEVELOPMENT_VOTES;
            } else if (i == 4 && level == SubLevel.BASIC) {
                linkUrl = ExternalLinks.LAWLIET_FEATURE_REQUESTS;
            }

            String[] subTexts = null;
            if (i == 2 && level == SubLevel.BASIC) {
                subTexts = getTranslation("premium.perks.autofeatures").split("\n");
            } else if (i == 3 && level == SubLevel.PRO) {
                subTexts = getTranslation("premium.perks.premiumfeatures").split("\n");
            }

            content.add(generateTierPerk(icon, perk, linkUrl, subTexts));
        }
        if (level == SubLevel.BASIC) {
            Icon icon = VaadinIcon.CLOSE_CIRCLE.create();
            icon.addClassName("prop-notcheck");
            content.add(generateTierPerk(icon, getTranslation("premium.perks.BASIC.notpremium")));
        }
        return content;
    }

    private Component generateButtonSeparator() {
        Hr hr = new Hr();
        hr.setWidthFull();
        hr.getStyle().set("margin-top", "16px");
        return hr;
    }

    private Component generateBuyButton(SubLevel level) {
        boolean loggedIn = sessionData.isLoggedIn();

        Button buyButton = new Button(getTranslation("premium.buy"));
        buyButton.setWidthFull();
        buyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buyButton.setHeight("43px");
        buyButton.getStyle().set("margin-top", "24px");

        if (level.buyDirectly()) {
            if (!loggedIn) {
                buyButton.setText(getTranslation(PRICE_TESTING ? "premium.buylogin.testing" : "premium.buylogin"));
            }
            buyButton.addClickListener(e -> {
                DiscordUser discordUser = sessionData.getDiscordUser().orElse(null);
                if (discordUser != null) {
                    try {
                        int value = extractValueFromQuantity(quantityNumberField.getValue());
                        List<Long> presetGuildIds = preselectGuildsLayout.getChildren()
                                .map(c -> (GuildComboBox) c)
                                .filter(g -> g.getValue() != null)
                                .map(g -> g.getValue().getId())
                                .collect(Collectors.toList());

                        PaddleManager.openPopup(durationSelect.getValue(), level, discordUser, value, presetGuildIds, getLocale(), group);
                        UICache.put(discordUser.getId(), UI.getCurrent());
                    } catch (Exception ex) {
                        LOGGER.error("Exception", ex);
                        CustomNotification.showError(getTranslation("error"));
                    }
                } else {
                    new Redirector().redirect(sessionData.getLoginUrl());
                }
            });
        } else {
            buyButton.setText(getTranslation("premium.contact"));
            buyButton.addClickListener(e -> {
                Label text1 = new Label(getTranslation("premium.contact.message"));
                text1.setWidthFull();
                text1.getStyle().set("color", "black");

                Label text2 = new Label(getTranslation("premium.contact.messagejoin"));
                text2.setWidthFull();
                text2.getStyle().set("color", "black");

                VerticalLayout layout = new VerticalLayout(text1, text2);
                layout.setPadding(false);
                layout.setSpacing(true);

                dialog.open(layout, () -> {
                    new Redirector().redirect(ExternalLinks.SERVER_INVITE_URL);
                }, () -> {
                });
            });
        }
        return buyButton;
    }

    private Component generateQuantityLayout() {
        VerticalLayout controlLayout = new VerticalLayout();
        controlLayout.setPadding(false);
        controlLayout.setWidthFull();

        quantityNumberField = new NumberField();
        quantityNumberField.getStyle().set("margin-top", "-6px");
        quantityNumberField.setValue(1d);
        quantityNumberField.setStepButtonsVisible(true);
        quantityNumberField.setMin(1);
        quantityNumberField.setMax(99);
        quantityNumberField.setStep(1);
        quantityNumberField.setLabel(getTranslation("premium.servers"));
        quantityNumberField.addValueChangeListener(e -> {
            int value = extractValueFromQuantity(e.getValue());
            quantityNumberField.setValue((double) value);
            refreshPremiumTiers();
        });

        HorizontalLayout quantityLayout = new HorizontalLayout();
        quantityLayout.setPadding(false);
        quantityLayout.setSpacing(false);
        quantityLayout.setWidthFull();
        quantityLayout.setAlignItems(FlexComponent.Alignment.END);
        quantityLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        quantityLayout.getStyle().set("margin-top", "0");
        quantityLayout.add(quantityNumberField);

        preselectGuildsLayout = new VerticalLayout();
        preselectGuildsLayout.setPadding(false);
        preselectGuildsLayout.setMaxHeight("350px");
        preselectGuildsLayout.getStyle().set("margin-bottom", "4px")
                .set("overflow-y", "auto");
        preselectGuildsLayout.add(generatePreselectGuildComboBox(0));

        controlLayout.add(quantityLayout, preselectGuildsLayout, generateButtonSeparator());
        return controlLayout;
    }

    private GuildComboBox generatePreselectGuildComboBox(int i) {
        GuildComboBox guildComboBox = new GuildComboBox();
        guildComboBox.setItems(sessionData.getDiscordUser().get().getGuilds());
        guildComboBox.setWidthFull();
        guildComboBox.setLabel(getTranslation("premium.preselect.label", StringUtil.numToString(i + 1)));
        guildComboBox.setPlaceholder(getTranslation("premium.preselect.placeholder"));
        guildComboBox.setClearButtonVisible(true);
        guildComboBox.getStyle().set("margin-top", "0");
        return guildComboBox;
    }

    private void refreshPremiumTiers() {
        SubDuration duration = durationSelect.getValue();
        PaddlePriceOverview paddlePriceOverview = PaddleManager.retrieveSubscriptionPrices(VaadinRequest.getCurrent().getHeader("CF-Connecting-IP"), group);
        Currency currency = paddlePriceOverview.getCurrency();
        Map<String, PaddlePriceOverview.Price> priceMap = paddlePriceOverview.getPrices();

        for (SubLevel subLevel : priceTextMap.keySet()) {
            long planId = PaddleManager.getPlanId(duration, subLevel, group);

            PaddlePriceOverview.Price price = priceMap.get(String.valueOf(planId));
            double currentPrice = price.getCurrentPrice();
            double previousPrice = price.getPreviousPrice();
            if (subLevel == SubLevel.PRO && quantityNumberField != null) {
                currentPrice *= quantityNumberField.getValue();
                previousPrice *= quantityNumberField.getValue();
            }

            String currentPriceString = NumberFormat.getCurrencyInstance(getLocale())
                    .format(currentPrice)
                    .replace("¤", currency.getSymbol());
            String previousPriceString = NumberFormat.getCurrencyInstance(getLocale())
                    .format(previousPrice)
                    .replace("¤", currency.getSymbol());

            if (currentPriceString.equals(previousPriceString)) {
                priceTextMap.get(subLevel)
                        .setText(subLevel == SubLevel.ULTIMATE ? getTranslation("premium.price.ultimate", currentPriceString) : currentPriceString);
                previousPriceTextMap.get(subLevel)
                        .setVisible(false);
                pricePeriodTextMap.get(subLevel)
                        .setText(getTranslation(price.getIncludesVat() ? "premium.priceperiod.includesvat" : "premium.priceperiod", duration == SubDuration.YEARLY));
            } else {
                priceTextMap.get(subLevel)
                        .setText(subLevel == SubLevel.ULTIMATE ? getTranslation("premium.price.ultimate", currentPriceString) : currentPriceString);
                pricePeriodTextMap.get(subLevel)
                        .setText(getTranslation(price.getIncludesVat() ? "premium.priceperiod.sale.includesvat" : "premium.priceperiod.sale", duration == SubDuration.YEARLY, previousPriceString));

                Span previousPriceText = previousPriceTextMap.get(subLevel);
                previousPriceText.setVisible(true);
                previousPriceText.setText(previousPriceString);
            }
        }

        if (sessionData.isLoggedIn()) {
            int quantity = extractValueFromQuantity(quantityNumberField.getValue());

            int previousQuantity = (int) preselectGuildsLayout.getChildren().count();
            if (quantity > previousQuantity) {
                for (int i = previousQuantity; i < quantity; i++) {
                    preselectGuildsLayout.add(generatePreselectGuildComboBox(i));
                }
            } else if (quantity < previousQuantity) {
                preselectGuildsLayout.getChildren()
                        .skip(quantity)
                        .forEach(c -> preselectGuildsLayout.remove(c));
            }
        }
    }

    private int extractValueFromQuantity(Double value) {
        value = value != null ? value : 0;
        return Math.max(Math.min((int) Math.floor(value), 99), 1);
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
                accordionPanel.addThemeVariants(DetailsVariant.REVERSE);
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
