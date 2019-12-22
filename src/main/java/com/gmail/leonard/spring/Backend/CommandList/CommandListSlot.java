package com.gmail.leonard.spring.Backend.CommandList;

import com.gmail.leonard.spring.Backend.LanguageString;

public class CommandListSlot {

    private String trigger, emoji;
    private LanguageString langTitle = new LanguageString();
    private LanguageString langUsage = new LanguageString();
    private LanguageString langExamples = new LanguageString();
    private LanguageString langDescShort = new LanguageString();
    private LanguageString langDescLong = new LanguageString();
    private LanguageString langUserPermissions = new LanguageString();
    private boolean nsfw = false, requiresUserPermissions = false, canBeTracked = false;

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

    public LanguageString getLangUserPermissions() {
        return langUserPermissions;
    }
}
