package com.mcmiddleearth.entities.ai.pathfinding;

import com.mcmiddleearth.entities.provider.ChunkProvider;
import org.bukkit.util.Vector;

public class WalkingPathfinder implements Pathfinder{

    private ChunkProvider chunkProvider;

    private Vector target;

    public WalkingPathfinder(ChunkProvider chunkProvider) {
        this.chunkProvider = chunkProvider;
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
