package com.memorycleaner;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@OnlyIn(Dist.CLIENT)
public class ClientCommandHandler {
    private final ClientMemoryCleaner clientCleaner;

    public ClientCommandHandler(ClientMemoryCleaner clientCleaner) {
        this.clientCleaner = clientCleaner;
    }

    @SubscribeEvent
    public void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("mc")
                .then(Commands.literal("clean").executes(context -> {
                    this.performClientClean();
                    return 1;
                }))
                .then(Commands.literal("status").executes(context -> {
                    this.showClientStatus();
                    return 1;
                }))
                .then(Commands.literal("setting")
                        .executes(context -> {
                            this.showClientSettings();
                            return 1;
                        })
                        .then(Commands.literal("threshold").then(Commands.argument("percent", com.mojang.brigadier.arguments.IntegerArgumentType.integer(50, 95)).executes(context -> {
                            int percent = com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "percent");
                            this.setClientThreshold(percent);
                            return 1;
                        })))
                        .then(Commands.literal("interval").then(Commands.argument("seconds", com.mojang.brigadier.arguments.IntegerArgumentType.integer(10, 300)).executes(context -> {
                            int seconds = com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "seconds");
                            this.setClientInterval(seconds);
                            return 1;
                        })))
                        .then(Commands.literal("autoclean").then(Commands.argument("enabled", com.mojang.brigadier.arguments.BoolArgumentType.bool()).executes(context -> {
                            boolean enabled = com.mojang.brigadier.arguments.BoolArgumentType.getBool(context, "enabled");
                            this.setClientAutoClean(enabled);
                            return 1;
                        })))
                        .then(Commands.literal("debug").then(Commands.argument("enabled", com.mojang.brigadier.arguments.BoolArgumentType.bool()).executes(context -> {
                            boolean enabled = com.mojang.brigadier.arguments.BoolArgumentType.getBool(context, "enabled");
                            this.setClientDebug(enabled);
                            return 1;
                        }))))
                .then(Commands.literal("help").executes(context -> {
                    this.showClientHelp();
                    return 1;
                }))
                .executes(context -> {
                    this.showClientHelp();
                    return 1;
                }));
        dispatcher.register(Commands.literal("memorycleaner")
                .then(Commands.literal("clean").executes(context -> {
                    this.performClientClean();
                    return 1;
                }))
                .then(Commands.literal("status").executes(context -> {
                    this.showClientStatus();
                    return 1;
                }))
                .then(Commands.literal("setting").executes(context -> {
                    this.showClientSettings();
                    return 1;
                }))
                .then(Commands.literal("help").executes(context -> {
                    this.showClientHelp();
                    return 1;
                }))
                .executes(context -> {
                    this.showClientHelp();
                    return 1;
                }));
    }

    private void performClientClean() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.displayClientMessage(Component.literal("\u00a7e\u6b63\u5728\u6e05\u7406\u5ba2\u6237\u7aef\u5185\u5b58... \ud83e\uddf9"), false);
        }
        long before = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L;
        System.gc();
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long after = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L;
        long freed = before - after;
        if (mc.player != null) {
            if (freed > 0L) {
                mc.player.displayClientMessage(Component.literal(String.format("\u00a7a\u2713 \u5ba2\u6237\u7aef\u6e05\u7406\u5b8c\u6210\uff01\u91ca\u653e\u4e86 \u00a7b%dMB \u00a7a\u5185\u5b58 \u2728", freed)), false);
            } else {
                mc.player.displayClientMessage(Component.literal("\u00a77\u2713 \u5ba2\u6237\u7aef\u6e05\u7406\u5b8c\u6210\uff0c\u6ca1\u6709\u53ef\u56de\u6536\u7684\u5185\u5b58"), false);
            }
        }
        if (this.clientCleaner != null) {
            this.clientCleaner.forceClean();
        }
    }

    private void showClientStatus() {
        Minecraft mc = Minecraft.getInstance();
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory() / 1024L / 1024L;
        long free = runtime.freeMemory() / 1024L / 1024L;
        long used = total - free;
        long max = runtime.maxMemory() / 1024L / 1024L;
        int percent = (int) (used * 100L / max);
        if (mc.player != null) {
            mc.player.displayClientMessage(Component.literal("\u00a7b===== \u5ba2\u6237\u7aef\u5185\u5b58\u72b6\u6001 ====="), false);
            mc.player.displayClientMessage(Component.literal(String.format("\u00a77\u5df2\u4f7f\u7528: \u00a7a%dMB \u00a77/ \u00a7c%dMB \u00a77(\u00a7e%d%%\u00a77)", used, max, percent)), false);
            mc.player.displayClientMessage(Component.literal(String.format("\u00a77\u5df2\u5206\u914d: \u00a7b%dMB \u00a77| \u7a7a\u95f2: \u00a72%dMB", total, free)), false);
            mc.player.displayClientMessage(Component.literal("\u00a7b========================="), false);
        }
    }

    private void showClientSettings() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.displayClientMessage(Component.literal("\u00a7b===== \u5ba2\u6237\u7aef\u8bbe\u7f6e ====="), false);
            mc.player.displayClientMessage(Component.literal(String.format("\u00a7e\u81ea\u52a8\u6e05\u7406: \u00a7a%s", Config.CLIENT_AUTO_CLEAN.get() ? "\u5df2\u542f\u7528" : "\u5df2\u7981\u7528")), false);
            mc.player.displayClientMessage(Component.literal(String.format("\u00a7e\u6e05\u7406\u95f4\u9694: \u00a7a%d \u00a77\u79d2", Config.CLIENT_CLEAN_INTERVAL.get())), false);
            mc.player.displayClientMessage(Component.literal(String.format("\u00a7e\u5185\u5b58\u9608\u503c: \u00a7a%d%%", Config.CLIENT_MEMORY_THRESHOLD.get())), false);
            mc.player.displayClientMessage(Component.literal(String.format("\u00a7e\u8c03\u8bd5\u6a21\u5f0f: \u00a7a%s", Config.CLIENT_SHOW_DEBUG.get() ? "\u5df2\u542f\u7528" : "\u5df2\u7981\u7528")), false);
            mc.player.displayClientMessage(Component.literal("\u00a77--------------------"), false);
            mc.player.displayClientMessage(Component.literal("\u00a77\u4fee\u6539\u547d\u4ee4:"), false);
            mc.player.displayClientMessage(Component.literal("\u00a7e/mc setting threshold <50-95>"), false);
            mc.player.displayClientMessage(Component.literal("\u00a7e/mc setting interval <10-300>"), false);
            mc.player.displayClientMessage(Component.literal("\u00a7e/mc setting autoclean <true/false>"), false);
            mc.player.displayClientMessage(Component.literal("\u00a7e/mc setting debug <true/false>"), false);
            mc.player.displayClientMessage(Component.literal("\u00a7b====================="), false);
        }
    }

    private void setClientThreshold(int percent) {
        Minecraft mc = Minecraft.getInstance();
        try {
            if (Config.CLIENT_MEMORY_THRESHOLD == null) {
                if (mc.player != null) {
                    mc.player.displayClientMessage(Component.literal("\u00a7c\u2717 \u5ba2\u6237\u7aef\u914d\u7f6e\u672a\u52a0\u8f7d\uff01"), false);
                }
                return;
            }
            Config.CLIENT_MEMORY_THRESHOLD.set(percent);
            if (mc.player != null) {
                mc.player.displayClientMessage(Component.literal(String.format("\u00a7a\u2713 \u5ba2\u6237\u7aef\u5185\u5b58\u9608\u503c\u5df2\u8bbe\u7f6e\u4e3a \u00a7e%d%%", percent)), false);
            }
        } catch (Exception e) {
            if (mc.player != null) {
                mc.player.displayClientMessage(Component.literal("\u00a7c\u2717 \u8bbe\u7f6e\u5931\u8d25: " + e.getMessage()), false);
            }
        }
    }

    private void setClientInterval(int seconds) {
        Minecraft mc = Minecraft.getInstance();
        try {
            if (Config.CLIENT_CLEAN_INTERVAL == null) {
                if (mc.player != null) {
                    mc.player.displayClientMessage(Component.literal("\u00a7c\u2717 \u5ba2\u6237\u7aef\u914d\u7f6e\u672a\u52a0\u8f7d\uff01"), false);
                }
                return;
            }
            Config.CLIENT_CLEAN_INTERVAL.set(seconds);
            if (mc.player != null) {
                mc.player.displayClientMessage(Component.literal(String.format("\u00a7a\u2713 \u5ba2\u6237\u7aef\u6e05\u7406\u95f4\u9694\u5df2\u8bbe\u7f6e\u4e3a \u00a7e%d \u00a77\u79d2", seconds)), false);
            }
        } catch (Exception e) {
            if (mc.player != null) {
                mc.player.displayClientMessage(Component.literal("\u00a7c\u2717 \u8bbe\u7f6e\u5931\u8d25: " + e.getMessage()), false);
            }
        }
    }

    private void setClientAutoClean(boolean enabled) {
        Minecraft mc = Minecraft.getInstance();
        try {
            if (Config.CLIENT_AUTO_CLEAN == null) {
                if (mc.player != null) {
                    mc.player.displayClientMessage(Component.literal("\u00a7c\u2717 \u5ba2\u6237\u7aef\u914d\u7f6e\u672a\u52a0\u8f7d\uff01"), false);
                }
                return;
            }
            Config.CLIENT_AUTO_CLEAN.set(enabled);
            if (mc.player != null) {
                mc.player.displayClientMessage(Component.literal(String.format("\u00a7a\u2713 \u5ba2\u6237\u7aef\u81ea\u52a8\u6e05\u7406\u5df2%s", enabled ? "\u00a7a\u542f\u7528" : "\u00a7c\u7981\u7528")), false);
            }
        } catch (Exception e) {
            if (mc.player != null) {
                mc.player.displayClientMessage(Component.literal("\u00a7c\u2717 \u8bbe\u7f6e\u5931\u8d25: " + e.getMessage()), false);
            }
        }
    }

    private void setClientDebug(boolean enabled) {
        Minecraft mc = Minecraft.getInstance();
        try {
            if (Config.CLIENT_SHOW_DEBUG == null) {
                if (mc.player != null) {
                    mc.player.displayClientMessage(Component.literal("\u00a7c\u2717 \u5ba2\u6237\u7aef\u914d\u7f6e\u672a\u52a0\u8f7d\uff01"), false);
                }
                return;
            }
            Config.CLIENT_SHOW_DEBUG.set(enabled);
            if (mc.player != null) {
                mc.player.displayClientMessage(Component.literal(String.format("\u00a7a\u2713 \u5ba2\u6237\u7aef\u8c03\u8bd5\u6a21\u5f0f\u5df2%s", enabled ? "\u00a7a\u542f\u7528" : "\u00a7c\u7981\u7528")), false);
            }
        } catch (Exception e) {
            if (mc.player != null) {
                mc.player.displayClientMessage(Component.literal("\u00a7c\u2717 \u8bbe\u7f6e\u5931\u8d25: " + e.getMessage()), false);
            }
        }
    }

    private void showClientHelp() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.displayClientMessage(Component.literal("\u00a7b========== \ud83d\udcda \u5185\u5b58\u6e05\u7406\u5668\u5e2e\u52a9 =========="), false);
            mc.player.displayClientMessage(Component.literal(""), false);
            mc.player.displayClientMessage(Component.literal("\u00a76\ud83c\udfae \u5ba2\u6237\u7aef\u547d\u4ee4 (\u4ec5\u5f71\u54cd\u4f60\u7684\u6e38\u620f):"), false);
            mc.player.displayClientMessage(Component.literal("\u00a7e/mc clean \u00a77- \u6e05\u7406\u5ba2\u6237\u7aef\u5185\u5b58"), false);
            mc.player.displayClientMessage(Component.literal("\u00a7e/mc status \u00a77- \u67e5\u770b\u5ba2\u6237\u7aef\u5185\u5b58\u72b6\u6001"), false);
            mc.player.displayClientMessage(Component.literal("\u00a7e/mc setting \u00a77- \u67e5\u770b/\u4fee\u6539\u5ba2\u6237\u7aef\u8bbe\u7f6e"), false);
            mc.player.displayClientMessage(Component.literal(""), false);
            mc.player.displayClientMessage(Component.literal("\u00a76\ud83d\udda5\ufe0f \u670d\u52a1\u5668\u547d\u4ee4 (\u9700\u8981\u7ba1\u7406\u5458\u6743\u9650):"), false);
            mc.player.displayClientMessage(Component.literal("\u00a7e/mc server clean \u00a77- \u6e05\u7406\u670d\u52a1\u5668\u5185\u5b58"), false);
            mc.player.displayClientMessage(Component.literal("\u00a7e/mc server status \u00a77- \u67e5\u770b\u670d\u52a1\u5668\u5185\u5b58\u72b6\u6001"), false);
            mc.player.displayClientMessage(Component.literal("\u00a7e/mc server setting \u00a77- \u67e5\u770b/\u4fee\u6539\u670d\u52a1\u5668\u8bbe\u7f6e"), false);
            mc.player.displayClientMessage(Component.literal(""), false);
            mc.player.displayClientMessage(Component.literal("\u00a77\ud83d\udca1 \u63d0\u793a: \u5ba2\u6237\u7aef\u547d\u4ee4\u4f1a\u5728\u672c\u5730\u6267\u884c\uff0c\u4e0d\u5f71\u54cd\u670d\u52a1\u5668"), false);
            mc.player.displayClientMessage(Component.literal("\u00a7b======================================"), false);
        }
    }
}