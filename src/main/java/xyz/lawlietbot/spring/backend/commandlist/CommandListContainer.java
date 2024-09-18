package xyz.lawlietbot.spring.backend.commandlist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.lawlietbot.spring.ExceptionLogger;
import xyz.lawlietbot.spring.syncserver.EventOut;
import xyz.lawlietbot.spring.syncserver.SendEvent;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

public class CommandListContainer {

    private final static Logger LOGGER = LoggerFactory.getLogger(CommandListContainer.class);
    private static final CommandListContainer ourInstance = new CommandListContainer();
    private ArrayList<CommandListCategory> categories = new ArrayList<>();
    private Instant nextUpdate = Instant.now();

    private CommandListContainer() {
    }

    public static CommandListContainer getInstance() {
        return ourInstance;
    }

    public CommandListCategory get(int n) {
        return categories.get(n);
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
        for (CommandListCategory category : categories) {
            n += category.size(showNsfw);
        }

        return n;
    }

    private synchronized void loadIfEmpty() {
        if (categories.isEmpty()) {
            LOGGER.info("Loading command list");
            categories = fetch().join();
            setNextUpdate();
        } else if (Instant.now().isAfter(nextUpdate)) {
            setNextUpdate();
            LOGGER.info("Updating command list");
            fetch().thenAccept(c -> categories = c)
                    .exceptionally(ExceptionLogger.get());
        }
    }

    private void setNextUpdate() {
        nextUpdate = Instant.now().plus(20, ChronoUnit.MINUTES);
    }

    private CompletableFuture<ArrayList<CommandListCategory>> fetch() {
        CompletableFuture<ArrayList<CommandListCategory>> future = new CompletableFuture<>();
        SendEvent.sendToAnyCluster(EventOut.COMMAND_LIST)
                .exceptionally(e -> {
                    future.completeExceptionally(e);
                    return null;
                })
                .thenAccept(responseJson -> {
                    try {
                        if (responseJson.has("categories")) {
                            ArrayList<CommandListCategory> mainList = new ArrayList<>();
                            JSONArray arrayJSON = responseJson.getJSONArray("categories");

                            //Read every command category
                            for (int i = 0; i < arrayJSON.length(); i++) {
                                JSONObject categoryJSON = arrayJSON.getJSONObject(i);

                                CommandListCategory commandListCategory = new CommandListCategory();
                                commandListCategory.setId(categoryJSON.getString("id"));
                                commandListCategory.getLangName().set(categoryJSON.getJSONObject("name"));

                                JSONArray commandsJSON = categoryJSON.optJSONArray("commands");
                                //Read every command
                                for (int j = 0; j < commandsJSON.length(); j++) {
                                    JSONObject commandJSON = commandsJSON.getJSONObject(j);

                                    CommandListSlot commandListSlot = new CommandListSlot();
                                    commandListSlot.setTrigger(commandJSON.getString("trigger"));
                                    commandListSlot.setEmoji(commandJSON.getString("emoji"));
                                    commandListSlot.getLangTitle().set(commandJSON.getJSONObject("title"));
                                    commandListSlot.getLangDescShort().set(commandJSON.getJSONObject("desc_short"));
                                    commandListSlot.getLangDescLong().set(commandJSON.getJSONObject("desc_long"));
                                    commandListSlot.getLangUsage().set(commandJSON.getJSONObject("usage"));
                                    commandListSlot.getLangExamples().set(commandJSON.getJSONObject("examples"));
                                    commandListSlot.getLangUserPermissions().set(commandJSON.getJSONObject("user_permissions"));
                                    commandListSlot.setNsfw(commandJSON.getBoolean("nsfw"));
                                    commandListSlot.setRequiresUserPermissions(commandJSON.getBoolean("requires_user_permissions"));
                                    commandListSlot.setCanBeTracked(commandJSON.getBoolean("can_be_tracked"));
                                    commandListSlot.setPatreonOnly(commandJSON.getBoolean("patron_only"));

                                    commandListCategory.add(commandListSlot);
                                }

                                mainList.add(commandListCategory);
                            }
                            future.complete(mainList);
                        } else {
                            future.completeExceptionally(new NoSuchElementException("No entries"));
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });

        return future;
    }

}
