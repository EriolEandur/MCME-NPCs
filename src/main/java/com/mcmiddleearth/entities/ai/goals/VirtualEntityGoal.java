package com.mcmiddleearth.entities.ai.goals;

import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.util.Vector;

public abstract class VirtualEntityGoal implements Goal{

    private GoalType type;

    private VirtualEntity entity;

    protected Vector direction;

    protected boolean rotation, headRotation;

    protected float yaw, headYaw, headPitch;

    private int updateInterval = 10;

    public VirtualEntityGoal(GoalType type, VirtualEntity entity) {
        this.type = type;
        this.entity = entity;
    }

    public Vector getDirection() {
        return direction;
    }

    public boolean hasHeadRotation() {
        return headRotation;
    }

    public float getHeadYaw() {
        return headYaw;
    }

    public float getHeadPitch() {
        return headPitch;
    }

    public boolean hasRotation() {
        return rotation;
    }

    public float getRotation() {
        return yaw;
    }

    public abstract void updatePath();

    public abstract void updateTick();

    public VirtualEntity getEntity() {
        return entity;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }
}
