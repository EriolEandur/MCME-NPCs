package com.mcmiddleearth.entities.ai.pathfinding;

import com.mcmiddleearth.entities.util.TriFunction;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RayTracer<T> {

    private List<Ray> rays = new ArrayList<>();

    private RayTraceResultColumn current;

    private RayTraceResultColumn next;

    private final int stepX, stepZ;
    private int blockX;
    private final int  blockEndX;

    private final Vector start;
    private final Vector traceVector;

    private double mz, myx, myz;

    TriFunction<Integer, Integer, Integer, T> calculator;

    public RayTracer(Vector start, Vector traceVector, TriFunction<Integer, Integer, Integer, T> calculator) {
        this.calculator = calculator;
        this.start = start;
        this.traceVector = traceVector;
        stepX = (traceVector.getX()>0?1:-1);
        stepZ = (traceVector.getZ()>0?1:-1);
        mz = traceVector.getZ() / traceVector.getX();
        myx = traceVector.getY() / traceVector.getX();
        myz = traceVector.getY() / traceVector.getZ();
        if(stepX<0) {
            blockX = start.getBlockX();
            blockEndX = (int) (start.getX()+traceVector.getX())+1;
        } else {
            blockX = start.getBlockX() + 1;
            blockEndX = (int) (start.getX()+traceVector.getX());
        }
    }

    public void traceStep() {
        if(current == null) {
            current = createResultColumn();
        } else {
            current = next;
        }
        blockX += stepX;
        if(blockX <= blockEndX) {
            next = createResultColumn();
        }
        //result = new RayTraceResultColumn[blockEndX- blockX +1];
        //while(blockX != blockEndX) {
    }

    private RayTraceResultColumn createResultColumn() {
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Ray ray : rays) {
            int rayY = (int) ray.getY(blockX);
            int rayZ = (int) ray.getZ(blockX);
            if (rayY > maxY) {
                maxY = rayY;
            }
            if (rayZ > maxZ) {
                maxZ = rayZ;
            }
            if (rayZ < minZ) {
                minZ = rayZ;
            }
        }
        int[] blockYs = new int[maxZ - minZ +1];
        for(int i = 0; i < blockYs.length; i++) {
            blockYs[i] = maxY + (int) (myz * i);
        }
        return new RayTraceResultColumn(blockX, minZ, blockYs);
    }

    public void addRay(Vector start) {
        rays.add(new Ray(start));
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

    /*public RayTraceResultColumn get(int i) {
        return result[(i-start.getBlockX())*stepX];
    }*/

    public RayTraceResultColumn current() {
        return current;
    }

    public RayTraceResultColumn next() {
        return next;
    }

    private T calculateValue(int x, int y, int z) {
        return calculator.apply(x,y,z);
    }

    public class RayTraceResultColumn {

        private final int blockX;

        private final int startZ;

        private final List<T> rows = new ArrayList<>();

        private final int[] blockYs;

        public RayTraceResultColumn(int blockX, int startZ, int[] blockYs) {
            this.blockYs = blockYs;
            for(int y: blockYs) this.rows.add(null);
            this.startZ = startZ;
            this.blockX = blockX;
        }

        public int first() {
            return startZ;
        }

        public int last() {
            return startZ + stepZ * (blockYs.length-1);
        }

        public T get(int j) {
            int index = (j-startZ)*stepZ;
            if (rows.get(index)==null) {
                rows.set(index, calculateValue(blockX,j,blockYs[index]));
            }
            return rows.get(index);
        }

        public boolean has(int j) {
            int index = (j-startZ)*stepZ;
            return index >= 0 && index < blockYs.length;
        }
    }

    public class Ray {

        Vector start;

        public Ray(Vector start) {
            this.start = start;
        }

        public double getY(double x) {
            return start.getY()+ myx * (x - start.getX());
        }

        public double getZ(double x) {
            return start.getZ()+mz * (x - start.getX());
        }

    }

}
