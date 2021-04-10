package com.mcmiddleearth.entities.ai.goals;

import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class FollowEntityGoal extends EntityTargetingGoal {

    public FollowEntityGoal(GoalType type, VirtualEntity entity, Pathfinder pathfinder, McmeEntity target) {
        super(type, entity, pathfinder, target);
    }

    @Override
    public void updatePath() {
        getPathfinder().setTarget(getTarget().getLocation().toVector());
        findPath(getEntity().getLocation().toVector());
        updateWaypoint();
        rotation = true;
        yaw = getEntity().getLocation().clone().setDirection(getDirection()).getYaw();
    }

    @Override
    public void updateTick() {
        rotation = false;
        headRotation = true;
        Location targetDir = getEntity().getLocation().clone()
                                        .setDirection(target.getLocation().toVector()
                                                            .subtract(getEntity().getLocation().toVector()));
        headPitch = targetDir.getPitch();
        headYaw = targetDir.getYaw();
        //head rotation
    }
}
