package com.mcmiddleearth.entities.protocol.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.mcmiddleearth.entities.entities.composite.Bone;
import org.bukkit.entity.Player;

public class BoneConfigPacket extends AbstractPacket {

    private final PacketContainer config;

    private final Bone bone;

    public BoneConfigPacket(Bone bone) {
        this.bone = bone;
        this.config = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        config.getIntegers().write(0,bone.getEntityId());

        update();
    }

    @Override
    public void update() {
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.WrappedDataWatcherObject invis = new WrappedDataWatcher
                .WrappedDataWatcherObject(0,WrappedDataWatcher.Registry.get(Byte.class));
        watcher.setObject(invis, Byte.decode("0x20"));
        WrappedDataWatcher.WrappedDataWatcherObject silent = new WrappedDataWatcher
                .WrappedDataWatcherObject(4,WrappedDataWatcher.Registry.get(Boolean.class));
        watcher.setObject(silent, true);
        WrappedDataWatcher.WrappedDataWatcherObject gravity = new WrappedDataWatcher
                .WrappedDataWatcherObject(5,WrappedDataWatcher.Registry.get(Boolean.class));
        watcher.setObject(gravity, false);
        WrappedDataWatcher.WrappedDataWatcherObject base = new WrappedDataWatcher
                .WrappedDataWatcherObject(14,WrappedDataWatcher.Registry.get(Byte.class));
        watcher.setObject(base, Byte.decode("0x08"));
    }

    @Override
    public void send(Player recipient) {
        send(config,recipient);
    }
}
