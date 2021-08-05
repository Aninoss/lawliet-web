package xyz.lawlietbot.spring.frontend.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import bell.oauth.discord.domain.Guild;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.lawlietbot.spring.ExternalLinks;
import xyz.lawlietbot.spring.LoginAccess;
import xyz.lawlietbot.spring.NoLiteAccess;
import xyz.lawlietbot.spring.backend.premium.UserPremium;
import xyz.lawlietbot.spring.backend.userdata.DiscordUser;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.Styles;
import xyz.lawlietbot.spring.frontend.components.Card;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.CustomNotification;
import xyz.lawlietbot.spring.frontend.components.PageHeader;
import xyz.lawlietbot.spring.frontend.layouts.MainLayout;
import xyz.lawlietbot.spring.frontend.layouts.PageLayout;
import xyz.lawlietbot.spring.syncserver.SendEvent;

@Route(value = "premium", layout = MainLayout.class)
@NoLiteAccess
@LoginAccess(withGuilds = true)
public class PremiumView extends PageLayout {

    private final static Logger LOGGER = LoggerFactory.getLogger(PremiumView.class);

    private final VerticalLayout mainContent = new VerticalLayout();
    private final ArrayList<Card> cards = new ArrayList<>();
    private final HashMap<Integer, ComboBox<Guild>> comboBoxMap = new HashMap<>();
    private final ConfirmationDialog dialog = new ConfirmationDialog(getTranslation("premium.confirm"));
    private ArrayList<Guild> availableGuilds;
    private UserPremium userPremium;

    public PremiumView(@Autowired SessionData sessionData, @Autowired UIData uiData) throws InterruptedException, ExecutionException, TimeoutException {
        super(sessionData, uiData);
        getStyle().set("margin-bottom", "48px");

        mainContent.addClassName(Styles.APP_WIDTH);
        mainContent.setPadding(true);
        mainContent.add(dialog);

        if (sessionData.isLoggedIn() && sessionData.getDiscordUser().get().hasGuilds()) {
            this.userPremium = SendEvent.sendRequestUserPremium(sessionData.getDiscordUser().get().getId()).get(5, TimeUnit.SECONDS);
            this.availableGuilds = new ArrayList<>(sessionData.getDiscordUser().get().getGuilds());
            addSlots();
        }

        add(
                new PageHeader(getUiData(), getTitleText(), getTranslation("premium.desc"), getRoute()),
                mainContent
        );
    }

    private void addSlots() {
        H2 title = new H2(getTranslation("premium.title"));
        title.getStyle().set("margin-top", "8px");
        mainContent.add(title);

        if (userPremium.getSlots().size() > 0) {
            Paragraph p = new Paragraph(getTranslation("premium.subtitle"));
            p.getStyle().set("margin-bottom", "26px")
                    .set("margin-top", "0");
            mainContent.add(p);

            for (int i = 0; i < userPremium.getSlots().size(); i++) {
                long guildId = userPremium.getSlots().get(i);
                Guild guild = getSessionData().getDiscordUser().map(u -> u.getGuildById(guildId)).orElse(null);
                if (guild == null && guildId != 0) {
                    guild = new Guild();
                    guild.setId(guildId);
                    guild.setName(String.format("%X", guildId));
                }
                addCard(guild, i);
            }
        } else {
            Paragraph p = new Paragraph(getTranslation("premium.noactive"));
            p.getStyle().set("margin-bottom", "26px")
                    .set("margin-top", "0");
            mainContent.add(p);

            HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.setPadding(false);

            Button patreonButton = new Button(getTranslation("premium.patreon"));
            patreonButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            Anchor patreonAnchor = new Anchor(ExternalLinks.PATREON_PAGE, patreonButton);
            patreonAnchor.setTarget("_blank");

            Button connectButton = new Button(getTranslation("premium.connect"));
            connectButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            Anchor connectAnchor = new Anchor("https://www.patreon.com/settings/apps", connectButton);
            connectAnchor.setTarget("_blank");

            buttonLayout.add(patreonAnchor, connectAnchor);
            mainContent.add(buttonLayout);
        }
    }

    private void addCard(Guild guild, int i) {
        Card card = new Card();
        card.setWidthFull();
        card.setHeight("72px");
        card.getStyle().set("margin-bottom", "-8px");

        card.add(getCardContent(guild, i, true));
        cards.add(card);
        mainContent.add(card);
    }

    private HorizontalLayout getCardContent(Guild guild, int i, boolean init) {
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
                    card.add(getCardContent(guild, i, false));
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
            card.add(getCardContent(null, i, false));
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
