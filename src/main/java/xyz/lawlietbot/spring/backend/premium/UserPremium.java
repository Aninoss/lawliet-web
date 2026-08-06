package xyz.lawlietbot.spring.backend.premium;

import java.util.ArrayList;

public class UserPremium {

    private final long userId;
    private final ArrayList<Long> slots;
    private final ArrayList<Long> slotsPlus;

    public UserPremium(long userId, ArrayList<Long> slots, ArrayList<Long> slotsPlus) {
        this.userId = userId;
        this.slots = slots;
        this.slotsPlus = slotsPlus;
    }

    public long getUserId() {
        return userId;
    }

    public ArrayList<Long> getSlots() {
        return slots;
    }

    public ArrayList<Long> getSlotsPlus() {
        return slotsPlus;
    }

}
