package com.mcmiddleearth.entities.ai.goals;

import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;

public abstract class EntityTargetingGoal extends PathGoal {

    McmeEntity target;

    public EntityTargetingGoal(GoalType type, VirtualEntity entity, Pathfinder pathfinder, McmeEntity target) {
        super(type, entity, pathfinder);
        this.target = target;
    }

    public McmeEntity getTarget() {
        return target;
    }
}
