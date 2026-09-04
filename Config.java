/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.ForgeConfigSpec
 *  net.minecraftforge.common.ForgeConfigSpec$BooleanValue
 *  net.minecraftforge.common.ForgeConfigSpec$Builder
 *  net.minecraftforge.common.ForgeConfigSpec$IntValue
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.config.ModConfigEvent$Loading
 *  net.minecraftforge.fml.event.config.ModConfigEvent$Reloading
 */
package com.memorycleaner;

import com.memorycleaner.MemoryCleanerMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid="memorycleaner", bus=Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    public static final ForgeConfigSpec SERVER_SPEC;
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec.BooleanValue AUTO_CLEAN_ENABLED;
    public static final ForgeConfigSpec.IntValue AUTO_CLEAN_INTERVAL;
    public static final ForgeConfigSpec.IntValue MEMORY_THRESHOLD_PERCENT;
    public static final ForgeConfigSpec.BooleanValue CLEAN_UNLOADED_CHUNKS;
    public static final ForgeConfigSpec.IntValue CHUNK_UNLOAD_THRESHOLD_TICKS;
    public static final ForgeConfigSpec.IntValue MAX_CHUNKS_PER_CLEAN;
    public static final ForgeConfigSpec.BooleanValue CLEAN_INVALID_PLAYERS;
    public static final ForgeConfigSpec.IntValue PLAYER_CLEAN_INTERVAL;
    public static final ForgeConfigSpec.IntValue CLEAN_BATCH_SIZE;
    public static final ForgeConfigSpec.IntValue CLEAN_DELAY_TICKS;
    public static final ForgeConfigSpec.BooleanValue AGGRESSIVE_MODE;
    public static final ForgeConfigSpec.IntValue CLEAN_COOLDOWN_SECONDS;
    public static final ForgeConfigSpec.BooleanValue AUTO_CLEAN_ON_THRESHOLD;
    public static final ForgeConfigSpec.BooleanValue CLIENT_AUTO_CLEAN;
    public static final ForgeConfigSpec.IntValue CLIENT_CLEAN_INTERVAL;
    public static final ForgeConfigSpec.IntValue CLIENT_MEMORY_THRESHOLD;
    public static final ForgeConfigSpec.BooleanValue CLIENT_SHOW_DEBUG;

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        MemoryCleanerMod.LOGGER.info("\u914d\u7f6e\u5df2\u52a0\u8f7d \u2713");
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        MemoryCleanerMod.LOGGER.info("\u914d\u7f6e\u5df2\u91cd\u65b0\u52a0\u8f7d \u2713");
    }

    static {
        ForgeConfigSpec.Builder serverBuilder = new ForgeConfigSpec.Builder();
        serverBuilder.push("auto_clean");
        AUTO_CLEAN_ENABLED = serverBuilder.comment("\u542f\u7528\u81ea\u52a8\u5185\u5b58\u6e05\u7406").define("enabled", true);
        AUTO_CLEAN_INTERVAL = serverBuilder.comment("\u81ea\u52a8\u6e05\u7406\u95f4\u9694\uff08\u5206\u949f\uff09").defineInRange("interval", 10, 1, 60);
        MEMORY_THRESHOLD_PERCENT = serverBuilder.comment("\u5185\u5b58\u4f7f\u7528\u9608\u503c\u767e\u5206\u6bd4\uff0c\u8d85\u8fc7\u6b64\u503c\u89e6\u53d1\u6e05\u7406").defineInRange("threshold", 80, 50, 95);
        serverBuilder.pop();
        serverBuilder.push("chunk_cleanup");
        CLEAN_UNLOADED_CHUNKS = serverBuilder.comment("\u6e05\u7406\u5df2\u5378\u8f7d\u4f46\u672a\u91ca\u653e\u7684\u533a\u5757").define("clean_unloaded", true);
        CHUNK_UNLOAD_THRESHOLD_TICKS = serverBuilder.comment("\u533a\u5757\u5378\u8f7d\u540e\u591a\u4e45\u53ef\u4ee5\u6e05\u7406\uff08tick\uff09").defineInRange("unload_threshold", 600, 100, 6000);
        MAX_CHUNKS_PER_CLEAN = serverBuilder.comment("\u6bcf\u6b21\u6e05\u7406\u7684\u6700\u5927\u533a\u5757\u6570\u91cf").defineInRange("max_per_clean", 1000, 100, 10000);
        serverBuilder.pop();
        serverBuilder.push("player_cleanup");
        CLEAN_INVALID_PLAYERS = serverBuilder.comment("\u6e05\u7406\u65e0\u6548\u7684\u73a9\u5bb6\u5bf9\u8c61").define("clean_invalid", true);
        PLAYER_CLEAN_INTERVAL = serverBuilder.comment("\u73a9\u5bb6\u6e05\u7406\u95f4\u9694\uff08\u5206\u949f\uff09").defineInRange("interval", 5, 1, 30);
        serverBuilder.pop();
        serverBuilder.push("performance");
        CLEAN_BATCH_SIZE = serverBuilder.comment("\u6bcf\u6279\u6b21\u6e05\u7406\u7684\u5bf9\u8c61\u6570\u91cf\uff08\u907f\u514d\u5361\u987f\uff09").defineInRange("batch_size", 50, 10, 500);
        CLEAN_DELAY_TICKS = serverBuilder.comment("\u6279\u6b21\u95f4\u5ef6\u8fdf\uff08tick\uff09").defineInRange("delay_ticks", 5, 1, 20);
        AGGRESSIVE_MODE = serverBuilder.comment("\u6fc0\u8fdb\u6a21\u5f0f - \u66f4\u9891\u7e41\u4f46\u66f4\u5f7b\u5e95\u7684\u6e05\u7406").define("aggressive", false);
        CLEAN_COOLDOWN_SECONDS = serverBuilder.comment("\u6e05\u7406\u51b7\u5374\u65f6\u95f4\uff08\u79d2\uff09- \u907f\u514d\u9891\u7e41\u6e05\u7406\u5bfc\u81f4\u5361\u987f").defineInRange("cooldown_seconds", 60, 10, 300);
        AUTO_CLEAN_ON_THRESHOLD = serverBuilder.comment("\u8d85\u8fc7\u5185\u5b58\u9608\u503c\u65f6\u81ea\u52a8\u6e05\u7406").define("auto_clean_on_threshold", true);
        serverBuilder.pop();
        SERVER_SPEC = serverBuilder.build();
        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();
        clientBuilder.push("client");
        CLIENT_AUTO_CLEAN = clientBuilder.comment("\u5ba2\u6237\u7aef\u81ea\u52a8\u6e05\u7406").define("auto_clean", true);
        CLIENT_CLEAN_INTERVAL = clientBuilder.comment("\u5ba2\u6237\u7aef\u6e05\u7406\u95f4\u9694\uff08\u79d2\uff09").defineInRange("clean_interval", 60, 10, 300);
        CLIENT_MEMORY_THRESHOLD = clientBuilder.comment("\u5ba2\u6237\u7aef\u5185\u5b58\u9608\u503c\u767e\u5206\u6bd4").defineInRange("memory_threshold", 85, 50, 95);
        CLIENT_SHOW_DEBUG = clientBuilder.comment("\u663e\u793a\u8c03\u8bd5\u4fe1\u606f").define("show_debug", false);
        clientBuilder.pop();
        CLIENT_SPEC = clientBuilder.build();
    }
}

