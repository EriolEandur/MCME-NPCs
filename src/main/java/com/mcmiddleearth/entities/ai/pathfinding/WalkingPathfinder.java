package com.mcmiddleearth.entities.ai.pathfinding;

import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.provider.BlockProvider;
import com.mcmiddleearth.entities.provider.SyncBlockProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

public class WalkingPathfinder implements Pathfinder{

    private final VirtualEntity entity;

    private final BlockProvider blockProvider;

    private Vector target;

    private int maxPathLength = 200;

    private PathMarker leaveWall, current;
    private int leaveWallIndex = -1;

    private boolean followRightSideWall;

    private final Random random;

    private Path path;

    boolean fail;
    int step;
    public WalkingPathfinder(VirtualEntity entity) {
        this.entity = entity;
        this.target = entity.getLocation().toVector();
        this.blockProvider = new SyncBlockProvider(entity.getLocation().getWorld());
        this.random = new Random();
    }

    public void setMaxPathLength(int maxPathLength) {
        this.maxPathLength = maxPathLength;
    }

    @Override
    public Path getPath(Vector start) {
        fail = false;
        path = new Path(target);
        current = new PathMarker(0,start);
        step = 0;
        while(!path.isComplete() && !fail && step < maxPathLength) {
            if(path.contains(current.getPoint()) && leaveWall != null) {
                path.shortcut(current.getPoint(), leaveWallIndex);
                leaveWallIndex = -1;
                current = new PathMarker(leaveWall);//start = path.get(path.size()-1);
                //this.setLocation(start.x, start.y);
                //this.setRotation(leaveWallRotation);
                // nachricht("rotation: "+this.getRotation()+"   x: "+getX()+" y: "+getY());
                followWall(followRightSideWall);
            }

            current.setRotation(getTargetDirection(current.getPoint()));
//Logger.getGlobal().info("rotation direct: "+current.getRotation());
            PathMarker next = new PathMarker(current);
            moveMarker(next);
            if(canMove(next)) {
                path.addPoint(current.getPoint());
//Logger.getGlobal().info("added direct "+current.toString());
//entity.getLocation().getWorld().dropItem(new Location(entity.getLocation().getWorld(),current.getPoint().getX(),
//                current.getPoint().getY(),
//                current.getPoint().getZ()),new ItemStack(Material.STONE));
                step++;
                current.move(next.getPoint().getY());
            } else {
                followRightSideWall = random.nextBoolean();
                //followRight = false;
                current.turn(!followRightSideWall);
                followWall(followRightSideWall);
            }
        }
        if(path.isComplete()) {
            path.optimise(entity.getJumpHeight(), entity.getFallDepth());
            path.getPoints().forEach(vector -> {
                entity.getLocation().getWorld().dropItem(new Location(entity.getLocation().getWorld(),
                                                                       vector.getX(),
                                                                       vector.getY(),
                                                                       vector.getZ()), new ItemStack(Material.BEACON));
                System.out.println("x: " + vector.getX() + " y: " + vector.getY() + " z: " + vector.getZ());}
            );

            return path;
        } else {
            return null;
        }
    }

    public void followWall(boolean rightSide) {
        int rotation, oldRotation, val1, val2;
        do {
            path.addPoint(current.getPoint());
//Logger.getGlobal().info("added wall "+current.toString());
//entity.getLocation().getWorld().dropItem(new Location(entity.getLocation().getWorld(),current.getPoint().getX(),
//                                                                                      current.getPoint().getY(),
//                                                                                      current.getPoint().getZ()),
//        new ItemStack(Material.STONE));
            step++;
            PathMarker next = new PathMarker(current);
            moveMarker(next);
            current.move(next.getPoint().getY());
            int targetDirection = getTargetDirection(current.getPoint());
            oldRotation = current.getRotation()-targetDirection;
            current.turn(rightSide);
            next = new PathMarker(current);
            moveMarker(next);
            int i = 0;
            while(!canMove(next) && i < 4) {
                current.turn(!rightSide);
                next = new PathMarker(current);
                moveMarker(next);
                i++;
            }
            if(i == 4) {
                fail = true;
                return;
            }
            rotation = current.getRotation()-targetDirection;
            //nachricht("rotation: "+rotation+" alteM: "+alteMarke);
            if(rightSide) {
                val1 = 90;
                val2 = -270;
            } else {
                val1 = -90;
                val2 = 270;
            }
        } while(step < maxPathLength && !path.isComplete()
                && !(( rotation == val1 || rotation== val2
                || oldRotation == val1 || oldRotation== val2)
                && !path.contains(current.getPoint())));
        leaveWallIndex = path.length()-1; //one too early?
        leaveWall = new PathMarker(current);
    }

