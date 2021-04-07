package com.mcmiddleearth.entities.ai.goals;

import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;

public class FollowEntityGoal extends EntityTargetingGoal {

    public FollowEntityGoal(GoalType type, VirtualEntity entity, Pathfinder pathfinder, McmeEntity target) {
        super(type, entity, pathfinder, target);
    }

    @Override
    public void updatePath() {
        getPathfinder().setTarget(getTarget().getLocation().toVector());
        findPath(getEntity().getLocation().toVector());
    }

    @Override
    public void updateTick() {
        //calculate Waypoint
    }
}
