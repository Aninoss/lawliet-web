package xyz.lawlietbot.spring.backend.premium;

import java.util.List;

public class UserPremium {

    private final long userId;
    private final List<Long> slots;

    public UserPremium(long userId, List<Long> slots) {
        this.userId = userId;
        this.slots = slots;
    }

    public long getUserId() {
        return userId;
    }

    public List<Long> getSlots() {
        return slots;
    }

    public void setSlot(int i, long guildId) {
        slots.set(i, guildId);
    }

}
