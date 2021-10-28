package xyz.lawlietbot.spring.frontend.views;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import bell.oauth.discord.domain.Guild;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.ExternalLinks;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.Redirector;
import xyz.lawlietbot.spring.backend.commandlist.CommandListContainer;
import xyz.lawlietbot.spring.backend.commandlist.CommandListSlot;
import xyz.lawlietbot.spring.backend.payment.*;
import xyz.lawlietbot.spring.backend.premium.UserPremium;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.backend.util.StringUtil;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.Card;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.syncserver.SendEvent;

@Route(value = "premium", layout = MainLayout.class)
@CssImport("./styles/premium.css")
@NoLiteAccess
public class PremiumView extends PageLayout implements HasUrlParameter<String> {

    private final static Logger LOGGER = LoggerFactory.getLogger(PremiumView.class);

    private final VerticalLayout mainContent = new VerticalLayout();
    private final ArrayList<Card> cards = new ArrayList<>();
    private final HashMap<Integer, ComboBox<Guild>> comboBoxMap = new HashMap<>();
    private final ConfirmationDialog dialog = new ConfirmationDialog();
    private final Select<SubDuration> durationSelect = new Select<>();
    private final Select<SubCurrency> currencySelect = new Select<>();
    private List<Subscription> userSubscriptions = Collections.emptyList();
    private ArrayList<Guild> availableGuilds;
    private UserPremium userPremium;
    private Div tiersContent = new Div();
    private boolean slotsBuild = false;

    public PremiumView(@Autowired SessionData sessionData, @Autowired UIData uiData) {
        super(sessionData, uiData);
        add(new PageHeader(getUiData(), getTitleText(), getTranslation("premium.desc"), getRoute()), dialog);

        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.setPadding(true);

        mainContent.add(generateTiers());
        add(mainContent);
    }

