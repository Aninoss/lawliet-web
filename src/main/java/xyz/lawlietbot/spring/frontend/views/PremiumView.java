package xyz.lawlietbot.spring.frontend.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import bell.oauth.discord.domain.Guild;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.ExternalLinks;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.Redirector;
import xyz.lawlietbot.spring.backend.UICache;
import xyz.lawlietbot.spring.backend.commandlist.CommandListContainer;
import xyz.lawlietbot.spring.backend.commandlist.CommandListSlot;
import xyz.lawlietbot.spring.backend.payment.SubCurrency;
import xyz.lawlietbot.spring.backend.payment.SubDuration;
import xyz.lawlietbot.spring.backend.payment.SubLevel;
import xyz.lawlietbot.spring.backend.payment.SubscriptionUtil;
import xyz.lawlietbot.spring.backend.payment.paddle.PaddleAPI;
import xyz.lawlietbot.spring.backend.payment.paddle.PaddleManager;
import xyz.lawlietbot.spring.backend.payment.stripe.StripeManager;
import xyz.lawlietbot.spring.backend.premium.UserPremium;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.backend.util.StringUtil;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.*;
import xyz.lawlietbot.spring.frontend.components.premium.PaddlePopup;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

@Route(value = "premium", layout = MainLayout.class)
@CssImport("./styles/premium.css")
@JavaScript("https://cdn.paddle.com/paddle/paddle.js")
@NoLiteAccess
public class PremiumView extends PageLayout implements HasUrlParameter<String> {

    private final static Logger LOGGER = LoggerFactory.getLogger(PremiumView.class);

    private final VerticalLayout mainContent = new VerticalLayout();
    private final ArrayList<Card> cards = new ArrayList<>();
    private final HashMap<Integer, GuildComboBox> comboBoxMap = new HashMap<>();
    private final ConfirmationDialog dialog = new ConfirmationDialog();
    private final Select<SubDuration> durationSelect = new Select<>();
    private final Select<SubCurrency> currencySelect = new Select<>();
    private ArrayList<Guild> availableGuilds;
    private UserPremium userPremium;
    private boolean slotsBuild = false;
    private NumberField quantityNumberField;
    private Text proBuyPriceText;
    private final Map<SubLevel, Text> priceTextMap = new HashMap<>();
    private VerticalLayout preselectGuildsLayout;
    private HorizontalLayout yearlySuggestionField;

