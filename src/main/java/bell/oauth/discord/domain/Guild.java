package bell.oauth.discord.domain;

public class Guild {

    private long id;
    private String name;
    private String icon;
    private Integer permissions;
    private boolean owner;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon != null ? String.format("https://cdn.discordapp.com/icons/%d/%s.webp", id, icon) : null;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getPermissions() {
        return permissions;
    }

    public void setPermissions(Integer permissions) {
        this.permissions = permissions;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return this.name
                + "\nID: " + this.id
                + "\nIcon: " + this.icon
                + "\nPermissions: " + this.permissions
                + "\nIsOwner: " + this.owner;
    }


}
