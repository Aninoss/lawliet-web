package xyz.lawlietbot.spring.backend.payment;

import java.time.Instant;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.*;
import xyz.lawlietbot.spring.ExceptionLogger;

public class WebhookNotifier {

    private static final WebhookClient webhookClient = new WebhookClientBuilder(System.getenv("WEBHOOK_NEWSUBS"))
            .setWait(false)
            .setAllowedMentions(AllowedMentions.all())
            .build();

    public static void newSub(String userTag, long userId, String avatar, String level, int quantity, String totalPrice) {
        String text = String.format(
                "• User: %s｜%d｜<@%d>\n• Level: %s\n• Quantity: %d\n• Total: %s",
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

        webhookClient.send(wm)
                .exceptionally(ExceptionLogger.get());
    }


}
