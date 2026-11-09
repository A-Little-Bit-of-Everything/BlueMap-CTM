package net.notaglitch.bluemap_ctm.renderer;

import de.bluecolored.bluemap.core.logger.Logger;
import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.util.Registry;
import de.bluecolored.bluemap.core.world.BlockState;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

public class CTMDelegatingRendererType implements BlockRendererType {

    private final BlockRendererType delegate;

    public CTMDelegatingRendererType(BlockRendererType delegate) {
        this.delegate = delegate;
    }

    @Override
    public Key getKey() {
        return delegate.getKey();
    }

    @Override
    public boolean isFallbackFor(BlockState blockState) {
        return delegate.isFallbackFor(blockState);
    }

    @Override
    public BlockRenderer create(ResourcePack resourcePack, TextureGallery textureGallery, RenderSettings renderSettings) {
        BlockRenderer delegateRenderer = delegate.create(resourcePack, textureGallery, renderSettings);
        return new CTMBlockRenderer(delegateRenderer, resourcePack, textureGallery, renderSettings);
    }

    public static void wrapDefaultRenderer() {
        try {
            Field entriesField = Registry.class.getDeclaredField("entries");
            entriesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            ConcurrentHashMap<Key, BlockRendererType> map =
                    (ConcurrentHashMap<Key, BlockRendererType>) entriesField.get(BlockRendererType.REGISTRY);

            Key defaultKey = Key.bluemap("default");
            BlockRendererType existingDefault = map.get(defaultKey);
            if (existingDefault != null && !(existingDefault instanceof CTMDelegatingRendererType)) {
                map.put(defaultKey, new CTMDelegatingRendererType(existingDefault));
                Logger.global.logInfo("[BlueMap-CTM] Successfully hooked into BlueMap DEFAULT BlockRenderer.");
            }
        } catch (Throwable t) {
            Logger.global.logWarning("[BlueMap-CTM] Could not hook into DEFAULT BlockRenderer directly: " + t.getMessage());
        }
    }

}
