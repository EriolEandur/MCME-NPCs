package com.mcmiddleearth.entities.protocol.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.mcmiddleearth.entities.entities.SimpleEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SimpleEntityMovePacket extends AbstractPacket {

    private final PacketContainer move;
    private final PacketContainer moveLook;
    private final PacketContainer look;
    private final PacketContainer stand;
    private final PacketContainer head;

    private final SimpleEntity entity;

    private MoveType moveType;

    public SimpleEntityMovePacket(SimpleEntity entity) {
        this.entity = entity;
        moveLook = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
        moveLook.getIntegers().write(0,entity.getEntityId());

        move = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE);
        move.getIntegers().write(0, entity.getEntityId());

        look = new PacketContainer(PacketType.Play.Server.ENTITY_LOOK);
        look.getIntegers().write(0, entity.getEntityId());

        stand = new PacketContainer(PacketType.Play.Server.ENTITY);
        stand.getIntegers().write(0, entity.getEntityId());

        head = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        stand.getIntegers().write(0, entity.getEntityId());

        update();
    }

    @Override
    public void update() {
        moveType = getMoveType();
        switch (moveType) {
            case MOVE:
                Vector dir = getShift();
                move.getShorts()
                        .write(0, (short) dir.getBlockX())
                        .write(1, (short) dir.getBlockY())
                        .write(2, (short) dir.getBlockZ());
                move.getBooleans().write(0,entity.onGround());
                break;
            case LOOK:
                byte yaw = getAngle(entity.getRotation());
                byte pitch = getAngle(entity.getLocation().getPitch());
                move.getBytes()
                        .write(0, yaw)
                        .write(1, pitch);
                move.getBooleans().write(0,entity.onGround());
                break;
            case MOVE_LOOK:
                dir = getShift();
                yaw = getAngle(entity.getRotation());
                pitch = getAngle(entity.getLocation().getPitch());
                moveLook.getShorts()
                        .write(0, (short) dir.getBlockX())
                        .write(1, (short) dir.getBlockY())
                        .write(2, (short) dir.getBlockZ());
                move.getBytes()
                        .write(0, yaw)
                        .write(1, pitch);
                move.getBooleans().write(0,entity.onGround());
                break;
        }
        if(entity.hasLookUpdate()) {
            look.getBytes().write(0,getAngle(entity.getLocation().getYaw()));
        }
    }

    @Override
    public void send(Player recipient) {
        //Logger.getGlobal().info(""+moveType.name());
        switch(moveType) {
            case STAND:
                //send(stand, recipient); //probably not required to send
                break;
            case MOVE:
                send(move, recipient);
                break;
            case MOVE_LOOK:
                send(moveLook, recipient);
                break;
            case LOOK:
                send(look, recipient);
        }
        if(entity.hasLookUpdate()) {
            send(head,recipient);
        }
    }

    private Vector getShift() {
        return entity.getVelocity().multiply(32*128);
    }

    private byte getAngle(float bukkitAngle) {

        return (byte)(bukkitAngle*256/360);
    }

    private MoveType getMoveType() {
        Vector velocity = entity.getVelocity();
        if(velocity.getX() == 0 && velocity.getY() == 0 && velocity.getZ() == 0) {
            if(entity.hasLookUpdate() || entity.hasLookUpdate()) {
                return MoveType.LOOK;
            } else {
                return MoveType.STAND;
            }
        } else {
            if(entity.hasLookUpdate() || entity.hasRotationUpdate()) {
                return MoveType.MOVE_LOOK;
            } else {
                return MoveType.MOVE;
            }
        }
    }

    public enum MoveType {
        STAND, MOVE, MOVE_LOOK, LOOK
    }
}
