package com.mcmiddleearth.entities.entities.composite.animation;

import com.google.gson.JsonObject;
import com.mcmiddleearth.entities.entities.composite.Bone;
import com.mcmiddleearth.entities.entities.composite.CompositeEntity;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class Frame {

    private final Map<Bone,BoneData> boneData = new HashMap<>();

    public Frame() {
    }

    public void apply(int state) {
        boneData.forEach((bone, boneData) -> {
            bone.setRelativePosition(boneData.getPosition());
            bone.setHeadPose(boneData.getHeadPose());
        });
    }

    public static Frame loadFrame(CompositeEntity entity,  Animation animation,
                                  JsonObject data, Material itemMaterial) {
        return null;
    }
}
