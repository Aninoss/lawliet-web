package xyz.lawlietbot.spring.backend.userdata;

import bell.oauth.discord.domain.Guild;
import bell.oauth.discord.domain.User;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DiscordUser implements Serializable {

    private final User discordUser;
    private final List<Guild> guilds;

    public DiscordUser(User discordUser, List<Guild> guilds) {
        this.discordUser = discordUser;
        this.guilds = guilds;
    }

    public long getId() {
        return Long.parseLong(discordUser.getId());
    }

    public String getUsername() {
        return discordUser.getUsername();
    }

    public String getDiscriminator() {
        return discordUser.getDiscriminator();
    }

    public String getUserAvatar() {
        return "https://cdn.discordapp.com/avatars/" + discordUser.getId() + "/" + discordUser.getAvatar() + ".png";
    }

    public boolean hasAvatar() {
        return discordUser.getAvatar() != null;
    }

    public boolean hasGuilds() {
        return guilds != null;
    }

    public List<Guild> getGuilds() {
        if (guilds == null) {
            return Collections.emptyList();
        }
        return guilds.stream()
                .sorted(Comparator.comparing(Guild::getName))
                .collect(Collectors.toList());
    }

    public Guild getGuildById(long guildId) {
        if (guilds == null) {
            return null;
        }
        return guilds.stream()
                .filter(g -> g.getId() == guildId)
                .findFirst()
                .orElse(null);
    }

}
