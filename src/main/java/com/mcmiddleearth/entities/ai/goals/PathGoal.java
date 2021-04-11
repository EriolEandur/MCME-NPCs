package com.mcmiddleearth.entities.ai.goals;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.pathfinding.Path;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.ai.pathfinding.RayTracer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.logging.Logger;

public abstract class PathGoal extends VirtualEntityGoal {

    private Path path;

    private Vector waypoint;

    private final Pathfinder pathfinder;

    private boolean hasRotation;
    private float rotation;

    public PathGoal(GoalType type, VirtualEntity entity, Pathfinder pathfinder) {
        super(type, entity);
        this.pathfinder = pathfinder;
    }

    @Override
    public void update() {
//Logger.getGlobal().info("target "+getEntity().getLocation().toVector().getBlockX()+" "
   //                                 +getEntity().getLocation().toVector().getBlockY()+" "
     //                               +getEntity().getLocation().toVector().getBlockZ());
        findPath(getEntity().getLocation().toVector());
        updateWaypoint();
        if(waypoint!=null) {
            hasRotation = true;
Logger.getGlobal().info("waypoint "+waypoint);
            rotation = getEntity().getLocation().clone().setDirection(waypoint).getYaw();
        }
    }

    @Override
    public void doTick() {
        hasRotation = false;
        if(waypoint != null && getEntity().getLocation().toVector().distanceSquared(waypoint) < 1) {
            path.setStart(waypoint);
            updateWaypoint();
        }
    }

    public void deletePath() {
        path = null;
        waypoint = null;
    }

    public void setPathTarget(Vector target) {
        pathfinder.setTarget(target);
    }
    //public Pathfinder getPathfinder() {
    //    return pathfinder;
    //}

    public void findPath(Vector start) {
        path = pathfinder.getPath(start);
    }

    public Vector getDirection() {
        if(path == null || waypoint == null) {
            return null;
        } else {
            return waypoint.clone().subtract(getEntity().getLocation().toVector());
        }
    }

    @Override
    public boolean hasRotation() {
        return hasRotation;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
//Logger.getGlobal().info("*                           rotation: "+rotation);
        this.rotation = rotation;
        hasRotation = true;
    }

    public void updateWaypoint() {
Logger.getGlobal().info("update Waypoint: start "+path+" "+path.getEnd());
        if(path!=null && path.getEnd()!=null) {
            int index = path.length()-1;
            while(!isDirectWayClear(path.get(index)) && index >= 0) {
Logger.getGlobal().info("update Waypoint: "+index);
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
Logger.getGlobal().info("Tracer: "+tracer.first()+" *** "+ tracer.last() + " *** "+tracer.stepX());
        for(int i = tracer.first(); i != tracer.last(); i += tracer.stepX()) {
Logger.getGlobal().info("trace step");
            tracer.traceStep();
            RayTracer<Double>.RayTraceResultColumn current = tracer.current();//get(i);
            RayTracer<Double>.RayTraceResultColumn next = tracer.next();//get(i+1);
Logger.getGlobal().info("Current: "+current.first()+" *** "+ current.last() + " *** "+tracer.stepZ());
            for(int j = current.first(); j != current.last(); j += tracer.stepZ()) {
Logger.getGlobal().info("compare: "+current.get(j+1)+" - "+current.get(j));
if(next != null && next.has(j)) {
    Logger.getGlobal().info("                              next: "+next.get(j)+" - "+current.get(j));
}
                if(current.get(j+1)-current.get(j)>jumpHeight
                    || (next!=null && (next.has(j) && next.get(j)-current.get(j) > jumpHeight))) {
                    return false;
                }

            }
        }
        return true;
    }

    public Path getPath() {
        return path;
    }
}
