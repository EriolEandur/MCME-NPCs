package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.entities.command.BukkitCommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class RealPlayer extends BukkitCommandSender {

    public RealPlayer(Player bukkitPlayer) {
        super(bukkitPlayer);
    }

    public Location getLocation() {
        return ((Player)getCommandSender()).getLocation();
    }

    public Player getBukkitPlayer() {
        return (Player) getCommandSender();
    }

    @Override
    public void sendMessage(BaseComponent[] baseComponents) {
        getBukkitPlayer().sendMessage(baseComponents);
    }

}