    public int getTargetDirection(Vector start) {
        Vector diff = target.clone().subtract(start);
        if(Math.abs(diff.getBlockX())>Math.abs(diff.getBlockZ())) {
            if(diff.getBlockX()>0) {
                return 0;
            } else {
                return 180;
            }
        } else {
            if(diff.getBlockZ()>0) {
                return 90;
            } else {
                return 270;
            }
        }
    }

    private void moveMarker(PathMarker next) {
        next.move(next.getPoint().getY());
        double blockY = blockY(next.getPoint());
        next.getPoint().setY(blockY);
    }

    private boolean canMove(PathMarker next) {
//Logger.getGlobal().info("CanMove: "+next.getPoint().getY() +" - "+current.getPoint().getY());
        return next.getPoint().getY() - current.getPoint().getY() <= entity.getJumpHeight()
                && current.getPoint().getY() - next.getPoint().getY() <= entity.getFallDepth();
    }

    private double blockY(Vector point) {
        int x = point.getBlockX();
        int y = point.getBlockY();
        int z = point.getBlockZ();
        if (!blockProvider.isPassable(x, y, z)) {
            do {
                y++;
            } while (!blockProvider.isPassable(x, y, z));
            y--; //y at lowest non-passable block;
        } else {
            do {
                y--;
            } while (blockProvider.isPassable(x, y, z));
        }
        return blockProvider.getBoundingBox(x,y,z).getMaxY();
    }

    @Override
    public void setTarget(Vector target) {
        this.target = target;
    }

    public static class PathMarker {

        public static final BlockFace[] DIRECTIONS = new BlockFace[]{BlockFace.EAST,BlockFace.SOUTH,BlockFace.WEST,BlockFace.NORTH};

        private int directionIndex;
        private BlockFace direction;

        private int rotation;
        private final Vector point;

        public PathMarker(int rotation, Vector point) {
            this.rotation = rotation;
            this.point = new Vector(point.getX(), point.getY(), point.getZ());
        }

        public PathMarker(PathMarker marker) {
            this.rotation = marker.rotation;
            this.point = marker.getPoint().clone();
            this.directionIndex = marker.directionIndex;
            this.direction = marker.direction;
        }

        public BlockFace getDirection() {
            return direction;
        }

        public int getRotation() {
            return rotation;
        }

        public void setRotation(int rotation) {
            this.rotation = rotation;
        }

        public Vector getPoint() {
            return point;
        }

        public void turn(boolean right) {
            int angle = (right?90:-90);
            rotation = rotation + angle;
            while(rotation<0) {
                rotation+=360;
            }
            while(rotation>=360) {
                rotation -=360;
            }
        }

        public void uTurn() {
            rotation = rotation + 180;
            while(rotation>=360) {
                rotation -=360;
            }
        }

        public void move(double nextY) {
            switch(rotation) {
                case 0: point.add(new Vector(1,0,0));
                    break;
                case 90: point.add(new Vector(0,0,1));
                    break;
                case 180: point.add(new Vector(-1,0,0));
                    break;
                default: point.add(new Vector(0,0,-1));
                    break;
            }
            point.setY(nextY);
        }

        public String toString() {
            return "x: "+point.getX()+" y: "+point.getY()+" z: "+point.getZ();
        }
    }
}
