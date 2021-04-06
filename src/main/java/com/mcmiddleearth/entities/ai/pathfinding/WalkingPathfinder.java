package com.mcmiddleearth.entities.ai.pathfinding;

import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.provider.BlockProvider;
import com.mcmiddleearth.entities.provider.SyncBlockProvider;
import org.bukkit.util.Vector;

public class WalkingPathfinder implements Pathfinder{

    private final VirtualEntity entity;

    private final BlockProvider blockProvider;

    private Vector target;

    private final int maxPathLength = 20;

    public WalkingPathfinder(VirtualEntity entity) {
        this.entity = entity;
        this.blockProvider = new SyncBlockProvider(entity.getLocation().getWorld());
    }

    @Override
    public Path getPath(Vector start) {

        return null;
    }

    @Override
    public void setTarget(Vector target) {
        this.target = target;
    }
}
