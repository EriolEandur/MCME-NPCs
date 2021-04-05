package com.mcmiddleearth.entities.provider;

import org.bukkit.Location;
import org.bukkit.block.BlockState;

public interface ChunkProvider {

    public BlockState getBlockAt(Location location);

}
