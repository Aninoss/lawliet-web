package xyz.lawlietbot.spring.frontend.components.premium;

import bell.oauth.discord.domain.Guild;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.lawlietbot.spring.backend.premium.UserPremium;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.Card;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.frontend.components.GuildComboBox;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PremiumUnlockServersSection extends VerticalLayout {

    private final static Logger LOGGER = LoggerFactory.getLogger(PremiumUnlockServersSection.class);

    private final SessionData sessionData;
    private final ConfirmationDialog dialog;
    private final VerticalLayout mainLayout = new VerticalLayout();
    private final ArrayList<Card> cards = new ArrayList<>();
    private final HashMap<Integer, GuildComboBox> comboBoxMap = new HashMap<>();
    private ArrayList<Guild> availableGuilds;
    private UserPremium userPremium;

    public PremiumUnlockServersSection(SessionData sessionData, ConfirmationDialog dialog) {
        this.sessionData = sessionData;
        this.dialog = dialog;
        setPadding(true);
        addClassName("section");
        add(generateMainContent());
        update();
    }

    public Component generateMainContent() {
        mainLayout.setPadding(false);
        mainLayout.addClassName(Styles.APP_WIDTH);
        return mainLayout;
    }

    private void update() {
        mainLayout.removeAll();
        mainLayout.add(generateTitle());

        if (sessionData.getDiscordUser().map(DiscordUser::hasGuilds).orElse(false)) {
            try {
                DiscordUser discordUser = sessionData.getDiscordUser().get();
                this.userPremium = SendEvent.send(EventOut.PREMIUM, Map.of("user_id", discordUser.getId()))
                        .thenApply(jsonResponse -> {
                            ArrayList<Long> slots = new ArrayList<>();
                            ArrayList<Long> slotsPlus = new ArrayList<>();

                            JSONArray jsonSlots = jsonResponse.getJSONArray("slots");
                            for (int i = 0; i < jsonSlots.length(); i++) {
                                slots.add(jsonSlots.getLong(i));
                            }

                            JSONArray jsonSlotsPlus = jsonResponse.getJSONArray("slots_plus");
                            for (int i = 0; i < jsonSlotsPlus.length(); i++) {
                                slotsPlus.add(jsonSlotsPlus.getLong(i));
                            }

                            return new UserPremium(discordUser.getId(), slots, slotsPlus);
                        })
                        .get(5, TimeUnit.SECONDS);
                this.availableGuilds = new ArrayList<>(discordUser.getGuilds());
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                LOGGER.error("Could not load slots", e);
                CustomNotification.showError(getTranslation("error"));
            }
        }

        mainLayout.add(generateDescription());
        if (sessionData.isLoggedIn()) {
            mainLayout.add(generateHelp());
        }
        if (userPremium != null) {
            if (!userPremium.getSlots().isEmpty() || !userPremium.getSlotsPlus().isEmpty()) {
                int index = 0;
                for (int i = 0; i < userPremium.getSlots().size(); i++) {
                    mainLayout.add(generatePremiumSlot(userPremium.getSlots(), false, index++, i));
                }
                for (int i = 0; i < userPremium.getSlotsPlus().size(); i++) {
                    mainLayout.add(generatePremiumSlot(userPremium.getSlotsPlus(), true, index++, i));
                }
            } else {
                mainLayout.add(generateNoPremiumCard(getTranslation("premium.slots.noslots"), false));
            }
        } else {
            mainLayout.add(generateNoPremiumCard(getTranslation("logout.status"), true));
        }
    }

    private Component generateTitle() {
        String text = getTranslation("premium.step3");
        H2 title = new H2(text);
        title.addClassName("section-title");
        return title;
    }

    private Component generateDescription() {
        Div div = new Div(getTranslation("premium.subtitle"));
        div.getStyle().set("margin-top", "0")
                .set("margin-bottom", "var(--lumo-space-m)");
        return div;
    }

    private Component generateHelp() {
        Div div = new Div(getTranslation("premium.help"));
        div.getStyle().set("margin-top", "0")
                .set("margin-bottom", "var(--lumo-space-m)");
        return div;
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

        Div label = new Div(text);
        horizontalLayout.add(label);
        horizontalLayout.setFlexGrow(1, label);

        if (withLoginButton) {
            Button login = new Button(getTranslation("login"));
            login.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            Anchor loginAnchor = new Anchor(sessionData.getLoginUrl(), login);
            horizontalLayout.add(loginAnchor);
        }

        return horizontalLayout;
    }

    private Component generatePremiumSlot(ArrayList<Long> slots, boolean plus, int index, int slotPosition) {
        long guildId = slots.get(slotPosition);
        Guild guild = sessionData.getDiscordUser().map(u -> u.getGuildById(guildId)).orElse(null);
        if (guild == null && guildId != 0) {
            guild = new Guild();
            guild.setId(guildId);
            guild.setName(String.format("%X", guildId));
        }

        Card card = new Card();
        card.setWidthFull();
        card.setHeight("72px");
        card.getStyle().set("margin-bottom", "-8px");

        card.add(generateCardContent(slots, plus, guild, index, slotPosition, true));
        cards.add(card);
        return card;
    }

    private HorizontalLayout generateCardContent(ArrayList<Long> slots, boolean plus, Guild guild, int index, int slotPosition, boolean init) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setPadding(true);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        String subscriptionName = plus ? getTranslation("premium.tier.PRO_PLUS") : getTranslation("premium.tier.PRO");
        if (guild == null) {
            Div label = new Div(subscriptionName);
            horizontalLayout.add(label);

            HorizontalLayout guildLayout = new HorizontalLayout();
            guildLayout.setPadding(false);
            guildLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            guildLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

            GuildComboBox guildComboBox = new GuildComboBox(getTranslation("premium.server"));
            guildComboBox.getStyle().set("max-width", "300px");
            guildComboBox.setItems(availableGuilds);
            guildLayout.add(guildComboBox);
            comboBoxMap.put(index, guildComboBox);

            Button button = new Button(VaadinIcon.PLUS.create());
            button.addClickListener(e -> {
                if (guildComboBox.getValue() != null) {
                    onAdd(slots, plus, guildComboBox.getValue(), index, slotPosition);
                }
            });
            button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            guildLayout.add(button);
            guildLayout.setFlexGrow(1, guildComboBox);

            horizontalLayout.add(guildLayout);
            horizontalLayout.setFlexGrow(1, guildLayout);
        } else {
            comboBoxMap.remove(index);
            availableGuilds.remove(guild);
            if (guild.getIcon() != null) {
                Image guildIcon = new Image(guild.getIcon(), "Server Icon");
                guildIcon.setHeightFull();
                guildIcon.addClassName(Styles.ROUND);
                horizontalLayout.add(guildIcon);
            }

            Div label = new Div(subscriptionName + ": " + guild.getName());
            horizontalLayout.add(label);
            horizontalLayout.setFlexGrow(1, label);

            Button button = new Button(getTranslation("premium.remove"), VaadinIcon.CLOSE_SMALL.create());
            button.setEnabled(init);
            button.addThemeVariants(ButtonVariant.LUMO_ERROR);
            button.addClickListener(e -> onRemove(slots, plus, index, slotPosition));
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

    private void onAdd(ArrayList<Long> slots, boolean plus, Guild guild, int index, int slotPosition) {
        if (!dialog.isOpened()) {
            Span outerSpan = new Span(getTranslation("premium.confirm") + " ");
            outerSpan.setWidthFull();
            outerSpan.getStyle().set("color", "black");
            Span innerSpan = new Span(getTranslation("premium.confirm.warning"));
            innerSpan.getStyle().set("color", "var(--lumo-error-color)");
            outerSpan.add(innerSpan);

            dialog.open(outerSpan, () -> {
                long guildId = guild.getId();
                if (modify(plus, slotPosition, guildId)) {
                    availableGuilds.remove(guild);
                    slots.set(slotPosition, guildId);
                    Card card = cards.get(index);
                    card.removeAll();
                    card.add(generateCardContent(slots, plus, guild, index, slotPosition, false));
                    refreshComboBoxes();
                }
            }, () -> {
            });
        }
    }

    private void onRemove(ArrayList<Long> slots, boolean plus, int index, int slotPosition) {
        if (modify(plus, slotPosition, 0)) {
            long guildId = slots.get(slotPosition);
            sessionData.getDiscordUser().map(u -> u.getGuildById(guildId))
                    .ifPresent(guild -> availableGuilds.add(guild));
            slots.set(slotPosition, 0L);

            Card card = cards.get(index);
            card.removeAll();
            card.add(generateCardContent(slots, plus,null, index, slotPosition, false));
            refreshComboBoxes();
        }
    }

    private boolean modify(boolean plus, int slotPosition, long guildId) {
        try {
            long userId = userPremium.getUserId();

            JSONObject json = new JSONObject();
            json.put("user_id", userId);
            json.put("slot", slotPosition);
            json.put("guild_id", guildId);
            json.put("plus", plus);

            boolean success = SendEvent.send(EventOut.PREMIUM_MODIFY, json)
                    .thenApply(r -> {
                        try {
                            return r.getBoolean("success");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .get();
            if (success) {
                if (guildId != 0) {
                    CustomNotification.showSuccess(getTranslation("premium.success", sessionData.getDiscordUser().get().getGuildById(guildId).getName()));
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
