package com.mcmiddleearth.entities.protocol.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.mcmiddleearth.entities.entities.SimpleEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SimpleEntityTeleportPacket extends AbstractPacket {

    private final PacketContainer teleport;

    private final SimpleEntity entity;

    public SimpleEntityTeleportPacket(SimpleEntity entity) {
        this.entity = entity;
        teleport = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        teleport.getIntegers().write(0,entity.getEntityId());

        update();
    }

    @Override
    public void update() {
        Location location = entity.getLocation();
        teleport.getDoubles()
                .write(0, location.getX())
                .write(1, location.getY())
                .write(2, location.getZ());
        teleport.getBytes()
                .write(0, (byte)(location.getYaw()*256/360))
                .write(1, (byte)(location.getPitch()*256/360));
        teleport.getBooleans().write(0,entity.onGround());
    }

    @Override
    public void send(Player recipient) {
        send(teleport,recipient);
    }

}
