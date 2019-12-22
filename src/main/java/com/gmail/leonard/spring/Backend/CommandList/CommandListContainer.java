package com.gmail.leonard.spring.Backend.CommandList;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommandListContainer {

    private static CommandListContainer ourInstance = new CommandListContainer();
    private CopyOnWriteArrayList<CommandListCategory> categories = new CopyOnWriteArrayList<>();

    public static CommandListContainer getInstance() {
        return ourInstance;
    }

    private CommandListContainer() {}


    public void add(CommandListCategory commandListCategory) {
        if (find(commandListCategory.getId()) == null)
            categories.add(commandListCategory);
    }

    public CommandListCategory get(int n) {
        return categories.get(n);
    }

    public CommandListCategory find(String id) {
        return categories.stream()
                .filter(categorie -> categorie.getId().equals(id))
                .findFirst().orElse(null);
    }

    public List<CommandListCategory> getCategories() {
        return categories;
    }

    public int size() {
        return categories.size();
    }

    public int allCommandsSize(boolean showNsfw) {
        int n = 0;
        for(CommandListCategory category: categories) {
            n += category.size(showNsfw);
        }

        return n;
    }

    public void clear() { categories.clear(); }
}
