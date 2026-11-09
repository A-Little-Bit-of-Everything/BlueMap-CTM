package net.notaglitch.bluemap_ctm.layout;

import de.bluecolored.bluemap.core.util.Direction;

public enum FaceOrientation {

    DOWN(0, 0, 1, 1, 0, 0),
    UP(0, 0, -1, 1, 0, 0),
    NORTH(0, 1, 0, -1, 0, 0),
    SOUTH(0, 1, 0, 1, 0, 0),
    WEST(0, 1, 0, 0, 0, 1),
    EAST(0, 1, 0, 0, 0, -1);

    public final int upX, upY, upZ;
    public final int rightX, rightY, rightZ;

    FaceOrientation(int upX, int upY, int upZ, int rightX, int rightY, int rightZ) {
        this.upX = upX;
        this.upY = upY;
        this.upZ = upZ;
        this.rightX = rightX;
        this.rightY = rightY;
        this.rightZ = rightZ;
    }

    public static FaceOrientation fromNormal(float nx, float ny, float nz) {
        float absX = Math.abs(nx);
        float absY = Math.abs(ny);
        float absZ = Math.abs(nz);

        if (absY >= absX && absY >= absZ) {
            return ny > 0 ? UP : DOWN;
        } else if (absZ >= absX && absZ >= absY) {
            return nz > 0 ? SOUTH : NORTH;
        } else {
            return nx > 0 ? EAST : WEST;
        }
    }

    public static FaceOrientation fromDirection(Direction dir) {
        return switch (dir) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    public int getTopDx() { return upX; }
    public int getTopDy() { return upY; }
    public int getTopDz() { return upZ; }

    public int getBottomDx() { return -upX; }
    public int getBottomDy() { return -upY; }
    public int getBottomDz() { return -upZ; }

    public int getRightDx() { return rightX; }
    public int getRightDy() { return rightY; }
    public int getRightDz() { return rightZ; }

    public int getLeftDx() { return -rightX; }
    public int getLeftDy() { return -rightY; }
    public int getLeftDz() { return -rightZ; }

    public int getTopRightDx() { return upX + rightX; }
    public int getTopRightDy() { return upY + rightY; }
    public int getTopRightDz() { return upZ + rightZ; }

    public int getBottomRightDx() { return -upX + rightX; }
    public int getBottomRightDy() { return -upY + rightY; }
    public int getBottomRightDz() { return -upZ + rightZ; }

    public int getBottomLeftDx() { return -upX - rightX; }
    public int getBottomLeftDy() { return -upY - rightY; }
    public int getBottomLeftDz() { return -upZ - rightZ; }

    public int getTopLeftDx() { return upX - rightX; }
    public int getTopLeftDy() { return upY - rightY; }
    public int getTopLeftDz() { return upZ - rightZ; }

}
