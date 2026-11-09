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
        initializeIntegrations();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(BlueMapCTM::initializeIntegrations);
    }

    public static void initializeIntegrations() {
        try {
            CTMDelegatingRendererType.wrapDefaultRenderer();

            // Listen for BlueMap API enablement if loaded as plugin
            try {
                BlueMapAPI.onEnable(api -> {
                    Logger.global.logInfo("[BlueMap-CTM] BlueMap API enabled, verifying CTM hooks...");
                    CTMDelegatingRendererType.wrapDefaultRenderer();
                });
            } catch (Throwable ignored) {}
        } catch (Throwable t) {
            Logger.global.logWarning("[BlueMap-CTM] Failed to initialize CTM hooks: " + t.getMessage());
        }
    }

}
