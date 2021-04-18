package com.mcmiddleearth.entities.protocol.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.mcmiddleearth.entities.entities.composite.Bone;
import org.bukkit.Rotation;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

public class BoneRotationPacket extends AbstractPacket {

    private final PacketContainer headRotation;

    private final Bone bone;

    public BoneRotationPacket(Bone bone) {
        this.bone = bone;
        this.headRotation = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        headRotation.getIntegers().write(0,bone.getEntityId());

        update();
    }

    @Override
    public void update() {
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.WrappedDataWatcherObject state = new WrappedDataWatcher
                .WrappedDataWatcherObject(15,WrappedDataWatcher.Registry.get(EulerAngle.class));
        watcher.setObject(state, bone.getHeadPose());

        headRotation.getWatchableCollectionModifier().write(0,watcher.getWatchableObjects());
    }

    @Override
    public void send(Player recipient) {
        send(headRotation,recipient);
    }
}
