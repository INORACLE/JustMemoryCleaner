package com.memorycleaner;

import net.minecraft.client.Minecraft;

public class ClientMemoryCleaner {
    private int tickCounter = 0;
    private int lastCleanTick = 0;
    private long lastMemoryUsage = this.getCurrentMemoryUsage();
    private boolean isCleaning = false;

    public ClientMemoryCleaner() {
        MemoryCleanerMod.LOGGER.info("\ud83c\udfae \u5ba2\u6237\u7aef\u5185\u5b58\u6e05\u7406\u5668\u521d\u59cb\u5316\u5b8c\u6210");
    }

    public void onClientTick() {
        ++this.tickCounter;
        if (Config.CLIENT_AUTO_CLEAN.get() && this.tickCounter - this.lastCleanTick >= Config.CLIENT_CLEAN_INTERVAL.get() * 20) {
            this.performClientClean();
            this.lastCleanTick = this.tickCounter;
        }
        if (this.tickCounter % 1200 == 0) {
            this.logMemoryStatus();
        }
    }

    private void performClientClean() {
        if (this.isCleaning) {
            return;
        }
        this.isCleaning = true;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            this.performAggressiveClean();
        } else {
            this.performGentleClean();
        }
        this.isCleaning = false;
    }

    private void performAggressiveClean() {
        long before = this.getCurrentMemoryUsage();
        System.gc();
        long after = this.getCurrentMemoryUsage();
        long freed = before - after;
        if (freed > 10L) {
            MemoryCleanerMod.LOGGER.info("\ud83e\uddf9 [\u5ba2\u6237\u7aef\u6e05\u7406] \u4e3b\u83dc\u5355\u6a21\u5f0f - \u91ca\u653e\u5185\u5b58: {}MB", freed);
        }
    }

    private void performGentleClean() {
        long before = this.getCurrentMemoryUsage();
        long maxMemory = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
        int usagePercent = (int) (before * 100L / maxMemory);
        if (usagePercent >= Config.CLIENT_MEMORY_THRESHOLD.get()) {
            System.gc();
            long after = this.getCurrentMemoryUsage();
            long freed = before - after;
            if (freed > 5L) {
                MemoryCleanerMod.LOGGER.info("\ud83e\uddf9 [\u5ba2\u6237\u7aef\u6e05\u7406] \u6e38\u620f\u6a21\u5f0f - \u91ca\u653e\u5185\u5b58: {}MB (\u4f7f\u7528\u7387: {}%)", freed, usagePercent);
            }
        }
    }

    private void logMemoryStatus() {
        long current = this.getCurrentMemoryUsage();
        long max = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
        int percent = (int) (current * 100L / max);
        Minecraft mc = Minecraft.getInstance();
        String location = mc.level == null ? "\u4e3b\u83dc\u5355" : "\u6e38\u620f\u4e2d";
        MemoryCleanerMod.LOGGER.debug("\ud83d\udcca [\u5ba2\u6237\u7aef\u5185\u5b58] {} - \u4f7f\u7528: {}MB / {}MB ({}%)", location, current, max, percent);
    }

    public long getCurrentMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / 1024L / 1024L;
    }

    public String getStatus() {
        long current = this.getCurrentMemoryUsage();
        long max = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
        int percent = (int) (current * 100L / max);
        return String.format("\u5ba2\u6237\u7aef\u5185\u5b58: %dMB / %dMB (%d%%)", current, max, percent);
    }

    public void forceClean() {
        MemoryCleanerMod.LOGGER.info("\ud83d\ude80 [\u5ba2\u6237\u7aef] \u5f3a\u5236\u6267\u884c\u5185\u5b58\u6e05\u7406...");
        long before = this.getCurrentMemoryUsage();
        System.gc();
        System.gc();
        long after = this.getCurrentMemoryUsage();
        long freed = before - after;
        MemoryCleanerMod.LOGGER.info("\u2705 [\u5ba2\u6237\u7aef] \u6e05\u7406\u5b8c\u6210\uff01\u91ca\u653e\u5185\u5b58: {}MB\uff0c\u5f53\u524d\u4f7f\u7528: {}MB", freed, after);
    }
}