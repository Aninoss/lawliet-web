package xyz.lawlietbot.spring.backend.userdata;

import java.util.Optional;

public class DiscordServerData {

    private long id;
    private String name;
    private Optional<String> iconURL;

    public DiscordServerData(long id, String name, Optional<String> iconURL) {
        this.id = id;
        this.name = name;
        this.iconURL = iconURL;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getIcon() {
        return iconURL;
    }
}
