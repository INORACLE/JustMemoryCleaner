package com.memorycleaner.client;

import com.memorycleaner.MemoryCleanerMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ClientInitializer implements ClientModInitializer {
    private static ClientMemoryCleaner clientCleaner;

    @Override
    public void onInitializeClient() {
        MemoryCleanerMod.LOGGER.info("\ud83c\udfae \u5ba2\u6237\u7aef\u6a21\u5f0f\u521d\u59cb\u5316...");
        clientCleaner = new ClientMemoryCleaner();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (clientCleaner != null) {
                clientCleaner.onClientTick();
            }
        });
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                new ClientCommandHandler(clientCleaner).register(dispatcher));
        MemoryCleanerMod.LOGGER.info("\u2328\ufe0f \u5ba2\u6237\u7aef\u547d\u4ee4\u5904\u7406\u5668\u5df2\u6ce8\u518c");
    }

    public static ClientMemoryCleaner getClientCleaner() {
        return clientCleaner;
    }
}