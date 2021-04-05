package com.mcmiddleearth.entities.ai.movement;

import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.util.Vector;

public class MovementEngine {

    private final Vector gravity = new Vector(0,-0.5,0);

    private final VirtualEntity entity;

    public MovementEngine(VirtualEntity entity) {
        this.entity = entity;
    }

    public void calculateMovement(Vector direction) {
        switch(entity.getMovementType()) {
            case FLYING:
                Vector velocity = direction.normalize().multiply(getFlyingSpeed());
                if(!canMove()) {
                    velocity = new Vector(0,0,0);
                }
                break;
            case FALLING:
                velocity = entity.getVelocity().add(gravity);
                if(!canMove()) {
                    velocity.setX(0);
                    velocity.setZ(0);
                    if(!canMove()) {
                        velocity.setY(distanceToGround());
                        entity.setMovementType(MovementType.WALKING);
                    }
                }
                break;
            case WALKING:
                velocity = direction.normalize().multiply(getGenericSpeed());
                velocity.setY(0);
                if(!canMove()) {
                    double jumpHeight = jumpHeight();
                    if(jumpHeight<=getJumpHeight()+0.01) {
                        entity.setMovementType(MovementType.FALLING);
                        velocity.setY(Math.sqrt(2 * jumpHeight * gravity.getY()));
                    }
                } else if(distanceToGround()>0.01) {
                    entity.setMovementType(MovementType.FALLING);
                }
                break;
        }
    }

    public boolean canMove() {

    }

    public double distanceToGround() {

    }

    public double jumpHeight() {

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
        return (instance!=null?instance.getValue():0);
    }

    private double getJumpHeight() {
        AttributeInstance instance = entity.getAttribute(Attribute.HORSE_JUMP_STRENGTH);
        return (instance!=null?instance.getValue():1);
    }
}
