package net.notaglitch.bluemap_ctm;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.core.logger.Logger;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.notaglitch.bluemap_ctm.renderer.CTMDelegatingRendererType;

@Mod(BlueMapCTM.MOD_ID)
public class BlueMapCTM {

    public static final String MOD_ID = "bluemap_ctm";

    public BlueMapCTM(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        hook();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            hook();
            try {
                BlueMapAPI.onEnable(api -> hook());
            } catch (Throwable t) {
                Logger.global.logWarning("[BlueMap-CTM] onEnable registration failed: " + t.getMessage());
            }
        });
    }

    public static void hook() {
        try {
            CTMDelegatingRendererType.wrapDefaultRenderer();
        } catch (Throwable ignored) {}
    }

}
