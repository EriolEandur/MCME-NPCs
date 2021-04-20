package com.mcmiddleearth.entities.entities.composite.animation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Set;

public class BoneData {

    private final EulerAngle headPose;

    private final Vector position;

    private final ItemStack[] items;

    private static final int Y_SHIFT = 3;

    public BoneData(EulerAngle headPose, Vector position, ItemStack[] items) {
        this.headPose = headPose;
        this.position = position;
        this.items = items;
    }

    public EulerAngle getHeadPose() {
        return headPose;
    }

    public Vector getPosition() {
        return position;
    }

    public ItemStack[] getItems() {
        return items;
    }

    public static BoneData loadBoneData(Map<String,Integer> states, JsonObject data, Material itemMaterial) {
        return new BoneData(readAngle(data.get("rot").getAsJsonArray()),
                            readPosition(data.get("pos").getAsJsonArray()),
                            readItems(states, data.get("cmd").getAsJsonObject(),itemMaterial));
    }

    private static ItemStack[] readItems(Map<String,Integer> states, JsonObject data, Material itemMaterial) {
        Set<Map.Entry<String, JsonElement>> entries = data.entrySet();
        ItemStack[] result = new ItemStack[entries.size()];
        entries.forEach(entry-> {
            ItemStack item = new ItemStack(itemMaterial);
            ItemMeta meta = item.getItemMeta();
            Integer stateId = states.get(entry.getKey());
            if(stateId == null) {
                stateId = states.size();
                states.put(entry.getKey(),stateId);
            }
            meta.setCustomModelData(entry.getValue().getAsInt());
            item.setItemMeta(meta);
            result[stateId] = item;
        });
        return result;
    }

    private static EulerAngle readAngle(JsonArray data) {
        return new EulerAngle(data.get(0).getAsDouble(),data.get(1).getAsDouble(),data.get(2).getAsDouble());
    }
    private static Vector readPosition(JsonArray data) {
        return new Vector(data.get(0).getAsDouble(),data.get(1).getAsDouble()+Y_SHIFT,data.get(2).getAsDouble());
    }
}
