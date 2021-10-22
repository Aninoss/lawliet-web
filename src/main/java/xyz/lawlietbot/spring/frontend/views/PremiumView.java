package xyz.lawlietbot.spring.frontend.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import bell.oauth.discord.domain.Guild;
import com.vaadin.flow.component.*;
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
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.LoginAccess;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.commandlist.CommandListContainer;
import xyz.lawlietbot.spring.backend.commandlist.CommandListSlot;
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
@LoginAccess(withGuilds = true)
public class PremiumView extends PageLayout {

    private final static Logger LOGGER = LoggerFactory.getLogger(PremiumView.class);

    private enum Duration { MONTHLY, YEARLY }

    private enum Tier { LITE, PRO }

    private final ArrayList<Card> cards = new ArrayList<>();
    private final HashMap<Integer, ComboBox<Guild>> comboBoxMap = new HashMap<>();
    private final ConfirmationDialog dialog = new ConfirmationDialog(getTranslation("premium.confirm"));
    private ArrayList<Guild> availableGuilds;
    private UserPremium userPremium;
    private Div tiersContent = new Div();

    public PremiumView(@Autowired SessionData sessionData, @Autowired UIData uiData) throws InterruptedException, ExecutionException, TimeoutException {
        super(sessionData, uiData);
        getStyle().set("margin-bottom", "48px");

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.setPadding(true);
        mainContent.add(dialog);

        if (sessionData.getDiscordUser().map(DiscordUser::hasGuilds).orElse(false)) {
            this.userPremium = SendEvent.sendRequestUserPremium(sessionData.getDiscordUser().get().getId()).get(5, TimeUnit.SECONDS);
            this.availableGuilds = new ArrayList<>(sessionData.getDiscordUser().get().getGuilds());
            if (userPremium.getSlots().size() > 0) {
                mainContent.add(generatePremium());
            }
        }
        mainContent.add(generateTiers());

        add(
                new PageHeader(getUiData(), getTitleText(), getTranslation("premium.desc"), getRoute()),
                mainContent
        );
    }

    private Component generateTiers() {
        VerticalLayout premiumContent = new VerticalLayout();
        premiumContent.setWidthFull();
        premiumContent.setPadding(false);

        premiumContent.add(generateTiersTitle(), generateSeparator(), generateTiersTiers());
        return premiumContent;
    }

    private Component generateTiersTitle() {
        HorizontalLayout content = new HorizontalLayout();
        content.setWidthFull();
        content.setSpacing(false);
        content.setPadding(false);
        content.getStyle().set("margin-top", "8px");
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
        Select<Duration> durationSelect = new Select<>();
        durationSelect.setItemLabelGenerator((ItemLabelGenerator<Duration>) duration -> getTranslation("premium.duration." + duration.name()));
        durationSelect.setItems(Duration.values());
        durationSelect.setValue(Duration.MONTHLY);
        durationSelect.addValueChangeListener(e -> setTiers(e.getValue()));
        return durationSelect;
    }

    private Component generateTiersTiers() {
        tiersContent = new Div();
        tiersContent.setId("premium-tiers");
        setTiers(Duration.MONTHLY);
        return tiersContent;
    }

    private void setTiers(Duration duration) {
        tiersContent.removeAll();
        for (Tier tier : Tier.values()) {
            tiersContent.add(generateTiersCard(duration, tier));
        }
    }

    private Component generateTiersCard(Duration duration, Tier tier) {
        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.addClassNames("tier-card");

        H2 title = new H2(getTranslation("premium.tier." + tier.name()));
        title.getStyle().set("margin-top", "14px")
                .set("margin-bottom", "0");
        Text price = new Text(getTranslation("premium.price." + tier.name(), duration == Duration.YEARLY, getPrice(duration, tier)));
        Div div = new Div();

        content.add(title, price, generateTierPerks(tier), div, generateButtonSeparator(), generateBuyLayout(duration, tier));
        content.setFlexGrow(1, div);
        return content;
    }

    private Component generateTierPerks(Tier tier) {
        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setPadding(false);
        content.getStyle().set("margin-top", "32px");
        for (String perk : getTranslation("premium.perks." + tier.name(), StringUtil.numToString(countPremiumCommands())).split("\n")) {
            Icon icon = VaadinIcon.CHECK_CIRCLE.create();
            icon.addClassName("prop-check");
            content.add(generateTierPerk(icon, perk));
        }
        if (tier == Tier.LITE) {
            Icon icon = VaadinIcon.CLOSE_CIRCLE.create();
            icon.addClassName("prop-notcheck");
            content.add(generateTierPerk(icon, getTranslation("premium.perks.LITE.notpremium")));
        }
        return content;
    }

