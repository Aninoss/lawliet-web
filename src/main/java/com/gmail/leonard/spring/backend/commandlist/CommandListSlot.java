package com.gmail.leonard.spring.backend.commandlist;

import com.gmail.leonard.spring.backend.LanguageString;

public class CommandListSlot {

    private String trigger, emoji;
    private final LanguageString langTitle = new LanguageString();
    private final LanguageString langUsage = new LanguageString();
    private final LanguageString langExamples = new LanguageString();
    private final LanguageString langDescShort = new LanguageString();
    private final LanguageString langDescLong = new LanguageString();
    private final LanguageString langUserPermissions = new LanguageString();
    private boolean nsfw = false, requiresUserPermissions = false, canBeTracked = false, patreonOnly = false;

    public CommandListSlot() {}

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public LanguageString getLangTitle() {
        return langTitle;
    }

    public LanguageString getLangUsage() {
        return langUsage;
    }

    public LanguageString getLangExamples() {
        return langExamples;
    }

    public LanguageString getLangDescShort() {
        return langDescShort;
    }

    public LanguageString getLangDescLong() {
        return langDescLong;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    public void setNsfw(boolean nsfw) {
        this.nsfw = nsfw;
    }

    public boolean isRequiresUserPermissions() {
        return requiresUserPermissions;
    }

    public void setRequiresUserPermissions(boolean requiresUserPermissions) {
        this.requiresUserPermissions = requiresUserPermissions;
    }

    public boolean isCanBeTracked() {
        return canBeTracked;
    }

    public void setCanBeTracked(boolean canBeTracked) {
        this.canBeTracked = canBeTracked;
    }

    public boolean isPatreonOnly() { return patreonOnly; }

    public void setPatreonOnly(boolean patreonOnly) { this.patreonOnly = patreonOnly; }

    public LanguageString getLangUserPermissions() {
        return langUserPermissions;
    }
}
