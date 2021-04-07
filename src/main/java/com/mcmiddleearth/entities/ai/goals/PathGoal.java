package com.mcmiddleearth.entities.ai.goals;

import com.mcmiddleearth.entities.ai.pathfinding.Path;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.util.Vector;

public abstract class PathGoal extends VirtualEntityGoal {

    private Path path;

    private Vector waypoint;

    private final Pathfinder pathfinder;

    public PathGoal(GoalType type, VirtualEntity entity, Pathfinder pathfinder) {
        super(type, entity);
        this.pathfinder = pathfinder;
    }

    public Pathfinder getPathfinder() {
        return pathfinder;
    }

    public void findPath(Vector start) {
        path = getPathfinder().getPath(start);
    }

    public Vector getWaypointDirection() {
        if(path == null || waypoint == null) {
            return null;
        } else {
            return waypoint.clone().subtract(getEntity().getLocation().toVector());
        }
    }

    public void updateWaypoint() {
        if(path!=null && path.getEnd()!=null) {
            Vector targetDirection = path.getEnd().clone().subtract(getEntity().getLocation().toVector());

    }
}
