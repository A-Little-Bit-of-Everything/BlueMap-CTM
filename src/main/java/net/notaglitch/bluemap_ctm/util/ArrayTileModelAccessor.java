package net.notaglitch.bluemap_ctm.util;

import de.bluecolored.bluemap.core.map.hires.ArrayTileModel;
import de.bluecolored.bluemap.core.map.hires.TileModel;

public class ArrayTileModelAccessor {

    public static float[] getPositions(TileModel model) {
        if (model != null) {
            float[] pos = model.getPositions();
            if (pos != null) return pos;
        }
        return null;
    }

    public static float[] getUvs(TileModel model) {
        if (model != null) {
            float[] uv = model.getUvs();
            if (uv != null) return uv;
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
