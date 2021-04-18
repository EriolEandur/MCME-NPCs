package com.mcmiddleearth.entities.entities.composite;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.entities.VirtualEntityFactory;
import com.mcmiddleearth.entities.entities.composite.animation.Animation;
import com.mcmiddleearth.entities.entities.composite.animation.AnimationType;
import org.bukkit.Material;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BakedAnimationEntity extends CompositeEntity {

    private final Map<String, Animation> animations = new HashMap<>();

    private Animation currentAnimation;

    private final File bakedAnimationFolder = new File(EntitiesPlugin.getInstance().getDataFolder(),"animation");


    public BakedAnimationEntity(int entityId, VirtualEntityFactory factory) {
        super(entityId, factory);
        File animationFile = new File(bakedAnimationFolder, factory.getDataFile());
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(animationFile)) {
            JsonObject data = new JsonParser().parse(reader).getAsJsonObject();
            JsonObject modelData = data.get("model").getAsJsonObject();
            Material itemMaterial = Material.valueOf(modelData.get("head_item").getAsString().toUpperCase());
            JsonObject animationData = data.get("animations").getAsJsonObject();
            animationData.entrySet().forEach(entry
                    -> animations.put(entry.getKey(), Animation.loadAnimation(entry.getValue().getAsJsonObject(),
                    itemMaterial, this)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        createPackets();
    }

    @Override
    public void doTick() {
        if(currentAnimation.isFinished()) {
            if(currentAnimation.getType().equals(AnimationType.CHAIN)) {
                currentAnimation = animations.get(currentAnimation.getNext());
                currentAnimation.reset();
            }
        }
        currentAnimation.doTick();
        super.doTick();
    }

    public void setAnimation(String name) {
        Animation newAnim = animations.get(name);
        if(newAnim!=null) {
            currentAnimation = newAnim;
            currentAnimation.reset();
        }
    }
}
