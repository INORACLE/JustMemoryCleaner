package com.memorycleaner.client;

import com.memorycleaner.Config;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

public class ClientCommandHandler {
    private final ClientMemoryCleaner clientCleaner;

    public ClientCommandHandler(ClientMemoryCleaner clientCleaner) {
        this.clientCleaner = clientCleaner;
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("mc")
                .then(ClientCommandManager.literal("clean").executes(this::executeClientClean))
                .then(ClientCommandManager.literal("status").executes(this::executeClientStatus))
                .then(ClientCommandManager.literal("setting")
                        .executes(this::executeClientSettingsStatus)
                        .then(ClientCommandManager.literal("threshold").then(ClientCommandManager.argument("percent", IntegerArgumentType.integer(50, 95)).executes(this::executeSetClientThreshold)))
                        .then(ClientCommandManager.literal("interval").then(ClientCommandManager.argument("seconds", IntegerArgumentType.integer(10, 300)).executes(this::executeSetClientInterval)))
                        .then(ClientCommandManager.literal("autoclean").then(ClientCommandManager.argument("enabled", BoolArgumentType.bool()).executes(this::executeSetClientAutoClean)))
                        .then(ClientCommandManager.literal("debug").then(ClientCommandManager.argument("enabled", BoolArgumentType.bool()).executes(this::executeSetClientDebug))))
                .then(ClientCommandManager.literal("help").executes(this::executeClientHelp))
                .executes(this::executeClientHelp));
        dispatcher.register(ClientCommandManager.literal("memorycleaner")
                .then(ClientCommandManager.literal("clean").executes(this::executeClientClean))
                .then(ClientCommandManager.literal("status").executes(this::executeClientStatus))
                .then(ClientCommandManager.literal("setting").executes(this::executeClientSettingsStatus))
                .then(ClientCommandManager.literal("help").executes(this::executeClientHelp))
                .executes(this::executeClientHelp));
    }

    private int executeClientClean(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        source.sendFeedback(Component.literal("\u00a7e\u6b63\u5728\u6e05\u7406\u5ba2\u6237\u7aef\u5185\u5b58... \ud83e\uddf9"));
        long before = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L;
        System.gc();
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long after = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L;
        long freed = before - after;
        if (freed > 0L) {
            source.sendFeedback(Component.literal(String.format("\u00a7a\u2713 \u5ba2\u6237\u7aef\u6e05\u7406\u5b8c\u6210\uff01\u91ca\u653e\u4e86 \u00a7b%dMB \u00a7a\u5185\u5b58 \u2728", freed)));
        } else {
            source.sendFeedback(Component.literal("\u00a77\u2713 \u5ba2\u6237\u7aef\u6e05\u7406\u5b8c\u6210\uff0c\u6ca1\u6709\u53ef\u56de\u6536\u7684\u5185\u5b58"));
        }
        if (this.clientCleaner != null) {
            this.clientCleaner.forceClean();
        }
        return 1;
    }

    private int executeClientStatus(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory() / 1024L / 1024L;
        long free = runtime.freeMemory() / 1024L / 1024L;
        long used = total - free;
        long max = runtime.maxMemory() / 1024L / 1024L;
        int percent = (int) (used * 100L / max);
        source.sendFeedback(Component.literal("\u00a7b===== \u5ba2\u6237\u7aef\u5185\u5b58\u72b6\u6001 ====="));
        source.sendFeedback(Component.literal(String.format("\u00a77\u5df2\u4f7f\u7528: \u00a7a%dMB \u00a77/ \u00a7c%dMB \u00a77(\u00a7e%d%%\u00a77)", used, max, percent)));
        source.sendFeedback(Component.literal(String.format("\u00a77\u5df2\u5206\u914d: \u00a7b%dMB \u00a77| \u7a7a\u95f2: \u00a72%dMB", total, free)));
        source.sendFeedback(Component.literal("\u00a7b========================="));
        return 1;
    }

    private int executeClientSettingsStatus(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        source.sendFeedback(Component.literal("\u00a7b===== \u5ba2\u6237\u7aef\u8bbe\u7f6e ====="));
        source.sendFeedback(Component.literal(String.format("\u00a7e\u81ea\u52a8\u6e05\u7406: \u00a7a%s", Config.CLIENT_AUTO_CLEAN.get() ? "\u5df2\u542f\u7528" : "\u5df2\u7981\u7528")));
        source.sendFeedback(Component.literal(String.format("\u00a7e\u6e05\u7406\u95f4\u9694: \u00a7a%d \u00a77\u79d2", Config.CLIENT_CLEAN_INTERVAL.get())));
        source.sendFeedback(Component.literal(String.format("\u00a7e\u5185\u5b58\u9608\u503c: \u00a7a%d%%", Config.CLIENT_MEMORY_THRESHOLD.get())));
        source.sendFeedback(Component.literal(String.format("\u00a7e\u8c03\u8bd5\u6a21\u5f0f: \u00a7a%s", Config.CLIENT_SHOW_DEBUG.get() ? "\u5df2\u542f\u7528" : "\u5df2\u7981\u7528")));
        source.sendFeedback(Component.literal("\u00a77--------------------"));
        source.sendFeedback(Component.literal("\u00a77\u4fee\u6539\u547d\u4ee4:"));
        source.sendFeedback(Component.literal("\u00a7e/mc setting threshold <50-95>"));
        source.sendFeedback(Component.literal("\u00a7e/mc setting interval <10-300>"));
        source.sendFeedback(Component.literal("\u00a7e/mc setting autoclean <true/false>"));
        source.sendFeedback(Component.literal("\u00a7e/mc setting debug <true/false>"));
        source.sendFeedback(Component.literal("\u00a7b====================="));
        return 1;
    }

    private int executeSetClientThreshold(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        int percent = IntegerArgumentType.getInteger(context, "percent");
        try {
            Config.CLIENT_MEMORY_THRESHOLD.set(percent);
            source.sendFeedback(Component.literal(String.format("\u00a7a\u2713 \u5ba2\u6237\u7aef\u5185\u5b58\u9608\u503c\u5df2\u8bbe\u7f6e\u4e3a \u00a7e%d%%", percent)));
        } catch (Exception e) {
            source.sendError(Component.literal("\u00a7c\u2717 \u8bbe\u7f6e\u5931\u8d25: " + e.getMessage()));
        }
        return 1;
    }

    private int executeSetClientInterval(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        int seconds = IntegerArgumentType.getInteger(context, "seconds");
        try {
            Config.CLIENT_CLEAN_INTERVAL.set(seconds);
            source.sendFeedback(Component.literal(String.format("\u00a7a\u2713 \u5ba2\u6237\u7aef\u6e05\u7406\u95f4\u9694\u5df2\u8bbe\u7f6e\u4e3a \u00a7e%d \u00a77\u79d2", seconds)));
        } catch (Exception e) {
            source.sendError(Component.literal("\u00a7c\u2717 \u8bbe\u7f6e\u5931\u8d25: " + e.getMessage()));
        }
        return 1;
    }

    private int executeSetClientAutoClean(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        try {
            Config.CLIENT_AUTO_CLEAN.set(enabled);
            source.sendFeedback(Component.literal(String.format("\u00a7a\u2713 \u5ba2\u6237\u7aef\u81ea\u52a8\u6e05\u7406\u5df2%s", enabled ? "\u00a7a\u542f\u7528" : "\u00a7c\u7981\u7528")));
        } catch (Exception e) {
            source.sendError(Component.literal("\u00a7c\u2717 \u8bbe\u7f6e\u5931\u8d25: " + e.getMessage()));
        }
        return 1;
    }

    private int executeSetClientDebug(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        try {
            Config.CLIENT_SHOW_DEBUG.set(enabled);
            source.sendFeedback(Component.literal(String.format("\u00a7a\u2713 \u5ba2\u6237\u7aef\u8c03\u8bd5\u6a21\u5f0f\u5df2%s", enabled ? "\u00a7a\u542f\u7528" : "\u00a7c\u7981\u7528")));
        } catch (Exception e) {
            source.sendError(Component.literal("\u00a7c\u2717 \u8bbe\u7f6e\u5931\u8d25: " + e.getMessage()));
        }
        return 1;
    }

    private int executeClientHelp(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        source.sendFeedback(Component.literal("\u00a7b========== \ud83d\udcda \u5185\u5b58\u6e05\u7406\u5668\u5e2e\u52a9 =========="));
        source.sendFeedback(Component.literal(""));
        source.sendFeedback(Component.literal("\u00a76\ud83c\udfae \u5ba2\u6237\u7aef\u547d\u4ee4 (\u4ec5\u5f71\u54cd\u4f60\u7684\u6e38\u620f):"));
        source.sendFeedback(Component.literal("\u00a7e/mc clean \u00a77- \u6e05\u7406\u5ba2\u6237\u7aef\u5185\u5b58"));
        source.sendFeedback(Component.literal("\u00a7e/mc status \u00a77- \u67e5\u770b\u5ba2\u6237\u7aef\u5185\u5b58\u72b6\u6001"));
        source.sendFeedback(Component.literal("\u00a7e/mc setting \u00a77- \u67e5\u770b/\u4fee\u6539\u5ba2\u6237\u7aef\u8bbe\u7f6e"));
        source.sendFeedback(Component.literal(""));
        source.sendFeedback(Component.literal("\u00a76\ud83d\udda5\ufe0f \u670d\u52a1\u5668\u547d\u4ee4 (\u9700\u8981\u7ba1\u7406\u5458\u6743\u9650):"));
        source.sendFeedback(Component.literal("\u00a7e/mc server clean \u00a77- \u6e05\u7406\u670d\u52a1\u5668\u5185\u5b58"));
        source.sendFeedback(Component.literal("\u00a7e/mc server status \u00a77- \u67e5\u770b\u670d\u52a1\u5668\u5185\u5b58\u72b6\u6001"));
        source.sendFeedback(Component.literal("\u00a7e/mc server setting \u00a77- \u67e5\u770b/\u4fee\u6539\u670d\u52a1\u5668\u8bbe\u7f6e"));
        source.sendFeedback(Component.literal(""));
        source.sendFeedback(Component.literal("\u00a77\ud83d\udca1 \u63d0\u793a: \u5ba2\u6237\u7aef\u547d\u4ee4\u4f1a\u5728\u672c\u5730\u6267\u884c\uff0c\u4e0d\u5f71\u54cd\u670d\u52a1\u5668"));
        source.sendFeedback(Component.literal("\u00a7b======================================"));
        return 1;
    }
}