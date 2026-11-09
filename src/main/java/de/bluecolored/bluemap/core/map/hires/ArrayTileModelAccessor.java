package de.bluecolored.bluemap.core.map.hires;

public class ArrayTileModelAccessor {

    public static float[] getPositions(TileModel model) {
        if (model instanceof ArrayTileModel atm) {
            return atm.position;
        }
        return null;
    }

    public static float[] getUvs(TileModel model) {
        if (model instanceof ArrayTileModel atm) {
            return atm.uv;
        }
        return null;
    }

    public static float[] getColors(TileModel model) {
        if (model instanceof ArrayTileModel atm) {
            return atm.color;
        }
        return null;
    }

    public static float[] getAos(TileModel model) {
        if (model instanceof ArrayTileModel atm) {
            return atm.ao;
        }
        return null;
    }

    public static byte[] getSunlight(TileModel model) {
        if (model instanceof ArrayTileModel atm) {
            return atm.sunlight;
        }
        return null;
    }

    public static byte[] getBlocklight(TileModel model) {
        if (model instanceof ArrayTileModel atm) {
            return atm.blocklight;
        }
        return null;
    }

    public static int[] getMaterialIndices(TileModel model) {
        if (model instanceof ArrayTileModel atm) {
            return atm.materialIndex;
        }
        return null;
    }

    public static int getFI_POSITION() {
        return ArrayTileModel.FI_POSITION;
    }

    public static int getFI_UV() {
        return ArrayTileModel.FI_UV;
    }

    public static int getFI_COLOR() {
        return ArrayTileModel.FI_COLOR;
    }

    public static int getFI_AO() {
        return ArrayTileModel.FI_AO;
    }

    public static int getFI_SUNLIGHT() {
        return ArrayTileModel.FI_SUNLIGHT;
    }

    public static int getFI_BLOCKLIGHT() {
        return ArrayTileModel.FI_BLOCKLIGHT;
    }

    public static int getFI_MATERIAL_INDEX() {
        return ArrayTileModel.FI_MATERIAL_INDEX;
    }

}
