package com.mcmiddleearth.entities.protocol.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.mcmiddleearth.entities.entities.SimpleNonLivingEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class SimpleNonLivingEntitySpawnPacket extends AbstractPacket {

    private final PacketContainer spawn;

    private final SimpleNonLivingEntity entity;

    public SimpleNonLivingEntitySpawnPacket(SimpleNonLivingEntity entity) {
        this.entity = entity;
        spawn = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        spawn.getIntegers().write(0, entity.getEntityId())
                .write(1, -300) //velocity
                .write(2, 0)
                .write(3, -300)
                .write(6,0); // object data
        spawn.getEntityTypeModifier().write(0,entity.getType().getBukkitEntityType());
        spawn.getUUIDs().write(0, entity.getUniqueId());
        update();
    }

    @Override
    public void update() {
        Location loc = entity.getLocation();
        spawn.getDoubles()
                .write(0, loc.getX())
                .write(1, loc.getY())
                .write(2, loc.getZ());
        spawn.getBytes()
                .write(4, (byte)(loc.getYaw()*256/360))
                .write(5, (byte) (loc.getPitch()*256/360));
    }

    @Override
    public void send(Player recipient) {
//Logger.getGlobal().info("send non living spawn to : "+recipient.getName());
        send(spawn, recipient);
    }

}
