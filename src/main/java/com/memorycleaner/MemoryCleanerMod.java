package com.memorycleaner;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryCleanerMod implements ModInitializer {
    public static final String MOD_ID = "memorycleaner";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static MemoryCleaner serverCleaner;

    @Override
    public void onInitialize() {
        LOGGER.info("\ud83d\ude80 MemoryCleaner \u521d\u59cb\u5316\u5b8c\u6210 - \u51c6\u5907\u5b88\u62a4\u4f60\u7684\u5185\u5b58~ \u2728");

        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
        ServerPlayConnectionEvents.DISCONNECT.register(this::onPlayerDisconnect);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                ModCommands.register(dispatcher));

        LOGGER.info("\u2705 \u5ba2\u6237\u7aef/\u670d\u52a1\u7aef\u516c\u5171\u521d\u59cb\u5316\u5b8c\u6210");
    }

    private void onServerStarted(MinecraftServer server) {
        if (serverCleaner == null) {
            serverCleaner = new MemoryCleaner();
        }
        serverCleaner.init(server);
        LOGGER.info("\ud83d\udda5\ufe0f \u670d\u52a1\u7aef\u5185\u5b58\u6e05\u7406\u5668\u5df2\u542f\u52a8... \ud83d\udd0d");
    }

    private void onServerStopping(MinecraftServer server) {
        if (serverCleaner != null) {
            serverCleaner.shutdown();
        }
        LOGGER.info("\ud83d\udc4b \u670d\u52a1\u7aef\u5185\u5b58\u6e05\u7406\u5668\u5df2\u5b89\u5168\u5173\u95ed");
    }

    private void onServerTick(MinecraftServer server) {
        if (serverCleaner != null) {
            serverCleaner.onServerTick();
        }
    }

    private void onPlayerDisconnect(ServerGamePacketListenerImpl handler, MinecraftServer server) {
        if (serverCleaner != null && handler.player != null) {
            serverCleaner.onPlayerLoggedOut(handler.player);
        }
    }

    public static MemoryCleaner getServerCleaner() {
        return serverCleaner;
    }
}