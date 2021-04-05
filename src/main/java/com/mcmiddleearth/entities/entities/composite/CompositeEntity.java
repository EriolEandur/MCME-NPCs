package com.mcmiddleearth.entities.entities.composite;

import com.mcmiddleearth.entities.ai.goals.VirtualEntityGoal;
import com.mcmiddleearth.entities.entities.VirtualEntityFactory;
import com.mcmiddleearth.entities.entities.McmeEntityType;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class CompositeEntity extends VirtualEntity {

    public CompositeEntity(int entityId, VirtualEntityFactory factory) {
        super(factory);
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void setLocation(Location setLocation) {

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
    public Location getTarget() {
        return null;
    }

    @Override
    public VirtualEntityGoal getGoal() {
        return null;
    }

    @Override
    public int getEntityId() {
        return 0;
    }

    @Override
    public int getEntityQuantity() {
        return 0;
    }

}
