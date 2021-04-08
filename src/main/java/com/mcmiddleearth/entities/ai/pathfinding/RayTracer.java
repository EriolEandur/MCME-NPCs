package com.mcmiddleearth.entities.ai.pathfinding;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RayTracer {

    private List<Ray> rays = new ArrayList<>();

    private RayTraceResultColumn[] result;

    private final int stepX, stepZ;

    private final Vector start;
    private final Vector targetDirection;

    private double mz;
    private double my;

    public RayTracer(Vector start, Vector targetDirection) {
        this.start = start;
        this.targetDirection = targetDirection;
        stepX = (targetDirection.getX()>0?1:-1);
        stepZ = (targetDirection.getZ()>0?1:-1);
    }

    public void trace() {
        mz = targetDirection.getZ() / targetDirection.getX();
        my = targetDirection.getY() / targetDirection.getX();

        double xStart = getEntity().getLocation().getX();
        double xEnd = path.getEnd().getX();
        for(int i = (int) xStart; i < (int) xEnd; i++) {

        }
    }

    public void addRay(Vector start) {
        rays.add(new Ray(start, targetDirection));
    }

    public int first() {
        return start.getBlockX();
    }

    public int last() {
        return start.getBlockX()+ stepX*(rays.size()-1);
    }

    public int stepX() {
        return stepX;
    }

    public int stepZ() {
        return stepZ;
    }

    public RayTraceResultColumn get(int i) {
        return result[(i-start.getBlockX())*stepX];
    }

    public class RayTraceResultColumn {

        private final int startZ;

        private final double[] rows;

        public RayTraceResultColumn(int startZ, double[] rows) {
            this.rows = rows;
            this.startZ = startZ;
        }
        public int first() {
            return startZ;
        }

        public int last() {
            return startZ + stepZ * (rows.length-1);
        }

        public double get(int j) {
            return rows[(j-startZ)*stepZ];
        }

        public boolean has(int j) {
            int index = (j-startZ)*stepZ;
            return index >= 0 && index < rows.length;
        }
    }

    public class Ray {

        Vector start;

        public Ray(Vector start) {
            this.start = start;
        }
    }
}