    private Component generateButtonSeparator() {
        Hr hr = new Hr();
        hr.setWidthFull();
        hr.getStyle().set("margin-bottom", "4px");
        return hr;
    }

    private Component generateBuyLayout(Duration duration, Tier tier) {
        Div outerDiv = new Div();
        outerDiv.setWidthFull();

        Div paypalDiv = new Div();
        paypalDiv.setWidthFull();
        paypalDiv.setId("buy-button-" + duration.name() + "-" + tier.name());

        Html script = new Html("<script src=\"https://www.paypal.com/sdk/js?client-id=AdfrVhawnCJKYyeqRrXv_a-sz9-ylfW8Db4uBZFDSXeOcOYJ6eh42mpeKVxJDNGLlarx7wc61q53LuEi&vault=true&intent=subscription\" data-sdk-integration-source=\"button-factory\"></script>");
        outerDiv.add(paypalDiv, script, generatePreBuyLayout(outerDiv, duration, tier, paypalDiv));
        return outerDiv;
    }

    private Component generatePreBuyLayout(Div outerDiv, Duration duration, Tier tier, Div paypalDiv) {
        VerticalLayout controlLayout = new VerticalLayout();
        controlLayout.setPadding(false);
        controlLayout.setWidthFull();
        controlLayout.getStyle().set("margin-top", "0")
                .set("margin-bottom", "24px");

        HorizontalLayout quantityLayout = new HorizontalLayout();
        quantityLayout.setPadding(false);
        quantityLayout.setSpacing(false);
        quantityLayout.setWidthFull();
        quantityLayout.setAlignItems(FlexComponent.Alignment.END);
        quantityLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        quantityLayout.getStyle().set("margin-top", "0");

        Text price = new Text(getTranslation("premium.price.LITE", duration == Duration.YEARLY, getPrice(duration, tier)));

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
            price.setText(getTranslation("premium.price.LITE", duration == Duration.YEARLY, getPrice(duration, tier) * value));
        });

        Button buyButton = new Button(getTranslation("premium.buy"), VaadinIcon.CART.create());
        buyButton.setWidthFull();
        buyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buyButton.setHeight("43px");
        buyButton.getStyle().set("margin-bottom", "-4px");
        buyButton.addClickListener(e -> {
            int value = extractValueFromQuantity(quantity.getValue());
            outerDiv.remove(controlLayout);
            Span text = new Span(getTranslation("premium.changequantity"));
            text.setWidthFull();
            text.addClassName("change-quantities");
            text.addClickListener(e2 -> {
                outerDiv.remove(text);
                paypalDiv.removeAll();
                outerDiv.add(generatePreBuyLayout(outerDiv, duration, tier, paypalDiv));
            });
            outerDiv.addComponentAsFirst(text);
            UI.getCurrent().getPage().executeJs("showPayPalButtons($0, $1, $2, $3)",
                    "P-2ND832908M568673AMFZMTQA",
                    value,
                    "#" + paypalDiv.getId().orElse(""),
                    String.valueOf(getSessionData().getDiscordUser().get().getId())
            );
        });

        quantityLayout.add(quantity, price);
        if (tier == Tier.PRO) {
            controlLayout.add(quantityLayout);
        }
        controlLayout.add(buyButton);
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

    private int getPrice(Duration duration, Tier tier) {
        int price = tier == Tier.LITE ? 3 : 5;
        if (duration == Duration.YEARLY) {
            price *= 10;
        }
        return price;
    }

    private Component generatePremium() {
        VerticalLayout premiumContent = new VerticalLayout();
        premiumContent.setWidthFull();
        premiumContent.setPadding(false);
        premiumContent.getStyle().set("margin-bottom", "56px");

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
            dialog.setConfirmListener(() -> {
                long guildId = guild.getId();
                if (modify(i, guildId)) {
                    availableGuilds.remove(guild);
                    userPremium.setSlot(i, guildId);
                    Card card = cards.get(i);
                    card.removeAll();
                    card.add(generateCardContent(guild, i, false));
                    refreshComboBoxes();
                }
            });
            dialog.open();
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

}
