package xyz.lawlietbot.spring.backend.payment;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.*;
import xyz.lawlietbot.spring.ExceptionLogger;

import java.time.Instant;

public class WebhookNotifier {

    private static final WebhookClient newSubClient = new WebhookClientBuilder(System.getenv("WEBHOOK_NEWSUBS"))
            .setWait(false)
            .setAllowedMentions(AllowedMentions.all())
            .build();

    private static final WebhookClient subFeedbackClient = new WebhookClientBuilder(System.getenv("WEBHOOK_SUBFEEDBACK"))
            .setWait(false)
            .setAllowedMentions(AllowedMentions.all())
            .build();

    public static void newSub(String userTag, long userId, String avatar, String level, int quantity, String totalPrice) {
        String text = String.format(
                "• User: %s｜%d｜<@%d>\n• Product: %s\n• Quantity: %d\n• Total: %s",
                userTag,
                userId,
                userId,
                level,
                quantity,
                totalPrice
        );

        WebhookEmbed we = new WebhookEmbedBuilder()
                .setDescription(text)
                .setThumbnailUrl(avatar)
                .setTimestamp(Instant.now())
                .setColor(0xFEFEFE)
                .build();
        WebhookMessage wm = new WebhookMessageBuilder()
                .addEmbeds(we)
                .setContent("<@272037078919938058>")
                .build();

        newSubClient.send(wm)
                .exceptionally(ExceptionLogger.get());
    }

    public static void newSubFeedback(String reason) {
        if (reason.isBlank()) {
            return;
        }

        WebhookEmbed we = new WebhookEmbedBuilder()
                .setTimestamp(Instant.now())
                .setColor(0xFEFEFE)
                .addField(new WebhookEmbed.EmbedField(false, "Why did you decide to stop your subscription?", reason))
                .build();
        WebhookMessage wm = new WebhookMessageBuilder()
                .addEmbeds(we)
                .build();

        subFeedbackClient.send(wm)
                .exceptionally(ExceptionLogger.get());
    }


}