    private Component generateTiers() {
        VerticalLayout premiumContent = new VerticalLayout();
        premiumContent.setWidthFull();
        premiumContent.setPadding(false);
        premiumContent.getStyle().set("margin-bottom", "48px");

        premiumContent.add(generateTiersTitle(), generateSeparator(), generateTiersTiers());
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

    private Component generateSeparator() {
        Hr hr = new Hr();
        hr.setWidthFull();
        hr.getStyle().set("margin-bottom", "-4px");
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
        currencySelect.addValueChangeListener(e -> setTiers());
        currencySelect.setMaxWidth("90px");
        currencySelect.getStyle().set("margin-right", "12px");
        content.add(currencySelect);

        durationSelect.setItemLabelGenerator((ItemLabelGenerator<SubDuration>) duration -> getTranslation("premium.duration." + duration.name()));
        durationSelect.setItems(SubDuration.values());
        durationSelect.setValue(SubDuration.MONTHLY);
        durationSelect.addValueChangeListener(e -> setTiers());
        durationSelect.setMaxWidth("150px");
        content.add(durationSelect);

        return content;
    }

    private Component generateTiersTiers() {
        tiersContent = new Div();
        tiersContent.setId("premium-tiers");
        return tiersContent;
    }

    private void setTiers() {
        tiersContent.removeAll();
        for (SubLevel level : SubLevel.getSubLevelsOfCurrency(currencySelect.getValue())) {
            tiersContent.add(generateTiersCard(durationSelect.getValue(), level));
        }
    }

    private Component generateTiersCard(SubDuration duration, SubLevel level) {
        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.addClassNames("tier-card");

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setPadding(false);
        titleLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        H2 title = new H2(getTranslation("premium.tier." + level.getSubLevelType().name()));
        title.getStyle().set("margin-top", "0")
                .set("margin-bottom", "0");
        titleLayout.add(title);

        if (level.getSubLevelType() == SubLevelType.PRO) {
            Span recommended = new Span(getTranslation("premium.tier.recommended").toUpperCase());
            recommended.getStyle().set("margin-top", "0")
                    .set("margin-bottom", "0")
                    .set("margin-left", "8px")
                    .set("font-size", "75%")
                    .set("background", "#f8b84e")
                    .set("padding", "0 5px")
                    .set("border-radius", "4px")
                    .set("color", "var(--lumo-shade)");
            titleLayout.add(recommended);
        }

        String priceString = SubscriptionUtil.generatePriceString(SubscriptionUtil.getPrice(duration, level));
        Text price = new Text(getTranslation("premium.price." + level.getSubLevelType().name(), duration == SubDuration.YEARLY, level.getCurrency().getSymbol(), priceString));
        Div div = new Div();

        content.add(titleLayout, price, generateTierPerks(level), div, generateButtonSeparator(), generateBuyLayout(duration, level));
        content.setFlexGrow(1, div);
        return content;
    }

    private Component generateTierPerks(SubLevel level) {
        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setPadding(false);
        content.getStyle().set("margin-top", "32px");
        for (String perk : getTranslation("premium.perks." + level.getSubLevelType().name(), StringUtil.numToString(countPremiumCommands())).split("\n")) {
            Icon icon = VaadinIcon.CHECK_CIRCLE.create();
            icon.addClassName("prop-check");
            content.add(generateTierPerk(icon, perk));
        }
        if (level.getSubLevelType() == SubLevelType.BASIC) {
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

    private Component generateBuyLayout(SubDuration duration, SubLevel level) {
        VerticalLayout controlLayout = new VerticalLayout();
        controlLayout.setPadding(false);
        controlLayout.setWidthFull();
        controlLayout.getStyle().set("margin-bottom", "24px");

        HorizontalLayout quantityLayout = new HorizontalLayout();
        quantityLayout.setPadding(false);
        quantityLayout.setSpacing(false);
        quantityLayout.setWidthFull();
        quantityLayout.setAlignItems(FlexComponent.Alignment.END);
        quantityLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        quantityLayout.getStyle().set("margin-top", "0");

        String priceString = SubscriptionUtil.generatePriceString(SubscriptionUtil.getPrice(duration, level));
        Text priceText = new Text(getTranslation("premium.price.BASIC", duration == SubDuration.YEARLY, level.getCurrency().getSymbol(), priceString));

        NumberField quantity = new NumberField();
        quantity.getStyle().set("margin-top", "-6px");
        quantity.setValue(1d);
        quantity.setHasControls(true);
        quantity.setMin(1);
        quantity.setMax(99);
        quantity.setStep(1);
        quantity.setLabel(getTranslation("premium.servers"));
        quantity.addValueChangeListener(e -> {
            int value = extractValueFromQuantity(e.getValue());
            quantity.setValue((double) value);
            String totalPriceString = SubscriptionUtil.generatePriceString(SubscriptionUtil.getPrice(duration, level) * value);
            priceText.setText(getTranslation("premium.price.BASIC", duration == SubDuration.YEARLY, level.getCurrency().getSymbol(), totalPriceString));
        });

        Button buyButton = new Button(getTranslation("premium.buy"));
        buyButton.setWidthFull();
        buyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buyButton.setHeight("43px");
        buyButton.getStyle().set("margin-bottom", "-4px");
        if (getSessionData().isLoggedIn()) {
            buyButton.setEnabled(level.getSubLevelType() == SubLevelType.PRO || userSubscriptions.isEmpty());
        } else {
            buyButton.setText(getTranslation("category.discordlogin"));
        }
        buyButton.addClickListener(e -> {
            DiscordUser discordUser = getSessionData().getDiscordUser().orElse(null);
            if (discordUser != null) {
                try {
                    int value = extractValueFromQuantity(quantity.getValue());
                    String sessionUrl = StripeManager.generateSession(duration, level, ExternalLinks.LAWLIET_PREMIUM, discordUser.getId(), value);
                    new Redirector().redirect(sessionUrl);
                } catch (Exception ex) {
                    LOGGER.error("Exception", ex);
                    CustomNotification.showError(getTranslation("error"));
                }
            } else {
                new Redirector().redirect(getSessionData().getLoginUrl());
            }
        });

        quantityLayout.add(quantity, priceText);
        if (level.getSubLevelType() == SubLevelType.PRO) {
            controlLayout.add(quantityLayout);
        }
        controlLayout.add(buyButton);
        if (!getSessionData().isLoggedIn()) {
            Span notLoggedIn = new Span(getTranslation("premium.notloggedin"));
            notLoggedIn.getStyle().set("color", "var(--lumo-error-text-color)")
                    .set("margin-bottom", "-8px");
            controlLayout.add(notLoggedIn);
        }
        return controlLayout;
    }

    private int extractValueFromQuantity(Double value) {
        value = value != null ? value : 0;
        return Math.max(Math.min((int) Math.floor(value), 99), 1);
    }

    private int countPremiumCommands() {
        return CommandListContainer.getInstance().getCategories().stream()
                .mapToInt(category -> (int) category.getSlots().stream().filter(CommandListSlot::isPatreonOnly).count())
                .sum();
    }

    private Component generateTierPerk(Icon icon, String text) {
        FlexLayout content = new FlexLayout();
        content.setFlexDirection(FlexLayout.FlexDirection.ROW);
        content.add(icon, new Text(text));
        return content;
    }

    private Component generatePremium() {
        VerticalLayout premiumContent = new VerticalLayout();
        premiumContent.setWidthFull();
        premiumContent.setPadding(false);
        premiumContent.getStyle().set("margin-bottom", "120px");

        premiumContent.add(generatePremiumTitle(), generatePremiumSubtitle());
        for (int i = 0; i < userPremium.getSlots().size(); i++) {
            premiumContent.add(generatePremiumSlot(i));
        }

        return premiumContent;
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
            horizontalLayout.setFlexGrow(1, label);

            ComboBox<Guild> guildComboBox = new ComboBox<>();
            guildComboBox.setItemLabelGenerator((ItemLabelGenerator<Guild>) Guild::getName);
            guildComboBox.setPlaceholder(getTranslation("premium.server"));
            guildComboBox.setItems(availableGuilds);
            horizontalLayout.add(guildComboBox);
            comboBoxMap.put(i, guildComboBox);

            Button button = new Button(VaadinIcon.PLUS.create());
            button.addClickListener(e -> {
                if (guildComboBox.getValue() != null) {
                    onAdd(guildComboBox.getValue(), i);
                }
            });
            button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            horizontalLayout.add(button);
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
            dialog.open(getTranslation("premium.confirm"), () -> {
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
            boolean success = SendEvent.sendModifyPremium(userId, slot, guildId).get();
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
        if (parametersMap != null && parametersMap.containsKey("session_id")) {
            String sessionId = parametersMap.get("session_id").get(0);
            UI.getCurrent().getPage().getHistory().replaceState(null, getRoute());
            try {
                StripeManager.registerSubscription(Session.retrieve(sessionId));
                ConfirmationDialog confirmationDialog = new ConfirmationDialog();
                confirmationDialog.open(getTranslation("premium.buy.success"), () -> {
                });
                add(confirmationDialog);
            } catch (StripeException e) {
                LOGGER.error("Could not update subscription", e);
                CustomNotification.showError(getTranslation("error"));
            }
        }

        SessionData sessionData = getSessionData();
        if (!slotsBuild) {
            slotsBuild = true;
            if (sessionData.getDiscordUser().map(DiscordUser::hasGuilds).orElse(false)) {
                try {
                    DiscordUser discordUser = sessionData.getDiscordUser().get();
                    this.userSubscriptions = StripeManager.retrieveActiveSubscriptions(discordUser.getId());
                    this.userPremium = SendEvent.sendRequestUserPremium(discordUser.getId()).get(5, TimeUnit.SECONDS);
                    this.availableGuilds = new ArrayList<>(discordUser.getGuilds());
                    if (userPremium.getSlots().size() > 0) {
                        mainContent.addComponentAsFirst(generatePremium());
                    }

                    StripeManager.retrieveCustomer(discordUser.getId())
                            .ifPresent(customer -> {
                                currencySelect.setValue(SubCurrency.getFromCurrency(customer.getCurrency()));
                                currencySelect.setVisible(false);
                            });
                } catch (InterruptedException | ExecutionException | TimeoutException | StripeException e) {
                    LOGGER.error("Could not load slots", e);
                    CustomNotification.showError(getTranslation("error"));
                }
            }
            setTiers();
        }
    }

}
