package com.mcmiddleearth.entities.ai.pathfinding;

import org.bukkit.util.Vector;

import java.util.*;

public class Path {

    private List<Vector> ordered = new ArrayList<>();

    private Set<Vector> unordered = new HashSet<>();

    private Vector start;
    private final Vector target;

    public Path(Vector target) {
        this.target = target;
    }

    public void  addPoint(Vector point) {
        ordered.add(point);
        unordered.add(point);
    }

    public List<Vector> getPoints() {
        return ordered;
    }

    public Vector get(int i) {
        return ordered.get(i);
    }

    public boolean isComplete() {
        if(ordered.isEmpty()) {
            return false;
        }
        Vector last = ordered.get(ordered.size()-1);
        return  target.getBlockX() == last.getBlockX()
             && target.getBlockY() == last.getBlockY()
             && target.getBlockZ() == last.getBlockZ();
    }

    /**
     * Truncates this path to start at given vector. If vector isn't a point on this path nothing happens.
     * @param start new start point of this path
     */
    public void setStart(Vector start) {
        if(this.contains(start)) {
            while(!ordered.get(0).equals(start)) {
                unordered.remove(ordered.get(0));
                ordered.remove(0);
            }
            this.start = start;
            //this.unordered = new HashSet<>(ordered);
        }
    }

    public Vector getStart() {
        return start;
    }

    public Vector getEnd() {
        if(ordered.isEmpty()) {
            return null;
        }
        return ordered.get(ordered.size()-1);
    }

    /*public Vector getWayPoint() {
        if(ordered.isEmpty()) {
            return null;
        }
        Vector first = ordered.get(0);

        return null;
    }*/

    public int length() {
        return ordered.size();
    }

    public boolean contains(Vector point) {
        return unordered.contains(point);
    }

    public void shortcut(Vector shortcutStart, int endIndex) {
        //Vector start = new Vector(getX(),getY());
        List<Vector> newPoints = new ArrayList<>();
        int index = 0;
        while(!ordered.get(index).equals(shortcutStart)) {
            newPoints.add(ordered.get(index));
            index++;
        }
        newPoints.add(ordered.get(index));
        index++;
        while(index < ordered.size()) {
            Vector temp = ordered.get(index);
            index++;
        }
        index = ordered.size()-1;
        while(index > endIndex) {
            newPoints.add(ordered.get(index));
            index--;
        }
        ordered = newPoints;
        unordered = new HashSet<>(ordered);
    }

    public void optimise(int jumpHeight, int fallDepth) {
        int startIndex = 0;
        while(startIndex < ordered.size()-1) {
            int endIndex = ordered.size()-1;
            Vector startVector = ordered.get(startIndex);
            while(endIndex > startIndex) {
                if(isClose(startVector, ordered.get(endIndex),jumpHeight,fallDepth)) {
                    boolean equal = startVector.equals(ordered.get(endIndex));
                    List<Vector> newPoints = new ArrayList<>();
                    for(int i = 0; i < startIndex; i++) {
                        newPoints.add(ordered.get(i));
                    }
                    if(!equal) {
                        newPoints.add(startVector);
                    }
                    for(int i = endIndex; i < ordered.size(); i++) {
                        newPoints.add(ordered.get(i));
                    }
                    ordered = newPoints;
                    unordered = new HashSet<>(ordered);
                    break;
                }
                endIndex--;
            }
            startIndex++;
        }
    }

    private boolean isClose(Vector from, Vector to, int jumpHeight, int fallDepth) {
        return to.getBlockY() - from.getBlockY() <= jumpHeight && from.getBlockY()-to.getBlockY() <= fallDepth
              && ((from.getBlockX() == to.getBlockX() && from.getBlockZ() <= to.getBlockZ()+1 && from.getBlockZ() >= to.getBlockZ()-1)
                || (from.getBlockZ() == to.getBlockZ() && from.getBlockX() <= to.getBlockX()+1 && from.getBlockX() >= to.getBlockX()-1));
    }


}