    public PremiumView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);
        add(new PageHeader(getUiData(), getTitleText(), getTranslation("premium.desc"), getRoute()), dialog);

        mainContent.setPadding(false);
        mainContent.setSpacing(false);
        mainContent.add(generateTiers());
        add(mainContent);

        refreshPremiumTiers();
    }

    private Component generateTiers() {
        VerticalLayout premiumContent = new VerticalLayout();
        premiumContent.setWidthFull();
        premiumContent.setPadding(true);
        premiumContent.addClassName(Styles.APP_WIDTH);
        premiumContent.getStyle().set("margin-bottom", "48px");

        premiumContent.add(generateTiersTitle(), generateTiersSubtitle(), generateYearlySuggestionField(), generateSeparator(), generateTiersTiers());
        return premiumContent;
    }

    private Component generateTiersTitle() {
        HorizontalLayout content = new HorizontalLayout();
        content.setWidthFull();
        content.setSpacing(false);
        content.setPadding(false);
        content.getStyle().set("margin-top", "12px");
        content.setAlignItems(FlexComponent.Alignment.END);
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        content.add(generateTiersTitleText(), generateTiersTitleDuration());
        return content;
    }

    private Component generateTiersSubtitle() {
        Span span = new Span(getTranslation("premium.tiers.subtitle"));
        span.setWidthFull();
        span.getStyle().set("margin-bottom", "16px");
        return span;
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
        if (!getSessionData().isLoggedIn()) {
            yearlySuggestionField.getStyle().set("display", "none");
        }

        return yearlySuggestionField;
    }

    private Component generateSeparator() {
        Hr hr = new Hr();
        hr.setWidthFull();
        hr.getStyle().set("margin-bottom", "12px");
        return hr;
    }

    private Component generateTiersTitleText() {
        H2 title = new H2(getTranslation("premium.tiers.title"));
        title.getStyle().set("margin-top", "0")
                .set("margin-bottom", "0");
        return title;
    }

    private Component generateTiersTitleDuration() {
        HorizontalLayout content = new HorizontalLayout();
        content.setSpacing(false);
        content.setPadding(false);

        currencySelect.setItemLabelGenerator((ItemLabelGenerator<SubCurrency>) Enum::name);
        currencySelect.setItems(SubCurrency.values());
        currencySelect.setValue(SubCurrency.retrieveDefaultCurrency(UI.getCurrent().getSession().getBrowser().getAddress()));
        currencySelect.addValueChangeListener(e -> refreshPremiumTiers());
        currencySelect.setMaxWidth("90px");
        currencySelect.getStyle().set("margin-right", "12px");
        content.add(currencySelect);

        durationSelect.setItemLabelGenerator((ItemLabelGenerator<SubDuration>) duration -> getTranslation("premium.duration." + duration.name()));
        durationSelect.setItems(SubDuration.values());
        durationSelect.setValue(SubDuration.MONTHLY);
        durationSelect.addValueChangeListener(e -> {
            if (e.getValue() == SubDuration.YEARLY) {
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

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setPadding(false);
        titleLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        H2 title = new H2(getTranslation("premium.tier." + level.name()));
        title.getStyle().set("margin-top", "0")
                .set("margin-bottom", "0");
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

        Text price = new Text("");
        priceTextMap.put(level, price);
        Div div = new Div();

        content.add(titleLayout, price, generateTierPerks(level), div, generateButtonSeparator(), generateBuyLayout(level));
        content.setFlexGrow(1, div);
        return content;
    }

    private Component generateTierPerks(SubLevel level) {
        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setPadding(false);
        content.getStyle().set("margin-top", "32px");
        String[] perks = getTranslation("premium.perks." + level.name(), StringUtil.numToString(countPremiumCommands())).split("\n");
        for (int i = 0; i < perks.length; i++) {
            String perk = perks[i];
            Icon icon = VaadinIcon.CHECK_CIRCLE.create();
            icon.addClassName("prop-check");
            String linkUrl = i == 1 && level == SubLevel.PRO
                    ? ExternalLinks.LAWLIET_PREMIUM_COMMANDS
                    : null;
            content.add(generateTierPerk(icon, perk, linkUrl));
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
        hr.getStyle().set("margin-bottom", "4px");
        return hr;
    }

    private Component generateBuyLayout(SubLevel level) {
        boolean loggedIn = getSessionData().isLoggedIn();

        VerticalLayout controlLayout = new VerticalLayout();
        controlLayout.setPadding(false);
        controlLayout.setWidthFull();
        controlLayout.getStyle().set("margin-bottom", "24px");

        Button buyButton = new Button(getTranslation("premium.buy"));
        buyButton.setWidthFull();
        buyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buyButton.setHeight("43px");
        buyButton.getStyle().set("margin-bottom", "-4px");
        if (!loggedIn) {
            buyButton.setText(getTranslation("login"));
        }
        buyButton.addClickListener(e -> {
            DiscordUser discordUser = getSessionData().getDiscordUser().orElse(null);
            if (discordUser != null) {
                try {
                    int value = extractValueFromQuantity(quantityNumberField.getValue());
                    List<Long> presetGuildIds = preselectGuildsLayout.getChildren()
                            .map(c -> (GuildComboBox) c)
                            .filter(g -> g.getValue() != null)
                            .map(g -> g.getValue().getId())
                            .collect(Collectors.toList());

                    PaddlePopup paddlePopup = new PaddlePopup(durationSelect.getValue(), level, discordUser, value, presetGuildIds);
                    add(paddlePopup);
                    UICache.put(discordUser.getId(), UI.getCurrent());
                } catch (Exception ex) {
                    LOGGER.error("Exception", ex);
                    CustomNotification.showError(getTranslation("error"));
                }
            } else {
                new Redirector().redirect(getSessionData().getLoginUrl());
            }
        });

        if (level == SubLevel.PRO && loggedIn) {
            proBuyPriceText = new Text("");

            quantityNumberField = new NumberField();
            quantityNumberField.getStyle().set("margin-top", "-6px");
            quantityNumberField.setValue(1d);
            quantityNumberField.setHasControls(true);
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
            quantityLayout.add(quantityNumberField, proBuyPriceText);

            preselectGuildsLayout = new VerticalLayout();
            preselectGuildsLayout.setPadding(false);
            preselectGuildsLayout.setMaxHeight("350px");
            preselectGuildsLayout.getStyle().set("margin-bottom", "8px")
                    .set("overflow-y", "auto");
            preselectGuildsLayout.add(generatePreselectGuildComboBox(0));

            controlLayout.add(quantityLayout, preselectGuildsLayout, generateButtonSeparator());
        }

        controlLayout.add(buyButton);
        if (loggedIn) {
            Span manageSubscriptionLink = generateManageSubscriptionsLink();
            manageSubscriptionLink.setWidthFull();
            controlLayout.add(manageSubscriptionLink);
        } else {
            Span notLoggedIn = new Span(getTranslation("premium.notloggedin"));
            notLoggedIn.getStyle().set("color", "var(--lumo-error-text-color)")
                    .set("margin-bottom", "-8px");
            controlLayout.add(notLoggedIn);
        }
        return controlLayout;
    }

    private Span generateManageSubscriptionsLink() {
        Span manageSubscriptions = new Span(getTranslation("premium.managesubs"));
        manageSubscriptions.getStyle().set("text-align", "center")
                .set("text-decoration", "underline")
                .set("margin-bottom", "-8px")
                .set("cursor", "pointer");
        manageSubscriptions.addClickListener(e -> UI.getCurrent().navigate(ManageSubscriptionsView.class));
        return manageSubscriptions;
    }

    private GuildComboBox generatePreselectGuildComboBox(int i) {
        GuildComboBox guildComboBox = new GuildComboBox();
        guildComboBox.setItems(getSessionData().getDiscordUser().get().getGuilds());
        guildComboBox.setWidthFull();
        guildComboBox.setLabel(getTranslation("premium.preselect.label", StringUtil.numToString(i + 1)));
        guildComboBox.setPlaceholder(getTranslation("premium.preselect.placeholder"));
        guildComboBox.setClearButtonVisible(true);
        guildComboBox.getStyle().set("margin-top", "0");
        return guildComboBox;
    }

    private void refreshPremiumTiers() {
        SubDuration duration = durationSelect.getValue();
        SubCurrency subCurrency = currencySelect.getValue();

        for (SubLevel subLevel : SubLevel.values()) {
            String priceString = SubscriptionUtil.generatePriceString(SubscriptionUtil.getPrice(duration, subLevel, subCurrency));
            priceTextMap.get(subLevel)
                    .setText(getTranslation("premium.price." + subLevel.name(), duration == SubDuration.YEARLY, subCurrency.getSymbol(), priceString));
        }

        if (getSessionData().isLoggedIn()) {
            int quantity = extractValueFromQuantity(quantityNumberField.getValue());

            String proTotalPriceString = SubscriptionUtil.generatePriceString(SubscriptionUtil.getPrice(duration, SubLevel.PRO, subCurrency) * quantity);
            proBuyPriceText.setText(getTranslation("premium.price.BASIC", duration == SubDuration.YEARLY, subCurrency.getSymbol(), proTotalPriceString));

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
                    .mapToInt(category -> (int) category.getSlots().stream().filter(CommandListSlot::isPatreonOnly).count())
                    .sum();
        } catch (Throwable e) {
            LOGGER.error("Error", e);
            return -1;
        }
    }

    private Component generateTierPerk(Icon icon, String text) {
        return generateTierPerk(icon, text, null);
    }

    private Component generateTierPerk(Icon icon, String text, String linkUrl) {
        FlexLayout content = new FlexLayout();
        content.setFlexDirection(FlexLayout.FlexDirection.ROW);
        content.add(icon, new Text(text));

        if (linkUrl != null) {
            Anchor a = new Anchor(linkUrl, content);
            a.setWidthFull();
            a.setTarget("_blank");
            return a;
        } else {
            return content;
        }
    }

    private Component generatePremium() {
        Div premiumSegment = new Div();
        premiumSegment.setWidthFull();
        premiumSegment.getStyle().set("background", "var(--lumo-secondary)");

        VerticalLayout premiumContent = new VerticalLayout();
        premiumContent.addClassName(Styles.APP_WIDTH);
        premiumContent.setPadding(true);
        premiumContent.getStyle().set("margin-top", "48px")
                .set("margin-bottom", "56px");

        premiumContent.add(generatePremiumTitle(), generatePremiumSubtitle());
        if (userPremium != null) {
            if (userPremium.getSlots().size() > 0) {
                for (int i = 0; i < userPremium.getSlots().size(); i++) {
                    premiumContent.add(generatePremiumSlot(i));
                }
            } else {
                premiumContent.add(generateNoPremiumCard(getTranslation("premium.slots.noslots"), false));
            }
            Span manageSubscriptionLink = generateManageSubscriptionsLink();
            manageSubscriptionLink.getStyle().set("margin-top", "24px");
            premiumContent.add(manageSubscriptionLink);
        } else {
            premiumContent.add(generateNoPremiumCard(getTranslation("logout.status"), true));
        }

        premiumSegment.add(premiumContent);
        return premiumSegment;
    }

    private Component generatePremiumTitle() {
        H2 title = new H2(getTranslation("premium.title"));
        title.getStyle().set("margin-top", "8px");
        return title;
    }

    private Component generatePremiumSubtitle() {
        Paragraph p = new Paragraph(getTranslation("premium.subtitle"));
        p.getStyle().set("margin-bottom", "26px")
                .set("margin-top", "0");
        return p;
    }

    private Component generateNoPremiumCard(String text, boolean withLoginButton) {
        Card card = new Card();
        card.setWidthFull();
        card.setHeight("72px");
        card.getStyle().set("margin-bottom", "-8px");

        card.add(generateNoPremiumCardContent(text, withLoginButton));
        cards.add(card);
        return card;
    }

    private Component generateNoPremiumCardContent(String text, boolean withLoginButton) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setPadding(true);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Label label = new Label(text);
        horizontalLayout.add(label);
        horizontalLayout.setFlexGrow(1, label);

        if (withLoginButton) {
            Button login = new Button(getTranslation("login"));
            login.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            Anchor loginAnchor = new Anchor(getSessionData().getLoginUrl(), login);
            horizontalLayout.add(loginAnchor);
        }

        return horizontalLayout;
    }

    private Component generatePremiumSlot(int i) {
        long guildId = userPremium.getSlots().get(i);
        Guild guild = getSessionData().getDiscordUser().map(u -> u.getGuildById(guildId)).orElse(null);
        if (guild == null && guildId != 0) {
            guild = new Guild();
            guild.setId(guildId);
            guild.setName(String.format("%X", guildId));
        }

        Card card = new Card();
        card.setWidthFull();
        card.setHeight("72px");
        card.getStyle().set("margin-bottom", "-8px");

        card.add(generateCardContent(guild, i, true));
        cards.add(card);
        return card;
    }

    private HorizontalLayout generateCardContent(Guild guild, int i, boolean init) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setPadding(true);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        if (guild == null) {
            Label label = new Label(getTranslation("premium.notset"));
            horizontalLayout.add(label);

            HorizontalLayout guildLayout = new HorizontalLayout();
            guildLayout.setPadding(false);
            guildLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            guildLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

            GuildComboBox guildComboBox = new GuildComboBox();
            guildComboBox.getStyle().set("max-width", "300px");
            guildComboBox.setItems(availableGuilds);
            guildLayout.add(guildComboBox);
            comboBoxMap.put(i, guildComboBox);

            Button button = new Button(VaadinIcon.PLUS.create());
            button.addClickListener(e -> {
                if (guildComboBox.getValue() != null) {
                    onAdd(guildComboBox.getValue(), i);
                }
            });
            button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            guildLayout.add(button);
            guildLayout.setFlexGrow(1, guildComboBox);

            horizontalLayout.add(guildLayout);
            horizontalLayout.setFlexGrow(1, guildLayout);
        } else {
            comboBoxMap.remove(i);
            availableGuilds.remove(guild);
            if (guild.getIcon() != null) {
                Image guildIcon = new Image(guild.getIcon(), "Server Icon");
                guildIcon.setHeightFull();
                guildIcon.addClassName(Styles.ROUND);
                horizontalLayout.add(guildIcon);
            }

            Label label = new Label(guild.getName());
            horizontalLayout.add(label);
            horizontalLayout.setFlexGrow(1, label);

            Button button = new Button(getTranslation("premium.remove"), VaadinIcon.CLOSE_SMALL.create());
            button.setEnabled(init);
            button.addThemeVariants(ButtonVariant.LUMO_ERROR);
            button.addClickListener(e -> onRemove(i));
            horizontalLayout.add(button);
        }

        return horizontalLayout;
    }

    private void refreshComboBoxes() {
        comboBoxMap.values().forEach(c -> {
            if (c.getValue() != null && !availableGuilds.contains(c.getValue())) {
                c.setValue(null);
            }
            c.getDataProvider().refreshAll();
        });
    }

    private void onAdd(Guild guild, int i) {
        if (!dialog.isOpened()) {
            Span outerSpan = new Span(getTranslation("premium.confirm") + " ");
            outerSpan.setWidthFull();
            outerSpan.getStyle().set("color", "black");
            Span innerSpan = new Span(getTranslation("premium.confirm.warning"));
            innerSpan.getStyle().set("color", "var(--lumo-error-text-color)");
            outerSpan.add(innerSpan);

            dialog.open(outerSpan, () -> {
                long guildId = guild.getId();
                if (modify(i, guildId)) {
                    availableGuilds.remove(guild);
                    userPremium.setSlot(i, guildId);
                    Card card = cards.get(i);
                    card.removeAll();
                    card.add(generateCardContent(guild, i, false));
                    refreshComboBoxes();
                }
            }, () -> {
            });
        }
    }

    private void onRemove(int i) {
        if (modify(i, 0)) {
            long guildId = userPremium.getSlots().get(i);
            getSessionData().getDiscordUser().map(u -> u.getGuildById(guildId))
                    .ifPresent(guild -> availableGuilds.add(guild));
            userPremium.setSlot(i, 0);

            Card card = cards.get(i);
            card.removeAll();
            card.add(generateCardContent(null, i, false));
            refreshComboBoxes();
        }
    }

    private boolean modify(int slot, long guildId) {
        try {
            long userId = userPremium.getUserId();

            JSONObject json = new JSONObject();
            json.put("user_id", userId);
            json.put("slot", slot);
            json.put("guild_id", guildId);

            boolean success = SendEvent.send(EventOut.PREMIUM_MODIFY, json)
                    .thenApply(r -> r.getBoolean("success"))
                    .get();
            if (success) {
                if (guildId != 0) {
                    CustomNotification.showSuccess(getTranslation("premium.success", getSessionData().getDiscordUser().get().getGuildById(guildId).getName()));
                }
                return true;
            } else {
                CustomNotification.showError(getTranslation("premium.cooldown"));
                return false;
            }
        } catch (Throwable e) {
            LOGGER.error("Could not modify premium", e);
            CustomNotification.showError(getTranslation("error"));
            return false;
        }
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        if (parametersMap != null) {
            if (parametersMap.containsKey("session_id")) {
                String sessionId = parametersMap.get("session_id").get(0);
                UI.getCurrent().getPage().getHistory().replaceState(null, getRoute());
                try {
                    Session session = Session.retrieve(sessionId);
                    StripeManager.registerSubscription(session);

                    boolean unlockServers = Boolean.parseBoolean(session.getMetadata().getOrDefault("unlock_servers", "false"));
                    String dialogText = unlockServers ? "premium.buy.success.pro" : "premium.buy.success";
                    ConfirmationDialog confirmationDialog = new ConfirmationDialog();
                    confirmationDialog.open(getTranslation(dialogText), () -> {
                    });
                    add(confirmationDialog);
                } catch (StripeException e) {
                    LOGGER.error("Could not update subscription", e);
                    CustomNotification.showError(getTranslation("error"));
                }
            }

            if (parametersMap.containsKey("paddle")) {
                String checkoutId = parametersMap.get("paddle").get(0);
                UI.getCurrent().getPage().getHistory().replaceState(null, getRoute());
                try {
                    PaddleManager.waitForCheckoutAsync(checkoutId).get(1, TimeUnit.MINUTES);

                    JSONObject checkout = PaddleAPI.retrieveCheckout(checkoutId);
                    int planId = checkout.getJSONObject("order").getInt("product_id");
                    SubLevel subLevel = PaddleManager.getSubLevelType(planId);

                    String dialogText = subLevel == SubLevel.PRO ? "premium.buy.success.pro" : "premium.buy.success";
                    ConfirmationDialog confirmationDialog = new ConfirmationDialog();
                    confirmationDialog.open(getTranslation(dialogText), () -> {
                    });
                    add(confirmationDialog);
                } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
                    LOGGER.error("Could not load subscription", e);
                    CustomNotification.showError(getTranslation("error"));
                }
            }
        }

        SessionData sessionData = getSessionData();
        if (!slotsBuild) {
            slotsBuild = true;
            if (sessionData.getDiscordUser().map(DiscordUser::hasGuilds).orElse(false)) {
                try {
                    DiscordUser discordUser = sessionData.getDiscordUser().get();
                    this.userPremium = SendEvent.send(EventOut.PREMIUM, Map.of("user_id", discordUser.getId()))
                            .thenApply(jsonResponse -> {
                                ArrayList<Long> slots = new ArrayList<>();
                                JSONArray jsonSlots = jsonResponse.getJSONArray("slots");
                                for (int i = 0; i < jsonSlots.length(); i++) {
                                    slots.add(jsonSlots.getLong(i));
                                }

                                return new UserPremium(discordUser.getId(), slots);
                            })
                            .get(5, TimeUnit.SECONDS);
                    this.availableGuilds = new ArrayList<>(discordUser.getGuilds());
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    LOGGER.error("Could not load slots", e);
                    CustomNotification.showError(getTranslation("error"));
                }
            }
            this.mainContent.add(generatePremium());
        }
    }

}
