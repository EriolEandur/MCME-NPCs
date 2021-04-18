package com.mcmiddleearth.entities.entities.composite;

import com.mcmiddleearth.entities.ai.goals.Goal;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.McmeEntityType;
import com.mcmiddleearth.entities.protocol.packets.*;
import com.mcmiddleearth.entities.util.UuidGenerator;
import org.bukkit.Location;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Bone implements McmeEntity {

    private final String name;

    private final int entityId;

    private final CompositeEntity parent;

    private Vector relativePosition, shift, velocity;
    private EulerAngle headPose;

    private final UUID uniqueId;

    private final AbstractPacket spawnPacket;
    private final AbstractPacket teleportPacket;
    private final AbstractPacket movePacket;
    private final AbstractPacket rotatePacket;
    private final AbstractPacket configPacket;

    public Bone(String name, CompositeEntity parent) {
        this.name = name;
        uniqueId = UuidGenerator.getRandomV2();
        entityId = parent.getEntityId()+parent.getBones().size();
        this.parent = parent;
        spawnPacket = new SimpleNonLivingEntitySpawnPacket(this);
        teleportPacket = new SimpleEntityTeleportPacket(this);
        movePacket = new SimpleEntityMovePacket(this);
        configPacket = new BoneConfigPacket(this);
        rotatePacket = new BoneRotationPacket(this);
    }

    @Override
    public void doTick() {}

    public void move() {
        velocity = parent.getVelocity().clone().add(shift);
    }

    public AbstractPacket getSpawnPacket() {
        return spawnPacket;
    }

    public AbstractPacket getTeleportPacket() {
        return teleportPacket;
    }

    public AbstractPacket getMovePacket() {
        return movePacket;
    }

    public AbstractPacket getRotatePacket() {
        return rotatePacket;
    }

    public AbstractPacket getConfigPacket() {
        return configPacket;
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getLocation() {
        return parent.getLocation().clone().add(relativePosition);
    }

    @Override
    public void setLocation(Location location) {}

    @Override
    public McmeEntityType getType() {
        return new McmeEntityType(McmeEntityType.CustomEntityType.BONE);
    }

    @Override
    public Vector getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(Vector velocity) {}

    @Override
    public Location getTarget() {
        return null;
    }

    @Override
    public Goal getGoal() {
        return null;
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public int getEntityQuantity() {
        return 1;
    }

    @Override
    public boolean hasLookUpdate() {
        return false;
    }

    @Override
    public boolean hasRotationUpdate() {
        return false;
    }

    @Override
    public boolean onGround() {
        return false;
    }

    @Override
    public float getRotation() {
        return 0;
    }

    public Vector getRelativePosition() {
        return relativePosition;
    }

    public EulerAngle getHeadPose() {
        return headPose;
    }

    public void setRelativePosition(Vector relativePosition) {
        shift = relativePosition.clone().subtract(this.relativePosition);
        this.relativePosition = relativePosition;
    }

    public void setHeadPose(EulerAngle headPose) {
        this.headPose = headPose;
    }
}
