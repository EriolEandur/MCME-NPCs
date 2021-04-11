package com.mcmiddleearth.entities.protocol.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.mcmiddleearth.entities.entities.SimpleLivingEntity;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class SimpleLivingEntitySpawnPacket extends AbstractPacket {

    private final PacketContainer spawn;

    private final SimpleLivingEntity entity;

    public SimpleLivingEntitySpawnPacket(SimpleLivingEntity entity) {
        this.entity = entity;
        spawn = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        spawn.getIntegers().write(0, entity.getEntityId())
                .write(1, getEntityType(entity.getType().getBukkitEntityType()));
        spawn.getUUIDs().write(0, entity.getUniqueId());
        update();
    }

    @Override
    public void update() {
        Location loc = entity.getLocation();
//Logger.getGlobal().info("Spawn living: "+loc.getYaw()+" "+loc.getPitch());
        spawn.getDoubles()
                .write(0, loc.getX())
                .write(1, loc.getY())
                .write(2, loc.getZ());
        spawn.getBytes()
                .write(0, (byte) (loc.getPitch()*256/360))
                .write(1, (byte) (loc.getPitch()*256/360))
                .write(2,(byte) (loc.getYaw()*256/360));
    }

    @Override
    public void send(Player recipient) {
//Logger.getGlobal().info("send living spawn to : "+recipient.getName());
        send(spawn, recipient);
    }

    private int getEntityType(EntityType entityType) {
        switch(entityType) {
            case BAT:
                return 3;
            case BEE:
                return 4;
            case BLAZE:
                return 5;
            case CAT:
                return 7;
            case CAVE_SPIDER:
                return 8;
            case CHICKEN:
                return 9;
            case COD:
                return 10;
            case COW:
                return 11;
            case CREEPER:
                return 12;
            case DOLPHIN:
                return 13;
            case DONKEY:
                return 14;
            case DROWNED:
                return 16;
            case ELDER_GUARDIAN:
                return 17;
            case ENDER_DRAGON:
                return 19;
            case ENDERMAN:
                return 20;
            default: //skeleton
                return 73;
        }
    }
}
