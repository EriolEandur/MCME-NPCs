package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.entities.ai.goals.Goal;
import com.mcmiddleearth.entities.command.BukkitCommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RealPlayer extends BukkitCommandSender implements McmeEntity {

    public RealPlayer(Player bukkitPlayer) {
        super(bukkitPlayer);
    }

    @Override
    public UUID getUniqueId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    public Location getLocation() {
        return ((Player)getCommandSender()).getLocation();
    }

    @Override
    public void setLocation(Location location) {

    }

    @Override
    public McmeEntityType getType() {
        return null;
    }

    @Override
    public Vector getVelocity() {
        return null;
    }

    @Override
    public void setVelocity(Vector velocity) {

    }

    @Override
    public Location getTarget() {
        return null;
    }

    @Override
    public Goal getGoal() {
        return null;
    }

    @Override
    public void doTick() {

    }

    @Override
    public int getEntityId() {
        return 0;
    }

    @Override
    public int getEntityQuantity() {
        return 1;
    }

    public Player getBukkitPlayer() {
        return (Player) getCommandSender();
    }

    @Override
    public void sendMessage(BaseComponent[] baseComponents) {
        getBukkitPlayer().sendMessage(baseComponents);
    }

}
