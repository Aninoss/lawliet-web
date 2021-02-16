package xyz.lawlietbot.spring.backend.commandlist;

import xyz.lawlietbot.spring.backend.LanguageString;
import java.util.ArrayList;
import java.util.List;

public class CommandListCategory {

    private String id;
    private final ArrayList<CommandListSlot> slots = new ArrayList<>();
    private final LanguageString langName = new LanguageString();

    public void add(CommandListSlot commandListSlot) {
        if (find(commandListSlot.getTrigger()) == null)
            slots.add(commandListSlot);
    }

    public CommandListSlot get(int n) {
        return slots.get(n);
    }

    public CommandListSlot find(String trigger) {
        return slots.stream()
                .filter(slot -> slot.getTrigger().equals(trigger))
                .findFirst().orElse(null);
    }

    public int size(boolean showNsfw) {
        if (showNsfw) return slots.size();
        return (int) slots.stream().filter(slot -> !slot.isNsfw()).count();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<CommandListSlot> getSlots() {
        return slots;
    }

    public LanguageString getLangName() {
        return langName;
    }

    public boolean hasCommands(boolean showNsfw) {
        for(CommandListSlot commandListSlot: slots) {
            if (!commandListSlot.isNsfw() || showNsfw) return true;
        }
        return false;
    }

}
