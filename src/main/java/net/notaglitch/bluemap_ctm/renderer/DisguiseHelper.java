package net.notaglitch.bluemap_ctm.renderer;

import de.bluecolored.bluemap.core.world.BlockEntity;
import de.bluecolored.bluemap.core.world.BlockState;

import java.lang.reflect.Method;

public class DisguiseHelper {

    private static Method getDisguisedStateMethod = null;
    private static boolean methodLookedUp = false;

    public static BlockState getDisguisedState(BlockEntity be) {
        if (be == null) return null;

        if (!methodLookedUp) {
            methodLookedUp = true;
            try {
                Class<?> clazz = Class.forName("net.notaglitch.bluemap_disguise.blockentity.DisguisedBlockEntity");
                getDisguisedStateMethod = clazz.getMethod("getDisguisedBlockState");
            } catch (Throwable ignored) {}
        }

        if (getDisguisedStateMethod != null) {
            try {
                Object res = getDisguisedStateMethod.invoke(be);
                if (res instanceof BlockState bs) {
                    return bs;
                }
            } catch (Throwable ignored) {}
        }

        return null;
    }

}
