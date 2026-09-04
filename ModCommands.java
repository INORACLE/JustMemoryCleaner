/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 *  net.minecraftforge.server.ServerLifecycleHooks
 */
package com.memorycleaner;

import com.memorycleaner.Config;
import com.memorycleaner.MemoryCleanerMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ModCommands {
    private static boolean isClientSide(CommandSourceStack source) {
        return source.m_81377_() == null || !source.m_81377_().m_6982_();
    }

    private static boolean canExecute(CommandSourceStack source) {
        if (ModCommands.isClientSide(source)) {
            return true;
        }
        return source.m_6761_(2);
    }

    private static boolean hasAdminPermission(CommandSourceStack source) {
        return source.m_6761_(2);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"memorycleaner").requires(ModCommands::canExecute)).then(Commands.m_82127_((String)"help").executes(ModCommands::executeHelp))).then(Commands.m_82127_((String)"status").executes(ModCommands::executeStatus))).then(Commands.m_82127_((String)"clean").executes(ModCommands::executeClientClean))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"server").requires(ModCommands::hasAdminPermission)).then(Commands.m_82127_((String)"clean").executes(ModCommands::executeServerClean))).then(Commands.m_82127_((String)"status").executes(ModCommands::executeServerStatus)))).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"config").then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"setting").executes(ModCommands::executeClientSettingsStatus)).then(Commands.m_82127_((String)"threshold").then(Commands.m_82129_((String)"percent", (ArgumentType)IntegerArgumentType.integer((int)50, (int)95)).executes(ModCommands::executeSetClientThreshold)))).then(Commands.m_82127_((String)"interval").then(Commands.m_82129_((String)"seconds", (ArgumentType)IntegerArgumentType.integer((int)10, (int)300)).executes(ModCommands::executeSetClientInterval)))).then(Commands.m_82127_((String)"autoclean").then(Commands.m_82129_((String)"enabled", (ArgumentType)BoolArgumentType.bool()).executes(ModCommands::executeSetClientAutoClean)))).then(Commands.m_82127_((String)"debug").then(Commands.m_82129_((String)"enabled", (ArgumentType)BoolArgumentType.bool()).executes(ModCommands::executeSetClientDebug))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"serversetting").requires(ModCommands::hasAdminPermission)).executes(ModCommands::executeServerSettingsStatus)).then(Commands.m_82127_((String)"threshold").then(Commands.m_82129_((String)"percent", (ArgumentType)IntegerArgumentType.integer((int)50, (int)95)).executes(ModCommands::executeSetServerThreshold)))).then(Commands.m_82127_((String)"cooldown").then(Commands.m_82129_((String)"seconds", (ArgumentType)IntegerArgumentType.integer((int)10, (int)300)).executes(ModCommands::executeSetCooldown)))).then(Commands.m_82127_((String)"autothreshold").then(Commands.m_82129_((String)"enabled", (ArgumentType)BoolArgumentType.bool()).executes(ModCommands::executeSetAutoThreshold)))).then(Commands.m_82127_((String)"aggressive").then(Commands.m_82129_((String)"enabled", (ArgumentType)BoolArgumentType.bool()).executes(ModCommands::executeSetAggressive)))).then(Commands.m_82127_((String)"batchsize").then(Commands.m_82129_((String)"value", (ArgumentType)IntegerArgumentType.integer((int)10, (int)500)).executes(ModCommands::executeSetBatchSize)))).then(Commands.m_82127_((String)"interval").then(Commands.m_82129_((String)"minutes", (ArgumentType)IntegerArgumentType.integer((int)1, (int)60)).executes(ModCommands::executeSetServerInterval)))).then(Commands.m_82127_((String)"reload").executes(ModCommands::executeReloadConfig))))).executes(ModCommands::executeHelp));
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"mc").requires(ModCommands::canExecute)).then(Commands.m_82127_((String)"help").executes(ModCommands::executeHelp))).then(Commands.m_82127_((String)"status").executes(ModCommands::executeStatus))).then(Commands.m_82127_((String)"clean").executes(ModCommands::executeClientClean))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"setting").executes(ModCommands::executeClientSettingsStatus)).then(Commands.m_82127_((String)"threshold").then(Commands.m_82129_((String)"percent", (ArgumentType)IntegerArgumentType.integer((int)50, (int)95)).executes(ModCommands::executeSetClientThreshold)))).then(Commands.m_82127_((String)"interval").then(Commands.m_82129_((String)"seconds", (ArgumentType)IntegerArgumentType.integer((int)10, (int)300)).executes(ModCommands::executeSetClientInterval)))).then(Commands.m_82127_((String)"autoclean").then(Commands.m_82129_((String)"enabled", (ArgumentType)BoolArgumentType.bool()).executes(ModCommands::executeSetClientAutoClean)))).then(Commands.m_82127_((String)"debug").then(Commands.m_82129_((String)"enabled", (ArgumentType)BoolArgumentType.bool()).executes(ModCommands::executeSetClientDebug))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"server").requires(ModCommands::hasAdminPermission)).then(Commands.m_82127_((String)"status").executes(ModCommands::executeServerStatus))).then(Commands.m_82127_((String)"clean").executes(ModCommands::executeServerClean))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"setting").executes(ModCommands::executeServerSettingsStatus)).then(Commands.m_82127_((String)"threshold").then(Commands.m_82129_((String)"percent", (ArgumentType)IntegerArgumentType.integer((int)50, (int)95)).executes(ModCommands::executeSetServerThreshold)))).then(Commands.m_82127_((String)"cooldown").then(Commands.m_82129_((String)"seconds", (ArgumentType)IntegerArgumentType.integer((int)10, (int)300)).executes(ModCommands::executeSetCooldown)))).then(Commands.m_82127_((String)"autothreshold").then(Commands.m_82129_((String)"enabled", (ArgumentType)BoolArgumentType.bool()).executes(ModCommands::executeSetAutoThreshold)))).then(Commands.m_82127_((String)"aggressive").then(Commands.m_82129_((String)"enabled", (ArgumentType)BoolArgumentType.bool()).executes(ModCommands::executeSetAggressive)))).then(Commands.m_82127_((String)"batchsize").then(Commands.m_82129_((String)"value", (ArgumentType)IntegerArgumentType.integer((int)10, (int)500)).executes(ModCommands::executeSetBatchSize)))).then(Commands.m_82127_((String)"interval").then(Commands.m_82129_((String)"minutes", (ArgumentType)IntegerArgumentType.integer((int)1, (int)60)).executes(ModCommands::executeSetServerInterval)))).then(Commands.m_82127_((String)"reload").executes(ModCommands::executeReloadConfig))))).executes(ModCommands::executeHelp));
    }

    private static int executeHelp(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        boolean isAdmin = ModCommands.hasAdminPermission(source);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551     \u00a7eMemoryCleaner \u5e2e\u52a9\u83dc\u5355\u00a7b      \u2551"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2560\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2563"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7a\u3010\u73a9\u5bb6\u547d\u4ee4\u3011\u00a77 (\u65e0\u9700\u6743\u9650)        \u2551"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7e/mc help \u00a77- \u663e\u793a\u6b64\u5e2e\u52a9\u83dc\u5355      \u2551"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7e/mc status \u00a77- \u67e5\u770b\u5ba2\u6237\u7aef\u5185\u5b58\u72b6\u6001 \u2551"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7e/mc clean \u00a77- \u624b\u52a8\u6e05\u7406\u5ba2\u6237\u7aef\u5185\u5b58 \u2551"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7e/mc setting \u00a77- \u67e5\u770b\u5ba2\u6237\u7aef\u8bbe\u7f6e   \u2551"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2560\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2563"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7a\u3010\u5ba2\u6237\u7aef\u8bbe\u7f6e\u3011\u00a77 (\u65e0\u9700\u6743\u9650)      \u2551"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7e/mc setting threshold <50-95>    \u2551"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551   \u00a77\u8bbe\u7f6e\u5185\u5b58\u9608\u503c\u767e\u5206\u6bd4             \u2551"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7e/mc setting interval <10-300>    \u2551"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551   \u00a77\u8bbe\u7f6e\u6e05\u7406\u95f4\u9694(\u79d2)               \u2551"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7e/mc setting autoclean <true/false> \u2551"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551   \u00a77\u542f\u7528/\u7981\u7528\u81ea\u52a8\u6e05\u7406              \u2551"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7e/mc setting debug <true/false>   \u2551"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551   \u00a77\u542f\u7528/\u7981\u7528\u8c03\u8bd5\u6a21\u5f0f              \u2551"), false);
        if (isAdmin) {
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2560\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2563"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7c\u3010\u7ba1\u7406\u5458\u547d\u4ee4\u3011\u00a77 (\u9700\u8981OP\u6743\u9650)   \u2551"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7e/mc server clean \u00a77- \u6e05\u7406\u670d\u52a1\u5668\u5185\u5b58 \u2551"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7e/mc server status \u00a77- \u670d\u52a1\u5668\u72b6\u6001  \u2551"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7e/mc server setting \u00a77- \u67e5\u770b\u670d\u52a1\u5668\u914d\u7f6e \u2551"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2560\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2563"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7c\u3010\u670d\u52a1\u5668\u8bbe\u7f6e\u3011\u00a77 (\u9700\u8981OP\u6743\u9650)   \u2551"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7e/mc server setting threshold <50-95> \u2551"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551   \u00a77\u8bbe\u7f6e\u670d\u52a1\u5668\u5185\u5b58\u9608\u503c             \u2551"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7e/mc server setting cooldown <10-300> \u2551"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551   \u00a77\u8bbe\u7f6e\u6e05\u7406\u51b7\u5374\u65f6\u95f4(\u79d2)           \u2551"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7e/mc server setting autothreshold <true/false> \u2551"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551   \u00a77\u542f\u7528/\u7981\u7528\u9608\u503c\u81ea\u52a8\u6e05\u7406          \u2551"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7e/mc server setting aggressive <true/false> \u2551"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551   \u00a77\u542f\u7528/\u7981\u7528\u6fc0\u8fdb\u6a21\u5f0f              \u2551"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7e/mc server setting interval <1-60> \u2551"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551   \u00a77\u8bbe\u7f6e\u81ea\u52a8\u6e05\u7406\u95f4\u9694(\u5206\u949f)         \u2551"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u2551 \u00a7e/mc server setting reload \u00a77- \u91cd\u8f7d\u914d\u7f6e \u2551"), false);
        }
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), false);
        return 1;
    }

    private static int executeStatus(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        if (ModCommands.isClientSide(source)) {
            Runtime runtime = Runtime.getRuntime();
            long total = runtime.totalMemory() / 1024L / 1024L;
            long free = runtime.freeMemory() / 1024L / 1024L;
            long used = total - free;
            long max = runtime.maxMemory() / 1024L / 1024L;
            int percent = (int)(used * 100L / max);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b===== \u5ba2\u6237\u7aef\u5185\u5b58\u72b6\u6001 ====="), false);
            source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a77\u5df2\u4f7f\u7528: \u00a7a%dMB \u00a77/ \u00a7c%dMB \u00a77(\u00a7e%d%%\u00a77)", used, max, percent)), false);
            source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a77\u5df2\u5206\u914d: \u00a7b%dMB \u00a77| \u7a7a\u95f2: \u00a72%dMB", total, free)), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7b========================="), false);
        } else {
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7e\u26a0\ufe0f \u63d0\u793a\uff1a\u8fd9\u662f\u5ba2\u6237\u7aef\u72b6\u6001\u547d\u4ee4"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a77\u5728\u670d\u52a1\u5668\u4e2d\u6267\u884c\u6b64\u547d\u4ee4\u4e0d\u4f1a\u663e\u793a\u60a8\u7684\u5ba2\u6237\u7aef\u5185\u5b58\u72b6\u6001"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a77\u5982\u679c\u8981\u67e5\u770b\u670d\u52a1\u5668\u72b6\u6001\uff0c\u8bf7\u4f7f\u7528 \u00a7e/mc server status"), false);
        }
        return 1;
    }

    private static int executeServerStatus(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory() / 1024L / 1024L;
        long free = runtime.freeMemory() / 1024L / 1024L;
        long used = total - free;
        long max = runtime.maxMemory() / 1024L / 1024L;
        int percent = (int)(used * 100L / max);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b===== \u670d\u52a1\u5668\u5185\u5b58\u72b6\u6001 ====="), false);
        source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a77\u5df2\u4f7f\u7528: \u00a7a%dMB \u00a77/ \u00a7c%dMB \u00a77(\u00a7e%d%%\u00a77)", used, max, percent)), false);
        source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a77\u5df2\u5206\u914d: \u00a7b%dMB \u00a77| \u7a7a\u95f2: \u00a72%dMB", total, free)), false);
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            int playerCount = ServerLifecycleHooks.getCurrentServer().m_7416_();
            int maxPlayers = ServerLifecycleHooks.getCurrentServer().m_7418_();
            source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a77\u5728\u7ebf\u73a9\u5bb6: \u00a7d%d\u00a77/\u00a7d%d", playerCount, maxPlayers)), false);
            if (MemoryCleanerMod.getServerCleaner() != null) {
                source.m_288197_(() -> Component.m_237113_((String)"\u00a77\u6e05\u7406\u5668\u72b6\u6001: \u00a7a\u8fd0\u884c\u4e2d"), false);
                source.m_288197_(() -> Component.m_237113_((String)MemoryCleanerMod.getServerCleaner().getStatus()), false);
            }
        }
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b========================="), false);
        return 1;
    }

    private static int executeClientClean(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        if (ModCommands.isClientSide(source)) {
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7e\u6b63\u5728\u6e05\u7406\u5ba2\u6237\u7aef\u5185\u5b58... \ud83e\uddf9"), false);
            long before = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L;
            System.gc();
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            long after = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L;
            long freed = before - after;
            if (freed > 0L) {
                source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7a\u2713 \u5ba2\u6237\u7aef\u6e05\u7406\u5b8c\u6210\uff01\u91ca\u653e\u4e86 \u00a7b%dMB \u00a7a\u5185\u5b58 \u2728", freed)), false);
            } else {
                source.m_288197_(() -> Component.m_237113_((String)"\u00a77\u2713 \u5ba2\u6237\u7aef\u6e05\u7406\u5b8c\u6210\uff0c\u6ca1\u6709\u53ef\u56de\u6536\u7684\u5185\u5b58"), false);
            }
        } else {
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7e\u26a0\ufe0f \u63d0\u793a\uff1a\u8fd9\u662f\u5ba2\u6237\u7aef\u6e05\u7406\u547d\u4ee4"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a77\u5728\u670d\u52a1\u5668\u4e2d\u6267\u884c\u6b64\u547d\u4ee4\u4e0d\u4f1a\u6e05\u7406\u60a8\u7684\u5ba2\u6237\u7aef\u5185\u5b58"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a77\u5982\u679c\u8981\u6e05\u7406\u670d\u52a1\u5668\u5185\u5b58\uff0c\u8bf7\u4f7f\u7528 \u00a7e/mc server clean"), false);
        }
        return 1;
    }

    private static int executeServerClean(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7e\u6b63\u5728\u6e05\u7406\u670d\u52a1\u5668\u5185\u5b58... \ud83e\uddf9"), false);
        if (MemoryCleanerMod.getServerCleaner() != null) {
            MemoryCleanerMod.getServerCleaner().forceClean();
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7a\u2713 \u670d\u52a1\u5668\u5185\u5b58\u6e05\u7406\u5b8c\u6210\uff01\u2728"), false);
        } else {
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7c\u2717 \u670d\u52a1\u5668\u6e05\u7406\u5668\u672a\u8fd0\u884c\uff01"), false);
        }
        return 1;
    }

    private static int executeClientSettingsStatus(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        if (ModCommands.isClientSide(source)) {
            try {
                source.m_288197_(() -> Component.m_237113_((String)"\u00a7b===== \u5ba2\u6237\u7aef\u8bbe\u7f6e ====="), false);
                source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7e\u81ea\u52a8\u6e05\u7406: \u00a7a%s", (Boolean)Config.CLIENT_AUTO_CLEAN.get() != false ? "\u5df2\u542f\u7528" : "\u5df2\u7981\u7528")), false);
                source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7e\u6e05\u7406\u95f4\u9694: \u00a7a%d \u00a77\u79d2", Config.CLIENT_CLEAN_INTERVAL.get())), false);
                source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7e\u5185\u5b58\u9608\u503c: \u00a7a%d%%", Config.CLIENT_MEMORY_THRESHOLD.get())), false);
                source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7e\u8c03\u8bd5\u6a21\u5f0f: \u00a7a%s", (Boolean)Config.CLIENT_SHOW_DEBUG.get() != false ? "\u5df2\u542f\u7528" : "\u5df2\u7981\u7528")), false);
                source.m_288197_(() -> Component.m_237113_((String)"\u00a77--------------------"), false);
                source.m_288197_(() -> Component.m_237113_((String)"\u00a77\u4fee\u6539\u547d\u4ee4:"), false);
                source.m_288197_(() -> Component.m_237113_((String)"\u00a7e/mc setting threshold <50-95>"), false);
                source.m_288197_(() -> Component.m_237113_((String)"\u00a7e/mc setting interval <10-300>"), false);
                source.m_288197_(() -> Component.m_237113_((String)"\u00a7e/mc setting autoclean <true/false>"), false);
                source.m_288197_(() -> Component.m_237113_((String)"\u00a7e/mc setting debug <true/false>"), false);
                source.m_288197_(() -> Component.m_237113_((String)"\u00a7b====================="), false);
            }
            catch (Exception e) {
                source.m_288197_(() -> Component.m_237113_((String)"\u00a7c\u2717 \u5ba2\u6237\u7aef\u914d\u7f6e\u672a\u52a0\u8f7d\uff01"), false);
            }
        } else {
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7e\u26a0\ufe0f \u63d0\u793a\uff1a\u8fd9\u662f\u5ba2\u6237\u7aef\u8bbe\u7f6e\u547d\u4ee4"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a77\u5728\u670d\u52a1\u5668\u4e2d\u6267\u884c\u6b64\u547d\u4ee4\u4e0d\u4f1a\u4fee\u6539\u60a8\u7684\u5ba2\u6237\u7aef\u8bbe\u7f6e"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a77\u5982\u679c\u8981\u67e5\u770b\u670d\u52a1\u5668\u8bbe\u7f6e\uff0c\u8bf7\u4f7f\u7528 \u00a7e/mc server setting"), false);
        }
        return 1;
    }

    private static int executeSetClientThreshold(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        int percent = IntegerArgumentType.getInteger(context, (String)"percent");
        if (ModCommands.isClientSide(source)) {
            try {
                if (Config.CLIENT_MEMORY_THRESHOLD == null) {
                    source.m_288197_(() -> Component.m_237113_((String)"\u00a7c\u2717 \u5ba2\u6237\u7aef\u914d\u7f6e\u672a\u52a0\u8f7d\uff01"), false);
                    return 0;
                }
                Config.CLIENT_MEMORY_THRESHOLD.set((Object)percent);
                source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7a\u2713 \u5ba2\u6237\u7aef\u5185\u5b58\u9608\u503c\u5df2\u8bbe\u7f6e\u4e3a \u00a7e%d%%", percent)), false);
            }
            catch (Exception e) {
                source.m_288197_(() -> Component.m_237113_((String)("\u00a7c\u2717 \u8bbe\u7f6e\u5931\u8d25: " + e.getMessage())), false);
            }
        } else {
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7e\u26a0\ufe0f \u63d0\u793a\uff1a\u8fd9\u662f\u5ba2\u6237\u7aef\u8bbe\u7f6e\u547d\u4ee4"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a77\u5728\u670d\u52a1\u5668\u4e2d\u6267\u884c\u6b64\u547d\u4ee4\u4e0d\u4f1a\u4fee\u6539\u60a8\u7684\u5ba2\u6237\u7aef\u8bbe\u7f6e"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a77\u5982\u679c\u8981\u8bbe\u7f6e\u670d\u52a1\u5668\u9608\u503c\uff0c\u8bf7\u4f7f\u7528 \u00a7e/mc server setting threshold <50-95>"), false);
        }
        return 1;
    }

    private static int executeSetClientInterval(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        int seconds = IntegerArgumentType.getInteger(context, (String)"seconds");
        if (ModCommands.isClientSide(source)) {
            try {
                if (Config.CLIENT_CLEAN_INTERVAL == null) {
                    source.m_288197_(() -> Component.m_237113_((String)"\u00a7c\u2717 \u5ba2\u6237\u7aef\u914d\u7f6e\u672a\u52a0\u8f7d\uff01"), false);
                    return 0;
                }
                Config.CLIENT_CLEAN_INTERVAL.set((Object)seconds);
                source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7a\u2713 \u5ba2\u6237\u7aef\u6e05\u7406\u95f4\u9694\u5df2\u8bbe\u7f6e\u4e3a \u00a7e%d \u00a77\u79d2", seconds)), false);
            }
            catch (Exception e) {
                source.m_288197_(() -> Component.m_237113_((String)("\u00a7c\u2717 \u8bbe\u7f6e\u5931\u8d25: " + e.getMessage())), false);
            }
        } else {
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7e\u26a0\ufe0f \u63d0\u793a\uff1a\u8fd9\u662f\u5ba2\u6237\u7aef\u8bbe\u7f6e\u547d\u4ee4"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a77\u5728\u670d\u52a1\u5668\u4e2d\u6267\u884c\u6b64\u547d\u4ee4\u4e0d\u4f1a\u4fee\u6539\u60a8\u7684\u5ba2\u6237\u7aef\u8bbe\u7f6e"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a77\u5982\u679c\u8981\u8bbe\u7f6e\u670d\u52a1\u5668\u95f4\u9694\uff0c\u8bf7\u4f7f\u7528 \u00a7e/mc server setting interval <1-60>"), false);
        }
        return 1;
    }

    private static int executeSetClientAutoClean(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        boolean enabled = BoolArgumentType.getBool(context, (String)"enabled");
        if (ModCommands.isClientSide(source)) {
            try {
                if (Config.CLIENT_AUTO_CLEAN == null) {
                    source.m_288197_(() -> Component.m_237113_((String)"\u00a7c\u2717 \u5ba2\u6237\u7aef\u914d\u7f6e\u672a\u52a0\u8f7d\uff01"), false);
                    return 0;
                }
                Config.CLIENT_AUTO_CLEAN.set((Object)enabled);
                source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7a\u2713 \u5ba2\u6237\u7aef\u81ea\u52a8\u6e05\u7406\u5df2%s", enabled ? "\u00a7a\u542f\u7528" : "\u00a7c\u7981\u7528")), false);
            }
            catch (Exception e) {
                source.m_288197_(() -> Component.m_237113_((String)("\u00a7c\u2717 \u8bbe\u7f6e\u5931\u8d25: " + e.getMessage())), false);
            }
        } else {
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7e\u26a0\ufe0f \u63d0\u793a\uff1a\u8fd9\u662f\u5ba2\u6237\u7aef\u8bbe\u7f6e\u547d\u4ee4"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a77\u5728\u670d\u52a1\u5668\u4e2d\u6267\u884c\u6b64\u547d\u4ee4\u4e0d\u4f1a\u4fee\u6539\u60a8\u7684\u5ba2\u6237\u7aef\u8bbe\u7f6e"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a77\u5982\u679c\u8981\u8bbe\u7f6e\u670d\u52a1\u5668\u81ea\u52a8\u6e05\u7406\uff0c\u8bf7\u4f7f\u7528 \u00a7e/mc server setting autoclean <true/false>"), false);
        }
        return 1;
    }

    private static int executeSetClientDebug(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        boolean enabled = BoolArgumentType.getBool(context, (String)"enabled");
        if (ModCommands.isClientSide(source)) {
            try {
                if (Config.CLIENT_SHOW_DEBUG == null) {
                    source.m_288197_(() -> Component.m_237113_((String)"\u00a7c\u2717 \u5ba2\u6237\u7aef\u914d\u7f6e\u672a\u52a0\u8f7d\uff01"), false);
                    return 0;
                }
                Config.CLIENT_SHOW_DEBUG.set((Object)enabled);
                source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7a\u2713 \u5ba2\u6237\u7aef\u8c03\u8bd5\u6a21\u5f0f\u5df2%s", enabled ? "\u00a7a\u542f\u7528" : "\u00a7c\u7981\u7528")), false);
            }
            catch (Exception e) {
                source.m_288197_(() -> Component.m_237113_((String)("\u00a7c\u2717 \u8bbe\u7f6e\u5931\u8d25: " + e.getMessage())), false);
            }
        } else {
            source.m_288197_(() -> Component.m_237113_((String)"\u00a7e\u26a0\ufe0f \u63d0\u793a\uff1a\u8fd9\u662f\u5ba2\u6237\u7aef\u8bbe\u7f6e\u547d\u4ee4"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a77\u5728\u670d\u52a1\u5668\u4e2d\u6267\u884c\u6b64\u547d\u4ee4\u4e0d\u4f1a\u4fee\u6539\u60a8\u7684\u5ba2\u6237\u7aef\u8bbe\u7f6e"), false);
            source.m_288197_(() -> Component.m_237113_((String)"\u00a77\u5982\u679c\u8981\u8bbe\u7f6e\u670d\u52a1\u5668\u8c03\u8bd5\u6a21\u5f0f\uff0c\u8bf7\u4f7f\u7528 \u00a7e/mc server setting debug <true/false>"), false);
        }
        return 1;
    }

    private static int executeServerSettingsStatus(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b===== \u670d\u52a1\u5668\u8bbe\u7f6e ====="), false);
        source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7e\u81ea\u52a8\u6e05\u7406: \u00a7a%s", (Boolean)Config.AUTO_CLEAN_ENABLED.get() != false ? "\u5df2\u542f\u7528" : "\u5df2\u7981\u7528")), false);
        source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7e\u6e05\u7406\u95f4\u9694: \u00a7a%d \u00a77\u5206\u949f", Config.AUTO_CLEAN_INTERVAL.get())), false);
        source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7e\u5185\u5b58\u9608\u503c: \u00a7a%d%%", Config.MEMORY_THRESHOLD_PERCENT.get())), false);
        source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7e\u51b7\u5374\u65f6\u95f4: \u00a7a%d \u00a77\u79d2", Config.CLEAN_COOLDOWN_SECONDS.get())), false);
        source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7e\u6fc0\u8fdb\u6a21\u5f0f: \u00a7a%s", (Boolean)Config.AGGRESSIVE_MODE.get() != false ? "\u5df2\u542f\u7528" : "\u5df2\u7981\u7528")), false);
        source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7e\u6279\u6b21\u5927\u5c0f: \u00a7a%d", Config.CLEAN_BATCH_SIZE.get())), false);
        source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7e\u9608\u503c\u81ea\u52a8\u6e05\u7406: \u00a7a%s", (Boolean)Config.AUTO_CLEAN_ON_THRESHOLD.get() != false ? "\u5df2\u542f\u7528" : "\u5df2\u7981\u7528")), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a77--------------------"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a77\u4fee\u6539\u547d\u4ee4:"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7e/mc serversetting threshold <50-95>"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7e/mc serversetting cooldown <10-300>"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7e/mc serversetting aggressive <true/false>"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7e/mc serversetting interval <1-60>"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7e/mc serversetting reload"), false);
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7b====================="), false);
        return 1;
    }

    private static int executeSetServerThreshold(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        int percent = IntegerArgumentType.getInteger(context, (String)"percent");
        Config.MEMORY_THRESHOLD_PERCENT.set((Object)percent);
        source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7a\u2713 \u670d\u52a1\u5668\u5185\u5b58\u9608\u503c\u5df2\u8bbe\u7f6e\u4e3a \u00a7e%d%%", percent)), false);
        return 1;
    }

    private static int executeSetCooldown(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        int seconds = IntegerArgumentType.getInteger(context, (String)"seconds");
        Config.CLEAN_COOLDOWN_SECONDS.set((Object)seconds);
        source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7a\u2713 \u6e05\u7406\u51b7\u5374\u65f6\u95f4\u5df2\u8bbe\u7f6e\u4e3a \u00a7e%d \u00a77\u79d2", seconds)), false);
        return 1;
    }

    private static int executeSetAutoThreshold(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        boolean enabled = BoolArgumentType.getBool(context, (String)"enabled");
        Config.AUTO_CLEAN_ON_THRESHOLD.set((Object)enabled);
        source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7a\u2713 \u9608\u503c\u81ea\u52a8\u6e05\u7406\u5df2%s", enabled ? "\u00a7a\u542f\u7528" : "\u00a7c\u7981\u7528")), false);
        return 1;
    }

    private static int executeSetAggressive(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        boolean enabled = BoolArgumentType.getBool(context, (String)"enabled");
        Config.AGGRESSIVE_MODE.set((Object)enabled);
        source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7a\u2713 \u6fc0\u8fdb\u6a21\u5f0f\u5df2%s", enabled ? "\u00a7a\u542f\u7528" : "\u00a7c\u7981\u7528")), false);
        return 1;
    }

    private static int executeSetBatchSize(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        int value = IntegerArgumentType.getInteger(context, (String)"value");
        Config.CLEAN_BATCH_SIZE.set((Object)value);
        source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7a\u2713 \u6279\u6b21\u5927\u5c0f\u5df2\u8bbe\u7f6e\u4e3a \u00a7e%d", value)), false);
        return 1;
    }

    private static int executeSetServerInterval(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        int minutes = IntegerArgumentType.getInteger(context, (String)"minutes");
        Config.AUTO_CLEAN_INTERVAL.set((Object)minutes);
        source.m_288197_(() -> Component.m_237113_((String)String.format("\u00a7a\u2713 \u81ea\u52a8\u6e05\u7406\u95f4\u9694\u5df2\u8bbe\u7f6e\u4e3a \u00a7e%d \u00a77\u5206\u949f", minutes)), false);
        return 1;
    }

    private static int executeReloadConfig(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack)context.getSource();
        source.m_288197_(() -> Component.m_237113_((String)"\u00a7a\u2713 \u914d\u7f6e\u5df2\u91cd\u65b0\u52a0\u8f7d\uff01"), false);
        return 1;
    }
}

