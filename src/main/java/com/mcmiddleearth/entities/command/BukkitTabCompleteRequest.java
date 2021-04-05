package com.mcmiddleearth.entities.command;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.TabCompleteRequest;
import com.mcmiddleearth.entities.EntityAPI;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class BukkitTabCompleteRequest implements TabCompleteRequest {

    private boolean isCancelled;

    private final McmeCommandSender sender;

    private final List<String> suggestions = new ArrayList<>();

    private final String cursor;

    public BukkitTabCompleteRequest(CommandSender sender, String cursor) {
        this.sender = EntityAPI.wrapCommandSender(sender);
        this.cursor = cursor;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public McmeCommandSender getSender() {
        return sender;
    }

    @Override
    public List<String> getSuggestions() {
        return (isCancelled?null:suggestions);
    }

    @Override
    public String getCursor() {
        return cursor;
    }
}
