package com.mcmiddleearth.entities.ai.pathfinding;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Path {

    private final List<Vector> points = new ArrayList<>();

    public Path(Vector start, Vector target) {

    }

    public List<Vector> getPoints() {
        return points;
    }

    /**
     * Truncates this path to start at given vector. If vector isn't a point on this path nothing happens.
     * @param start new start point of this path
     */
    public void setStart(Vector start) {

    }

    public Vector getWayPoint() {
        return null;
    }

    public int length() {
        return points.size();
    }

}
