package com.mcmiddleearth.entities.ai.movement;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.provider.BlockProvider;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.UUID;
import java.util.logging.Logger;

public class MovementEngine {

    private final Vector gravity = new Vector(0,-0.5,0);

    private final VirtualEntity entity;

    private final BlockProvider blockProvider;

    public MovementEngine(VirtualEntity entity) {
        this.entity = entity;
        this.blockProvider = EntitiesPlugin.getEntityServer().getBlockProvider(entity.getLocation().getWorld().getUID());
    }

    public void calculateMovement(Vector direction) {
//Logger.getGlobal().info("direction: "+direction);
        if(direction == null) return;
        switch(entity.getMovementType()) {
            case FLYING:
            case WALKING:
Logger.getGlobal().info("location: "+ entity.getLocation());
Logger.getGlobal().info("speed: "+ getFlyingSpeed());
                Vector velocity = direction.normalize().multiply(getFlyingSpeed());
Logger.getGlobal().info("velocity: "+ velocity);
                if(cannotMove()) {
                    velocity = new Vector(0,0,0);
Logger.getGlobal().info("cant move");
                }
//Logger.getGlobal().info("speed: "+getFlyingSpeed()+" velocity: "+velocity);
                entity.setVelocity(velocity);
                break;
            case FALLING:
Logger.getGlobal().info("FALLING");
                velocity = entity.getVelocity().add(gravity);
                if(cannotMove()) {
                    velocity.setX(0);
                    velocity.setZ(0);
                    if(cannotMove()) {
                        velocity.setY(distanceToGround());
                        entity.setMovementType(MovementType.WALKING);
                    }
                }
                entity.setVelocity(velocity);
                break;
            default:
Logger.getGlobal().info("DEFAULT");
                velocity = direction.normalize().multiply(getGenericSpeed());
                velocity.setY(0);
                if(cannotMove()) {
                    double jumpHeight = jumpHeight();
                    if(jumpHeight<=getJumpHeight()+0.01) {
                        entity.setMovementType(MovementType.FALLING);
                        velocity.setY(Math.sqrt(2 * jumpHeight * gravity.getY()));
                    }
                } else if(distanceToGround()>0.01) {
                    entity.setMovementType(MovementType.FALLING);
                }
                entity.setVelocity(velocity);
                break;
        }
    }

    public boolean cannotMove() {
        BoundingBox entityBB = entity.getBoundingBox().getBoundingBox().clone();
        entityBB.shift(entity.getVelocity());
        for (int i = (int) entityBB.getMinX(); i <= entityBB.getMaxX(); i++) {
            for (int j = (int) entityBB.getMinY(); j <= entityBB.getMaxY(); j++) {
                for (int k = (int) entityBB.getMinZ(); k <= entityBB.getMaxZ(); k++) {
                    if (entityBB.overlaps(blockProvider.getBoundingBox(i, j, k))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public double distanceToGround() {
        BoundingBox entityBB = entity.getBoundingBox().getBoundingBox().clone();
        return distanceToGround(entityBB, entity.getJumpHeight()+1);
    }

    private double distanceToGround(BoundingBox boundingBox, int range) {
        double distance = Double.MAX_VALUE;
        for (int i = (int) boundingBox.getMinX(); i <= boundingBox.getMaxX(); i++) {
            for (int j = (int) boundingBox.getMinZ(); j <= boundingBox.getMaxZ(); j++) {
                int y = (int) boundingBox.getMinY();
                double thisDistance = boundingBox.getMinY() - blockProvider.blockTopY(i,y,j, range);
                if(thisDistance < distance){
                    distance = thisDistance;
                }
            }
        }
        return distance;
    }

    public double jumpHeight() {
        BoundingBox entityBB = entity.getBoundingBox().getBoundingBox().clone();
        entityBB.shift(new Vector(entity.getVelocity().getX(),0,entity.getVelocity().getZ()));
        return - distanceToGround(entityBB, entity.getJumpHeight()+1);
    }

    private double getFlyingSpeed() {
        AttributeInstance instance = entity.getAttribute(Attribute.GENERIC_FLYING_SPEED);
        if(instance == null) {
            return getGenericSpeed();
        }
        return instance.getValue();
    }

    private double getGenericSpeed() {
        AttributeInstance instance = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        return (instance!=null?instance.getValue():0.1);
    }

    private double getJumpHeight() {
        AttributeInstance instance = entity.getAttribute(Attribute.HORSE_JUMP_STRENGTH);
        return (instance!=null?instance.getValue():1);
    }
}
