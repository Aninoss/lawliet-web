package com.gmail.leonard.spring.Backend.CommandList;

import com.gmail.leonard.spring.Backend.WebCommunicationClient.WebComClient;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

public class CommandListContainer {

    private static CommandListContainer ourInstance = new CommandListContainer();
    private CopyOnWriteArrayList<CommandListCategory> categories = new CopyOnWriteArrayList<>();

    public static CommandListContainer getInstance() {
        return ourInstance;
    }


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
        loadIfEmpty();
        return categories;
    }

    public int size() {
        loadIfEmpty();
        return categories.size();
    }

    public int allCommandsSize(boolean showNsfw) {
        loadIfEmpty();
        int n = 0;
        for(CommandListCategory category: categories) {
            n += category.size(showNsfw);
        }

        return n;
    }

    public void clear() { categories.clear(); }

    private void loadIfEmpty() {
        if (categories.size() == 0) {
            try {
                System.out.println("Update command list...");
                WebComClient.getInstance().updateCommandList().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

}
