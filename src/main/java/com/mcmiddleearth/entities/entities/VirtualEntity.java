package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.ai.goals.Goal;
import com.mcmiddleearth.entities.ai.goals.VirtualEntityGoal;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import com.mcmiddleearth.entities.ai.movement.MovementEngine;
import com.mcmiddleearth.entities.ai.movement.MovementType;
import com.mcmiddleearth.entities.entities.attributes.VirtualAttributeFactory;
import com.mcmiddleearth.entities.protocol.packets.AbstractPacket;
import org.bukkit.Location;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public abstract class VirtualEntity implements McmeEntity, Attributable {

    private UUID uniqueId;

    private String name;

    private final Set<Player> viewers = new HashSet<>();

    private final Set<Player> whiteList = new HashSet<>();

    private boolean invertWhiteList = false;

    private int tickCounter = 0;

    protected AbstractPacket spawnPacket;
    protected AbstractPacket removePacket;
    protected AbstractPacket teleportPacket;
    protected AbstractPacket movePacket;

    private Location location;

    private float rotation;

    private Vector velocity;

    private boolean lookUpdate, rotationUpdate;

    private boolean teleported;

    private MovementType movementType;

    private VirtualEntityGoal goal;

    private final McmeEntityType type;

    private final Map<Attribute, AttributeInstance> attributes = new HashMap<>();

    private final EntityBoundingBox boundingBox;

    private final MovementEngine movementEngine;

    private final int jumpHeight = 1;
    private final int fallDepth = 1; //if both values differ from each other pathfinding can easily get stuck.

    public VirtualEntity(VirtualEntityFactory factory) {
        this.type = factory.getType();
        this.location = factory.getLocation();
        this.velocity = new Vector(0, 0, 0);
        this.uniqueId = factory.getUniqueId();
        this.name = factory.getName();
        this.invertWhiteList = factory.isInvertWhitelist();
        this.movementType = factory.getMovementType();
        this.boundingBox = factory.getBoundingBox();
        this.boundingBox.setLocation(location);
        this.movementEngine = new MovementEngine(this);
        this.goal = factory.getGoalFactory().build(this);
    }

    @Override
    public void doTick() {
        if(teleported) {
            teleport();
            if(goal!=null) {
                goal.updatePath();
            }
        } else {
            if(goal != null) {
                goal.updateTick();
                if(tickCounter%goal.getUpdateInterval()==goal.getUpdateRandom()) {
                    goal.updatePath();
                }
                switch(movementType) {
                    case FLYING:
                    case WALKING:
                        goal.updateTick();
                }
                movementEngine.calculateMovement(goal.getDirection());
                if(goal.hasHeadRotation()) {
                    setHeadRotation(goal.getHeadYaw(), goal.getHeadPitch());
                }
                if(goal.hasRotation()) {
                    setRotation(goal.getRotation());
                }
            }
            move();
        }
        tickCounter++;
    }

    public void teleport() {
        teleportPacket.update();
        teleportPacket.send(viewers);
        teleported = false;
        lookUpdate = false;
        rotationUpdate = false;

        spawnPacket.update();
    }

    public void move() {
        location = location.add(velocity);
        boundingBox.setLocation(location);

        movePacket.update();
        movePacket.send(viewers);
        lookUpdate = false;
        rotationUpdate = false;

        spawnPacket.update();
    }

    @Override
    public void setLocation(Location location) {
        this.location = location.clone();
        this.boundingBox.setLocation(location);
        teleported = true;
    }

    @Override
    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public void setHeadRotation(float yaw, float pitch) {
        getLocation().setYaw(yaw);
        getLocation().setPitch(pitch);
        lookUpdate = true;
    }

    public void setRotation(float yaw) {
        rotation = yaw;
        rotationUpdate = true;
    }

    public float getRotation() {
        return rotation;
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
        return location;
    }

    @Override
    public Goal getGoal() {
        return goal;
    }

    @Override
    public Vector getVelocity() {
        return velocity;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public boolean hasLookUpdate() {
        return lookUpdate;
    }

    public boolean hasRotationUpdate() {
        return rotationUpdate;
    }

    @Override
    public Location getTarget() {
        return null;
    }

    public boolean onGround() {
        return true;
    }

    public EntityBoundingBox getBoundingBox() {
        return boundingBox;
    }

    @Override
    public McmeEntityType getType() {
        return type;
    }

    public boolean isViewer(Player player) {
        return viewers.contains(player);
    }

    public Set<Player> getViewers() {
        return viewers;
    }

    public synchronized void addViewer(Player player) {
        if(!invertWhiteList && !(whiteList.isEmpty() || whiteList.contains(player))
                || invertWhiteList && whiteList.contains(player)) {
            return;
        }
        spawnPacket.send(player);
        viewers.add(player);
    }

    public synchronized void removeViewer(Player player) {
        removePacket.send(player);
        viewers.remove(player);
    }

    public void removeAllViewers() {
        List<Player> removal = new ArrayList<>(viewers);
        removal.forEach(this::removeViewer);
    }

    @Override
    public AttributeInstance getAttribute(@NotNull Attribute attribute) {
        return attributes.get(attribute);
    }

    @Override
    public void registerAttribute(@NotNull Attribute attribute) {
        attributes.put(attribute, VirtualAttributeFactory.getAttributeInstance(attribute, null));
    }

    public int getJumpHeight() {
        return jumpHeight;
    }

    public int getFallDepth() {
        return fallDepth;
    }
}
