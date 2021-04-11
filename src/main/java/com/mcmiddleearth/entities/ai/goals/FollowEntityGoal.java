package com.mcmiddleearth.entities.ai.goals;

import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;

public class FollowEntityGoal extends EntityTargetingGoal {

    public FollowEntityGoal(GoalType type, VirtualEntity entity, Pathfinder pathfinder, McmeEntity target) {
        super(type, entity, pathfinder, target);
    }

    //todo: stop movement when close to target
    @Override
    public void doTick() {
        super.doTick();
        if(isCloseToTarget()) {
            deletePath();
            setRotation(getEntity().getLocation().clone().setDirection(getTarget().getLocation().toVector()
                                                         .subtract(getEntity().getLocation().toVector())).getYaw());
        }
    }

    @Override
    public void update() {
        if(!isCloseToTarget()) {
            super.update();
        }
    }

    private boolean isCloseToTarget() {
        return getEntity().getLocation().toVector().distanceSquared(getTarget().getLocation().toVector()) < 4;
    }
}
