package com.gmail.leonard.spring.backend.userdata;

import bell.oauth.discord.domain.User;

public class DiscordUser {

    private final User discordUser;

    public DiscordUser(User discordUser) {
        this.discordUser = discordUser;
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

}
