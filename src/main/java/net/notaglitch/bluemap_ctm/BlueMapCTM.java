package net.notaglitch.bluemap_ctm;

import de.bluecolored.bluemap.api.BlueMapAPI;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.notaglitch.bluemap_ctm.renderer.CTMDelegatingRendererType;

import java.util.concurrent.atomic.AtomicBoolean;

@Mod(BlueMapCTM.MOD_ID)
public class BlueMapCTM {

    public static final String MOD_ID = "bluemap_ctm";
    private static final AtomicBoolean API_LISTENER_REGISTERED = new AtomicBoolean(false);

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

            // Register once only!
            if (API_LISTENER_REGISTERED.compareAndSet(false, true)) {
                try {
                    BlueMapAPI.onEnable(api -> {
                        CTMDelegatingRendererType.wrapDefaultRenderer();
                    });
                } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}
    }

}
