package com.mcmiddleearth.entities.ai.goals;

import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;

public abstract class LocationTargetingGoal extends PathGoal {

    public Location target;

    public LocationTargetingGoal(GoalType type, VirtualEntity entity, Pathfinder pathfinder, Location target) {
        super(type, entity, pathfinder);
        this.target = target;
    }
}
