package net.notaglitch.bluemap_ctm.util;

import de.bluecolored.bluemap.core.map.hires.ArrayTileModel;
import de.bluecolored.bluemap.core.map.hires.TileModel;

import java.lang.reflect.Field;

public class ArrayTileModelAccessor {

    private static Field posField;
    private static Field uvField;

    static {
        try {
            posField = ArrayTileModel.class.getDeclaredField("position");
            posField.setAccessible(true);
            uvField = ArrayTileModel.class.getDeclaredField("uv");
            uvField.setAccessible(true);
        } catch (Throwable t) {
            // Field will be retried on demand
        }
    }

    public static float[] getPositions(TileModel model) {
        if (model instanceof ArrayTileModel atm) {
            if (posField == null) {
                try {
                    posField = ArrayTileModel.class.getDeclaredField("position");
                    posField.setAccessible(true);
                } catch (Throwable ignored) {}
            }
            if (posField != null) {
                try {
                    return (float[]) posField.get(atm);
                } catch (Throwable ignored) {}
            }
        }
        return null;
    }

    public static float[] getUvs(TileModel model) {
        if (model instanceof ArrayTileModel atm) {
            if (uvField == null) {
                try {
                    uvField = ArrayTileModel.class.getDeclaredField("uv");
                    uvField.setAccessible(true);
                } catch (Throwable ignored) {}
            }
            if (uvField != null) {
                try {
                    return (float[]) uvField.get(atm);
                } catch (Throwable ignored) {}
            }
        }
        return null;
    }

    public static int getFI_POSITION() {
        return 9;
    }

    public static int getFI_UV() {
        return 6;
    }

}
