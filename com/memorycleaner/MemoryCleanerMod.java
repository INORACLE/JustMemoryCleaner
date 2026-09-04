/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.RegisterCommandsEvent
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedOutEvent
 *  net.minecraftforge.event.server.ServerStartedEvent
 *  net.minecraftforge.event.server.ServerStoppingEvent
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.config.IConfigSpec
 *  net.minecraftforge.fml.config.ModConfig$Type
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 *  net.minecraftforge.server.command.ConfigCommand
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.memorycleaner;

import com.memorycleaner.ClientCommandHandler;
import com.memorycleaner.ClientMemoryCleaner;
import com.memorycleaner.Config;
import com.memorycleaner.MemoryCleaner;
import com.memorycleaner.ModCommands;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.command.ConfigCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value="memorycleaner")
public class MemoryCleanerMod {
    public static final String MOD_ID = "memorycleaner";
    public static final Logger LOGGER = LogManager.getLogger((String)"memorycleaner");
    private static MemoryCleaner serverCleaner;
    private static ClientMemoryCleaner clientCleaner;
    private static boolean isServerSide;
    private static boolean isClientSide;

    public MemoryCleanerMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> {
            modEventBus.addListener(this::clientSetup);
            isClientSide = true;
        });
        DistExecutor.unsafeRunWhenOn((Dist)Dist.DEDICATED_SERVER, () -> () -> {
            modEventBus.addListener(this::serverSetup);
            isServerSide = true;
        });
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, (IConfigSpec)Config.SERVER_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, (IConfigSpec)Config.CLIENT_SPEC);
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("\ud83d\ude80 MemoryCleaner \u521d\u59cb\u5316\u5b8c\u6210 - \u51c6\u5907\u5b88\u62a4\u4f60\u7684\u5185\u5b58~ \u2728");
    }

    private void clientSetup(FMLClientSetupEvent event) {
        LOGGER.info("\ud83c\udfae \u5ba2\u6237\u7aef\u6a21\u5f0f\u521d\u59cb\u5316...");
        clientCleaner = new ClientMemoryCleaner();
        MinecraftForge.EVENT_BUS.register((Object)clientCleaner);
        ClientCommandHandler clientCommandHandler = new ClientCommandHandler(clientCleaner);
        MinecraftForge.EVENT_BUS.register((Object)clientCommandHandler);
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
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && serverCleaner != null) {
            serverCleaner.onServerTick();
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && clientCleaner != null) {
            clientCleaner.onClientTick();
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register((CommandDispatcher<CommandSourceStack>)event.getDispatcher());
        ConfigCommand.register((CommandDispatcher)event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (serverCleaner != null && event.getEntity() instanceof ServerPlayer) {
            serverCleaner.onPlayerLoggedOut((ServerPlayer)event.getEntity());
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

    static {
        isServerSide = false;
        isClientSide = false;
    }
}

