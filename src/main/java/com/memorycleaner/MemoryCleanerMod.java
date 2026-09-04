package com.memorycleaner;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.command.ConfigCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = MemoryCleanerMod.MOD_ID)
public class MemoryCleanerMod {
    public static final String MOD_ID = "memorycleaner";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static MemoryCleaner serverCleaner;
    private static ClientMemoryCleaner clientCleaner;
    private static boolean isServerSide = false;
    private static boolean isClientSide = false;

    public MemoryCleanerMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        if (FMLLoader.getDist().isClient()) {
            modEventBus.addListener(this::clientSetup);
            isClientSide = true;
        }
        if (FMLLoader.getDist().isDedicatedServer()) {
            modEventBus.addListener(this::serverSetup);
            isServerSide = true;
        }
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("\ud83d\ude80 MemoryCleaner \u521d\u59cb\u5316\u5b8c\u6210 - \u51c6\u5907\u5b88\u62a4\u4f60\u7684\u5185\u5b58~ \u2728");
    }

    private void clientSetup(FMLClientSetupEvent event) {
        LOGGER.info("\ud83c\udfae \u5ba2\u6237\u7aef\u6a21\u5f0f\u521d\u59cb\u5316...");
        clientCleaner = new ClientMemoryCleaner();
        NeoForge.EVENT_BUS.register(clientCleaner);
        ClientCommandHandler clientCommandHandler = new ClientCommandHandler(clientCleaner);
        NeoForge.EVENT_BUS.register(clientCommandHandler);
        LOGGER.info("\u2328\ufe0f \u5ba2\u6237\u7aef\u547d\u4ee4\u5904\u7406\u5668\u5df2\u6ce8\u518c");
    }

    private void serverSetup(FMLDedicatedServerSetupEvent event) {
        LOGGER.info("\ud83d\udda5\ufe0f \u670d\u52a1\u7aef\u6a21\u5f0f\u521d\u59cb\u5316...");
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        if (serverCleaner == null) {
            serverCleaner = new MemoryCleaner();
        }
        serverCleaner.init(event.getServer());
        LOGGER.info("\ud83d\udda5\ufe0f \u670d\u52a1\u7aef\u5185\u5b58\u6e05\u7406\u5668\u5df2\u542f\u52a8... \ud83d\udd0d");
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        if (serverCleaner != null) {
            serverCleaner.shutdown();
        }
        LOGGER.info("\ud83d\udc4b \u670d\u52a1\u7aef\u5185\u5b58\u6e05\u7406\u5668\u5df2\u5b89\u5168\u5173\u95ed");
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        if (serverCleaner != null) {
            serverCleaner.onServerTick();
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        if (clientCleaner != null) {
            clientCleaner.onClientTick();
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (serverCleaner != null && event.getEntity() instanceof ServerPlayer player) {
            serverCleaner.onPlayerLoggedOut(player);
        }
    }

    public static boolean isServerSide() {
        return isServerSide;
    }

    public static boolean isClientSide() {
        return isClientSide;
    }

    public static MemoryCleaner getServerCleaner() {
        return serverCleaner;
    }

    public static ClientMemoryCleaner getClientCleaner() {
        return clientCleaner;
    }
}