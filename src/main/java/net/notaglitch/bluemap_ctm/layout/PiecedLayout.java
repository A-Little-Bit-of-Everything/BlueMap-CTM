package net.notaglitch.bluemap_ctm.layout;

public class PiecedLayout {

    private static final int[] CORNER_SPRITE_INDICES = { 0, 3, 2, 4, 0, 3, 2, 1 };

    /**
     * Resolves single tile index (0..4) or returns -1 if corners must be split.
     */
    public static int getTileIndex(TextureConnections c) {
        if (!c.top && !c.right && !c.bottom && !c.left) {
            return 0; // Standalone tile
        }
        if (c.top && c.topRight && c.right && c.bottomRight && c.bottom && c.bottomLeft && c.left && c.topLeft) {
            return 1; // Full connected interior
        }
        if (c.top && !c.right && c.bottom && !c.left) {
            return 2; // Vertical column
        }
        if (!c.top && c.right && !c.bottom && c.left) {
            return 3; // Horizontal beam
        }
        if (c.top && !c.topRight && c.right && !c.bottomRight && c.bottom && !c.bottomLeft && c.left && !c.topLeft) {
            return 4; // 4-way cross without diagonals
        }

        // Check if all 4 corners produce the exact same sprite index
        int tl = getCornerSprite(c.top, c.left, c.topLeft);
        int tr = getCornerSprite(c.top, c.right, c.topRight);
        int bl = getCornerSprite(c.bottom, c.left, c.bottomLeft);
        int br = getCornerSprite(c.bottom, c.right, c.bottomRight);

        if (tl == tr && tr == bl && bl == br) {
            return tl;
        }

        return -1; // Mixed corner tiles required
    }

    public static int getCornerSprite(boolean side1, boolean side2, boolean diagonal) {
        int index = (side1 ? 1 : 0) | (side2 ? 2 : 0) | (diagonal ? 4 : 0);
        return CORNER_SPRITE_INDICES[index];
    }

}
