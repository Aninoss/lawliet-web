package xyz.lawlietbot.spring.frontend.components.premium;

import bell.oauth.discord.domain.Guild;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

public class PremiumUnlockPage extends PremiumPage {

    private final static Logger LOGGER = LoggerFactory.getLogger(PremiumUnlockPage.class);

    private final SessionData sessionData;
    private final ConfirmationDialog dialog;
    private final ArrayList<Card> cards = new ArrayList<>();
    private final HashMap<Integer, GuildComboBox> comboBoxMap = new HashMap<>();
    private ArrayList<Guild> availableGuilds;
    private UserPremium userPremium;

    public PremiumUnlockPage(SessionData sessionData, ConfirmationDialog dialog) {
        this.sessionData = sessionData;
        this.dialog = dialog;

        setPadding(true);
        getStyle().set("margin-top", "16px");
    }

    @Override
    public void build() {
        update();
    }

    public void update() {
        removeAll();

        if (sessionData.getDiscordUser().map(DiscordUser::hasGuilds).orElse(false)) {
            try {
                DiscordUser discordUser = sessionData.getDiscordUser().get();
                UserPremium userPremium = SendEvent.send(EventOut.PREMIUM, Map.of("user_id", discordUser.getId()))
                        .thenApply(jsonResponse -> {
                            ArrayList<Long> slots = new ArrayList<>();
                            try {
                                JSONArray jsonSlots = jsonResponse.getJSONArray("slots");
                                for (int i = 0; i < jsonSlots.length(); i++) {
                                    slots.add(jsonSlots.getLong(i));
                                }

                                return new UserPremium(discordUser.getId(), slots);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .get(5, TimeUnit.SECONDS);
                this.userPremium = userPremium;
                this.availableGuilds = new ArrayList<>(discordUser.getGuilds());
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                LOGGER.error("Could not load slots", e);
                CustomNotification.showError(getTranslation("error"));
            }
        }

        add(generatePremiumSubtitle());
        if (userPremium != null) {
            if (!userPremium.getSlots().isEmpty()) {
                for (int i = 0; i < userPremium.getSlots().size(); i++) {
                    add(generatePremiumSlot(i));
                }
            } else {
                add(generateNoPremiumCard(getTranslation("premium.slots.noslots"), false));
            }
        } else {
            add(generateNoPremiumCard(getTranslation("logout.status"), true));
        }
    }

    private Component generatePremiumSubtitle() {
        Paragraph p = new Paragraph(getTranslation("premium.subtitle"));
        p.getStyle().set("margin-top", "0");
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
            Anchor loginAnchor = new Anchor(sessionData.getLoginUrl(), login);
            horizontalLayout.add(loginAnchor);
        }

        return horizontalLayout;
    }

    private Component generatePremiumSlot(int i) {
        long guildId = userPremium.getSlots().get(i);
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
            label.addClassName(Styles.VISIBLE_NOT_SMALL);
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
            sessionData.getDiscordUser().map(u -> u.getGuildById(guildId))
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
