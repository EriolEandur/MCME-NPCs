package com.mcmiddleearth.entities.ai.pathfinding;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.provider.BlockProvider;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class WalkingPathfinder implements Pathfinder{

    private final VirtualEntity entity;

    private final BlockProvider blockProvider;

    private Vector target;

    private int maxPathLength = 50;

    private PathMarker leaveWall, current;
    private int leaveWallIndex = -1;

    private boolean followRightSideWall;

    private Path path;

    boolean fail;
    int step;
    public WalkingPathfinder(VirtualEntity entity) {
        this.entity = entity;
        this.target = getBlockCenterXZ(entity.getLocation().toVector());
        this.blockProvider = EntitiesPlugin.getEntityServer().getBlockProvider(entity.getLocation().getWorld().getUID());
    }

    public void setMaxPathLength(int maxPathLength) {
        this.maxPathLength = maxPathLength;
    }

    @Override
    public Path getPath(Vector start) {
//Logger.getGlobal().info("findPath start: "+start);
//Logger.getGlobal().info("findPath target: "+target);
        path = new Path(target);
        current = new PathMarker(0, getBlockCenterXZ(start));
        step = 0;
        fail = false;
        while(!path.isComplete() && !fail && step < maxPathLength) {
            if(path.contains(current.getPoint()) && leaveWall != null) {
                path.shortcut(current.getPoint(), leaveWallIndex);
                leaveWallIndex = -1;
                current = new PathMarker(leaveWall);//start = path.get(path.size()-1);
//Logger.getGlobal().info("current after shortcut: "+current.getPoint());
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
//Logger.getGlobal().info("current: "+current.getPoint()+" - "+current.getRotation());
            } else {
//Logger.getGlobal().info("follow Wall");
                //followRight = false;
                current.turn(!followRightSideWall);
                followWall(followRightSideWall);
            }
        }
        //if(path.isComplete()) {
//path.getPoints().forEach(vector -> {
//    System.out.println("x: " + vector.getX() + " y: " + vector.getY() + " z: " + vector.getZ());}
//);
            path.optimise(entity.getJumpHeight(), entity.getFallDepth());
/*Logger.getGlobal().info("Path");
path.getPoints().forEach(vector -> {
    System.out.println("x: " + vector.getBlockX() + " y: " + vector.getBlockY() + " z: " + vector.getBlockZ());}
);*/

            return path;
        //} else {
        //    return null;
        //}
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
        if(Math.abs(diff.getX())>Math.abs(diff.getZ())) {
            if(diff.getX()>0) {
                return 0;
            } else {
                return 180;
            }
        } else {
            if(diff.getZ()>0) {
                return 90;
            } else {
                return 270;
            }
        }
    }

    private void moveMarker(PathMarker next) {
        next.move(next.getPoint().getY());
        double blockY = blockProvider.blockTopY(next.getPoint().getBlockX(),next.getPoint().getBlockY(),next.getPoint().getBlockZ(),
                                                entity.getJumpHeight()+1);
        next.getPoint().setY(blockY);
    }

    private boolean canMove(PathMarker next) {
//Logger.getGlobal().info("CanMove: "+current.getPoint().getBlockX()+" "+current.getPoint().getBlockZ()+" "
//                          +next.getPoint().getBlockX()+" "+next.getPoint().getBlockZ()+" "
//                          +current.getPoint().getY() +" - "+next.getPoint().getY());
        return next.getPoint().getY() - current.getPoint().getY() <= entity.getJumpHeight()
                && current.getPoint().getY() - next.getPoint().getY() <= entity.getFallDepth();
    }

    @Override
    public void setTarget(Vector target) {
        this.target = target;
    }

    @Override
    public Vector getTarget() {
        return target;
    }

    private Vector getBlockCenterXZ(Vector vector) {
        return new Vector(vector.getBlockX()+0.5,vector.getY(), vector.getBlockZ()+0.5);
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
