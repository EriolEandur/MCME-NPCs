package com.mcmiddleearth.entities.ai.pathfinding;

import com.mcmiddleearth.entities.util.TriFunction;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RayTracer<T> {

    private List<Ray> rays = new ArrayList<>();

    private RayTraceResultColumn current;

    private RayTraceResultColumn next;

    private final int stepX, stepZ;
    private int blockX, lastStartZ;
    private final int  blockStartX, blockEndX;

    private final Vector start;
    private final Vector traceVector;

    private final double mz, myx, myz;

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
        blockStartX = start.getBlockX();
        blockEndX = getBlock(start.getX()+traceVector.getX());
        blockX = blockStartX;
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
        } else {
            next = null;
        }
        //result = new RayTraceResultColumn[blockEndX- blockX +1];
        //while(blockX != blockEndX) {
    }

    private RayTraceResultColumn createResultColumn() {
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Ray ray : rays) {
            int rayX = (stepX>0?blockX+1:blockX);
            int rayY = getBlock(ray.getY(rayX));
            int rayZ = getBlock(ray.getZ(rayX));
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
        int startZ, lengthZ;
        if(stepZ>0) {
            startZ = Math.min(minZ, lastStartZ);
            if(blockX == blockStartX) {
                startZ = start.getBlockZ();
            }
            if(blockX == blockEndX) {
                maxZ = getBlock(start.getBlockZ()+traceVector.getZ());
            }
            lengthZ = maxZ - startZ + 1;
        } else {
            startZ = Math.max(maxZ, lastStartZ);
            if(blockX == blockStartX) {
                startZ = start.getBlockZ();
            }
            if(blockX == blockEndX) {
                minZ = getBlock(start.getBlockZ()+traceVector.getZ());
            }
            lengthZ = startZ - minZ +1;
        }
        lastStartZ = startZ;
Logger.getGlobal().info("blockStartX:"+blockStartX+" blockX:"+blockX+" blockEndX:"+blockEndX);
Logger.getGlobal().info("minZ:"+minZ+" maxZ"+maxZ+" startZ:"+startZ+" lenngthZ:"+lengthZ);
        int[] blockYs = new int[lengthZ];
        for(int i = 0; i < blockYs.length; i++) {
            blockYs[i] = maxY + getBlock(myz * i);
        }
        return new RayTraceResultColumn(blockX, startZ, blockYs);
    }

    public void addRay(Vector start) {
        rays.add(new Ray(start));
    }

    public int first() {
        return blockStartX;
    }

    public int last() {
        return blockEndX;//start.getBlockX()+ stepX*(rays.size()-1);
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

    private int getBlock(double cord) {
        return (int) Math.floor(cord);
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
