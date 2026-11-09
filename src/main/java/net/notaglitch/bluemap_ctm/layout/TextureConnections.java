package net.notaglitch.bluemap_ctm.layout;

public class TextureConnections {

    public final boolean top;
    public final boolean topRight;
    public final boolean right;
    public final boolean bottomRight;
    public final boolean bottom;
    public final boolean bottomLeft;
    public final boolean left;
    public final boolean topLeft;

    public TextureConnections(
            boolean top, boolean topRight, boolean right, boolean bottomRight,
            boolean bottom, boolean bottomLeft, boolean left, boolean topLeft
    ) {
        this.top = top;
        this.topRight = topRight;
        this.right = right;
        this.bottomRight = bottomRight;
        this.bottom = bottom;
        this.bottomLeft = bottomLeft;
        this.left = left;
        this.topLeft = topLeft;
    }

}
