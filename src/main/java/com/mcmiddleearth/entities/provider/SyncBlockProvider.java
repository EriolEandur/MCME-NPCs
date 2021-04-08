package com.mcmiddleearth.entities.provider;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.BoundingBox;

public class SyncBlockProvider implements BlockProvider {

    private final World world;

    public SyncBlockProvider(World world) {
        this.world = world;
    }

    @Override
    public BlockData getBlockDataAt(Location location) {
        return world.getBlockAt(location).getBlockData();
    }

    @Override
    public boolean isPassable(int x, int y, int z) {
        return world.getBlockAt(x, y, z).isPassable();
    }

    @Override
    public BoundingBox getBoundingBox(int x, int y, int z) {
        return world.getBlockAt(x,y,z).getBoundingBox();
    }

    @Override
    public double blockTopY(int x, int y, int z) {
        if (!isPassable(x, y, z)) {
            do {
                y++;
            } while (!isPassable(x, y, z));
            y--; //y at lowest non-passable block;
        } else {
            do {
                y--;
            } while (isPassable(x, y, z));
        }
        return getBoundingBox(x,y,z).getMaxY();
    }


}
