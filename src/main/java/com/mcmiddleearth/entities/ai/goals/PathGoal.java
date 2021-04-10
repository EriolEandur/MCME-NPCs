package com.mcmiddleearth.entities.ai.goals;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.pathfinding.Path;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.ai.pathfinding.RayTracer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.util.BoundingBox;
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

    public Vector getDirection() {
        if(path == null || waypoint == null) {
            return null;
        } else {
            return waypoint.clone().subtract(getEntity().getLocation().toVector());
        }
    }

    public void updateWaypoint() {
        if(path!=null && path.getEnd()!=null) {
            int index = path.length()-1;
            while(!isDirectWayClear(path.get(index)) && index >= 0) {
                index --;
            }
            waypoint = path.get(index).clone();
        }
    }

    private boolean isDirectWayClear(Vector target) {
        Vector targetDirection = target.clone().subtract(getEntity().getLocation().toVector());
        RayTracer<Double> tracer = new RayTracer<>(getEntity().getLocation().toVector(),targetDirection,
                (x,y,z) -> EntitiesPlugin.getEntityServer().getBlockProvider(getEntity().getLocation().getWorld().getUID())
                                       .blockTopY(x,y,z));
        BoundingBox boundingBox = getEntity().getBoundingBox().getBoundingBox();
        int jumpHeight = getEntity().getJumpHeight();
        tracer.addRay(new Vector(boundingBox.getMinX(),boundingBox.getMinY(),boundingBox.getMinZ()));
        tracer.addRay(new Vector(boundingBox.getMinX(),boundingBox.getMinY(),boundingBox.getMaxZ()));
        tracer.addRay(new Vector(boundingBox.getMaxX(),boundingBox.getMinY(),boundingBox.getMinZ()));
        tracer.addRay(new Vector(boundingBox.getMaxX(),boundingBox.getMinY(),boundingBox.getMaxZ()));
        tracer.addRay(new Vector(boundingBox.getMinX(),boundingBox.getMaxY(),boundingBox.getMinZ()));
        tracer.addRay(new Vector(boundingBox.getMinX(),boundingBox.getMaxY(),boundingBox.getMaxZ()));
        tracer.addRay(new Vector(boundingBox.getMaxX(),boundingBox.getMaxY(),boundingBox.getMinZ()));
        tracer.addRay(new Vector(boundingBox.getMaxX(),boundingBox.getMaxY(),boundingBox.getMaxZ()));
        //tracer.trace();
        for(int i = tracer.first(); i < tracer.last(); i += tracer.stepX()) {
            tracer.traceStep();
            RayTracer<Double>.RayTraceResultColumn current = tracer.current();//get(i);
            RayTracer<Double>.RayTraceResultColumn next = tracer.next();//get(i+1);
            for(int j = current.first(); j < current.last(); j += tracer.stepZ()) {
                if(current.get(j+1)-current.get(j)>jumpHeight
                    || (next!=null && (next.has(j) && next.get(j)-current.get(j) > jumpHeight))) {
                    return false;
                }

            }
        }
        return true;
    }
}
