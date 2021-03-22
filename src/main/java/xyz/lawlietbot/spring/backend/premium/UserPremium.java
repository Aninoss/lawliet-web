package xyz.lawlietbot.spring.backend.premium;

import java.util.List;

public class UserPremium {

    private final long userId;
    private final List<Guild> mutualGuilds;
    private final List<Long> slots;

    public UserPremium(long userId, List<Guild> mutualGuilds, List<Long> slots) {
        this.userId = userId;
        this.mutualGuilds = mutualGuilds;
        this.slots = slots;
    }

    public long getUserId() {
        return userId;
    }

    public List<Guild> getMutualGuilds() {
        return mutualGuilds;
    }

    public List<Long> getSlots() {
        return slots;
    }

    public void setSlot(int i, long guildId) {
        slots.set(i, guildId);
    }

    public Guild getGuildById(long guildId) {
        return mutualGuilds.stream()
                .filter(guild -> guild.getId() == guildId)
                .findFirst()
                .orElse(null);
    }


    public static class Guild {

        private final long guildId;
        private final String name;
        private final String iconUrl;

        public Guild(long guildId, String name, String iconUrl) {
            this.guildId = guildId;
            this.name = name;
            this.iconUrl = iconUrl;
        }

        public long getId() {
            return guildId;
        }

        public String getName() {
            return name;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        @Override
        public String toString() {
            return name;
        }

    }

}
