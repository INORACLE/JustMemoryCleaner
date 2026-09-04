package com.memorycleaner;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import it.unimi.dsi.fastutil.longs.LongSet;

public class MemoryCleaner {
    private MinecraftServer server;
    private int tickCounter = 0;
    private int lastChunkCleanTick = 0;
    private int lastPlayerCleanTick = 0;
    private int lastAutoCleanTick = 0;
    private long lastAutoCleanTime = 0L;
    private final Map<ChunkPos, Long> chunkLastAccessTime = new ConcurrentHashMap<>();
    private final Set<ChunkPos> pendingUnloadChunks = ConcurrentHashMap.newKeySet();
    private boolean isCleaning = false;
    private int cleanPhase = 0;
    private int batchCounter = 0;
    private final AtomicInteger totalChunksCleaned = new AtomicInteger(0);
    private final AtomicInteger totalPlayersCleaned = new AtomicInteger(0);
    private long lastMemoryUsage = 0L;
    private Field chunksField;
    private Field toDropField;
    private boolean reflectionInitialized = false;
    private int lastTrackedChunkCount = 0;

    public void init(MinecraftServer server) {
        this.server = server;
        this.lastMemoryUsage = this.getCurrentMemoryUsage();
        this.initReflection();
        MemoryCleanerMod.LOGGER.info("========================================");
        MemoryCleanerMod.LOGGER.info("\ud83d\ude80 MemoryCleaner \u5185\u5b58\u6e05\u7406\u5668\u5df2\u542f\u52a8\uff01");
        MemoryCleanerMod.LOGGER.info("========================================");
        MemoryCleanerMod.LOGGER.info("\u2705 \u5f53\u524d\u5185\u5b58\u4f7f\u7528: {}MB / {}MB", this.lastMemoryUsage, this.getMaxMemory());
        MemoryCleanerMod.LOGGER.info("\u2705 \u53cd\u5c04\u521d\u59cb\u5316\u72b6\u6001: {}", this.reflectionInitialized ? "\u6210\u529f" : "\u5931\u8d25");
        MemoryCleanerMod.LOGGER.info("\u2705 \u81ea\u52a8\u6e05\u7406: {}", Config.AUTO_CLEAN_ENABLED.get() ? "\u5df2\u542f\u7528" : "\u5df2\u7981\u7528");
        MemoryCleanerMod.LOGGER.info("\u2705 \u533a\u5757\u6e05\u7406: {}", Config.CLEAN_UNLOADED_CHUNKS.get() ? "\u5df2\u542f\u7528" : "\u5df2\u7981\u7528");
        MemoryCleanerMod.LOGGER.info("\u2705 \u73a9\u5bb6\u6e05\u7406: {}", Config.CLEAN_INVALID_PLAYERS.get() ? "\u5df2\u542f\u7528" : "\u5df2\u7981\u7528");
        MemoryCleanerMod.LOGGER.info("========================================");
    }

    public void shutdown() {
        MemoryCleanerMod.LOGGER.info("\ud83d\udeaa [\u5173\u95ed\u6e05\u7406] \u5f00\u59cb\u6267\u884c\u670d\u52a1\u5668\u5173\u95ed\u524d\u7684\u6700\u7ec8\u6e05\u7406...");
        this.chunkLastAccessTime.clear();
        this.pendingUnloadChunks.clear();
        MemoryCleanerMod.LOGGER.info("\u2705 [\u5173\u95ed\u6e05\u7406] \u670d\u52a1\u5668\u5173\u95ed\u5b8c\u6210\uff0c\u7d2f\u8ba1\u6e05\u7406\u533a\u5757: {}\uff0c\u73a9\u5bb6\u5bf9\u8c61: {}", this.totalChunksCleaned.get(), this.totalPlayersCleaned.get());
    }

    @SuppressWarnings("unchecked")
    private void cleanInvalidPlayerReferences() {
        if (this.server == null) {
            return;
        }
        int cleaned = 0;
        List<ServerPlayer> onlinePlayers = this.server.getPlayerList().getPlayers();
        HashSet<UUID> onlineUUIDs = new HashSet<>();
        for (ServerPlayer player : onlinePlayers) {
            onlineUUIDs.add(player.getUUID());
        }
        try {
            for (ServerLevel level : this.server.getAllLevels()) {
                cleaned += this.cleanInvalidPlayerReferences(level, onlineUUIDs);
            }
        } catch (Exception e) {
            MemoryCleanerMod.LOGGER.debug("\u6e05\u7406\u73a9\u5bb6\u5f15\u7528\u65f6\u51fa\u9519: {}", e.getMessage());
        }
        this.totalPlayersCleaned.addAndGet(cleaned);
        if (cleaned > 0) {
            MemoryCleanerMod.LOGGER.info("\ud83d\udc64 [\u73a9\u5bb6\u6e05\u7406] \u6e05\u7406\u4e86 {} \u4e2a\u65e0\u6548\u73a9\u5bb6\u5bf9\u8c61\uff0c\u7d2f\u8ba1\u6e05\u7406: {}", cleaned, this.totalPlayersCleaned.get());
        }
    }

    private void cleanAllUnloadedChunks() {
        if (this.server == null) {
            return;
        }
        int cleaned = 0;
        if (this.reflectionInitialized) {
            Iterator<Map.Entry<ChunkPos, Long>> iterator = this.chunkLastAccessTime.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ChunkPos, Long> entry = iterator.next();
                if (!this.tryUnloadChunk(entry.getKey())) continue;
                iterator.remove();
                ++cleaned;
            }
        } else {
            for (ServerLevel level : this.server.getAllLevels()) {
                ServerChunkCache chunkCache = level.getChunkSource();
                try {
                    chunkCache.save(false);
                    ++cleaned;
                } catch (Exception e) {
                    MemoryCleanerMod.LOGGER.debug("\u6e05\u7406\u533a\u5757\u65f6\u51fa\u9519: {}", e.getMessage());
                }
            }
        }
        this.totalChunksCleaned.addAndGet(cleaned);
        if (cleaned > 0) {
            MemoryCleanerMod.LOGGER.info("\ud83d\uddd1\ufe0f [\u533a\u5757\u6e05\u7406] \u6e05\u7406\u4e86 {} \u4e2a\u533a\u5757\uff0c\u7d2f\u8ba1\u6e05\u7406: {}", cleaned, this.totalChunksCleaned.get());
        }
    }

    private void initReflection() {
        try {
            String[] possibleFieldNames = new String[]{"chunks", "loadedChunks", "chunkMap", "storage", "chunkCache", "levelChunks",
                    "f_8326_", "f_8327_", "f_8329_", "f_8330_", "f_8331_", "f_8332_", "f_8325_", "f_8333_", "f_8334_", "f_8335_",
                    "f_8336_", "f_143226_", "f_8337_", "f_8338_", "f_8339_", "f_8340_"};
            for (String fieldName : possibleFieldNames) {
                try {
                    this.chunksField = ServerChunkCache.class.getDeclaredField(fieldName);
                    this.chunksField.setAccessible(true);
                    this.reflectionInitialized = true;
                    MemoryCleanerMod.LOGGER.info("\u53cd\u5c04\u521d\u59cb\u5316\u6210\u529f\uff0c\u4f7f\u7528\u5b57\u6bb5: {}", fieldName);
                    return;
                } catch (NoSuchFieldException noSuchFieldException) {
                } catch (Exception exception) {
                }
            }
            MemoryCleanerMod.LOGGER.warn("\u53cd\u5c04\u521d\u59cb\u5316\u5931\u8d25\uff0c\u5c06\u4f7f\u7528\u5907\u7528\u6e05\u7406\u7b56\u7565");
        } catch (Exception e) {
            MemoryCleanerMod.LOGGER.warn("\u53cd\u5c04\u521d\u59cb\u5316\u5931\u8d25\uff0c\u5c06\u4f7f\u7528\u5907\u7528\u6e05\u7406\u7b56\u7565: {}", e.getMessage());
        }
    }

    public void onServerTick() {
        ++this.tickCounter;
        if (this.isCleaning) {
            this.performGradualClean();
            return;
        }
        if (this.tickCounter % 100 == 0) {
            this.trackChunkAccess();
        }
        if (Config.AUTO_CLEAN_ENABLED.get() && this.tickCounter - this.lastAutoCleanTick >= Config.AUTO_CLEAN_INTERVAL.get() * 60 * 20) {
            if (this.shouldAutoClean()) {
                this.startAutoClean();
            }
            this.lastAutoCleanTick = this.tickCounter;
        }
        if (Config.CLEAN_UNLOADED_CHUNKS.get() && this.tickCounter - this.lastChunkCleanTick >= 1200) {
            this.startChunkClean();
            this.lastChunkCleanTick = this.tickCounter;
        }
        if (Config.CLEAN_INVALID_PLAYERS.get() && this.tickCounter - this.lastPlayerCleanTick >= Config.PLAYER_CLEAN_INTERVAL.get() * 60 * 20) {
            this.startPlayerClean();
            this.lastPlayerCleanTick = this.tickCounter;
        }
    }

    private void trackChunkAccess() {
        if (this.server == null) {
            return;
        }
        try {
            int totalTracked = 0;
            for (ServerLevel level : this.server.getAllLevels()) {
                ServerChunkCache chunkCache = level.getChunkSource();
                for (int x = -32; x <= 32; ++x) {
                    for (int z = -32; z <= 32; ++z) {
                        LevelChunk chunk = chunkCache.getChunk(x, z, false);
                        if (chunk == null) continue;
                        ChunkPos pos = chunk.getPos();
                        this.chunkLastAccessTime.put(pos, System.currentTimeMillis());
                        ++totalTracked;
                    }
                }
            }
            if (this.tickCounter % 600 == 0) {
                MemoryCleanerMod.LOGGER.info("\ud83d\udcca [\u533a\u5757\u8ffd\u8e2a] \u5f53\u524d\u8ffd\u8e2a\u533a\u5757\u6570: {} (\u65b0\u589e: {})\uff0c\u4f7f\u7528{}\u65b9\u5f0f", totalTracked, totalTracked - this.lastTrackedChunkCount, "\u76f4\u63a5\u8bbf\u95ee");
                this.lastTrackedChunkCount = totalTracked;
            }
        } catch (Exception e) {
            MemoryCleanerMod.LOGGER.debug("\u8ffd\u8e2a\u533a\u5757\u8bbf\u95ee\u65f6\u51fa\u9519: {}", e.getMessage());
        }
    }

    private boolean shouldAutoClean() {
        if (!Config.AUTO_CLEAN_ON_THRESHOLD.get()) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        long timeSinceLastClean = currentTime - this.lastAutoCleanTime;
        long cooldownMs = (long) Config.CLEAN_COOLDOWN_SECONDS.get() * 1000L;
        if (timeSinceLastClean < cooldownMs) {
            long remainingSeconds = (cooldownMs - timeSinceLastClean) / 1000L;
            MemoryCleanerMod.LOGGER.debug("\u51b7\u5374\u4e2d\uff0c\u8fd8\u9700\u7b49\u5f85 {} \u79d2", remainingSeconds);
            return false;
        }
        long currentMemory = this.getCurrentMemoryUsage();
        long maxMemory = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
        int usagePercent = (int) (currentMemory * 100L / maxMemory);
        return usagePercent >= Config.MEMORY_THRESHOLD_PERCENT.get();
    }

    private void startAutoClean() {
        long currentMemory = this.getCurrentMemoryUsage();
        long maxMemory = this.getMaxMemory();
        int percent = (int) (currentMemory * 100L / maxMemory);
        MemoryCleanerMod.LOGGER.info("\u26a0\ufe0f [\u81ea\u52a8\u6e05\u7406\u89e6\u53d1] \u5185\u5b58\u4f7f\u7528: {}MB / {}MB ({}%)\uff0c\u8d85\u8fc7\u9608\u503c {}%", currentMemory, maxMemory, percent, Config.MEMORY_THRESHOLD_PERCENT.get());
        MemoryCleanerMod.LOGGER.info("\ud83e\uddf9 \u542f\u52a8\u81ea\u52a8\u6e05\u7406...");
        this.isCleaning = true;
        this.cleanPhase = 0;
        this.batchCounter = 0;
        this.lastAutoCleanTime = System.currentTimeMillis();
    }

    private void startChunkClean() {
        if (this.isCleaning) {
            return;
        }
        MemoryCleanerMod.LOGGER.info("\ud83e\uddf9 [\u5b9a\u671f\u6e05\u7406] \u5f00\u59cb\u6e05\u7406\u533a\u5757\u7f13\u5b58...");
        this.isCleaning = true;
        this.cleanPhase = 1;
        this.batchCounter = 0;
    }

    private void startPlayerClean() {
        if (this.isCleaning) {
            return;
        }
        MemoryCleanerMod.LOGGER.info("\ud83e\uddf9 [\u5b9a\u671f\u6e05\u7406] \u5f00\u59cb\u6e05\u7406\u73a9\u5bb6\u5bf9\u8c61...");
        this.isCleaning = true;
        this.cleanPhase = 2;
        this.batchCounter = 0;
    }

    private void performGradualClean() {
        int batchSize = Config.CLEAN_BATCH_SIZE.get();
        int delayTicks = Config.CLEAN_DELAY_TICKS.get();
        if (Config.AGGRESSIVE_MODE.get()) {
            delayTicks = Math.max(1, delayTicks / 2);
            batchSize *= 2;
        }
        ++this.batchCounter;
        if (this.batchCounter < delayTicks) {
            return;
        }
        this.batchCounter = 0;
        boolean completed = false;
        switch (this.cleanPhase) {
            case 0:
                if (!this.cleanChunksBatch(batchSize / 2)) {
                    this.cleanPhase = 2;
                }
                break;
            case 1:
                if (!this.cleanChunksBatch(batchSize)) {
                    completed = true;
                }
                break;
            case 2:
                if (!this.cleanPlayersBatch(batchSize)) {
                    this.cleanPhase = 3;
                }
                break;
            case 3:
                if (!this.cleanServerLevelBatch(batchSize)) {
                    completed = true;
                }
                break;
        }
        if (completed) {
            this.isCleaning = false;
            this.cleanPhase = 0;
            long memoryAfter = this.getCurrentMemoryUsage();
            long freed = this.lastMemoryUsage - memoryAfter;
            this.lastMemoryUsage = memoryAfter;
            if (freed > 0L) {
                MemoryCleanerMod.LOGGER.info("\u2705 [\u6e05\u7406\u5b8c\u6210] \u91ca\u653e\u5185\u5b58: {}MB\uff0c\u5f53\u524d\u4f7f\u7528: {}MB \ud83d\udcab", freed, memoryAfter);
            } else {
                MemoryCleanerMod.LOGGER.info("\u2705 [\u6e05\u7406\u5b8c\u6210] \u5f53\u524d\u5185\u5b58\u4f7f\u7528: {}MB", memoryAfter);
            }
            if (Config.AGGRESSIVE_MODE.get() && freed > 50L) {
                MemoryCleanerMod.LOGGER.info("\ud83e\uddf9 [\u6fc0\u8fdb\u6a21\u5f0f] \u89e6\u53d1\u989d\u5916GC\u4ee5\u91ca\u653e\u66f4\u591a\u5185\u5b58...");
            }
        }
    }

    private boolean cleanChunksBatch(int batchSize) {
        if (this.server == null) {
            return false;
        }
        int cleaned = 0;
        if (this.reflectionInitialized) {
            long currentTime = System.currentTimeMillis();
            long threshold = (long) Config.CHUNK_UNLOAD_THRESHOLD_TICKS.get() * 50L;
            Iterator<Map.Entry<ChunkPos, Long>> iterator = this.chunkLastAccessTime.entrySet().iterator();
            while (iterator.hasNext() && cleaned < batchSize) {
                Map.Entry<ChunkPos, Long> entry = iterator.next();
                long inactiveTime = currentTime - entry.getValue();
                if (inactiveTime <= threshold || !this.tryUnloadChunk(entry.getKey())) continue;
                iterator.remove();
                ++cleaned;
            }
            this.pendingUnloadChunks.removeIf(pos -> !this.chunkLastAccessTime.containsKey(pos));
        } else {
            for (ServerLevel level : this.server.getAllLevels()) {
                ServerChunkCache chunkCache = level.getChunkSource();
                try {
                    chunkCache.save(false);
                    ++cleaned;
                } catch (Exception e) {
                    MemoryCleanerMod.LOGGER.debug("\u6e05\u7406\u533a\u5757\u65f6\u51fa\u9519: {}", e.getMessage());
                }
            }
        }
        this.totalChunksCleaned.addAndGet(cleaned);
        if (cleaned > 0) {
            MemoryCleanerMod.LOGGER.info("\ud83d\uddd1\ufe0f [\u533a\u5757\u6e05\u7406] \u672c\u6279\u6b21\u6e05\u7406\u4e86 {} \u4e2a\u533a\u5757\uff0c\u7d2f\u8ba1\u6e05\u7406: {}", cleaned, this.totalChunksCleaned.get());
        }
        return this.chunkLastAccessTime.size() > Config.MAX_CHUNKS_PER_CLEAN.get();
    }

    private boolean tryUnloadChunk(ChunkPos pos) {
        if (this.server == null) {
            return false;
        }
        try {
            for (ServerLevel level : this.server.getAllLevels()) {
                ServerChunkCache chunkCache = level.getChunkSource();
                if (!chunkCache.hasChunk(pos.x, pos.z)) {
                    return true;
                }
                this.scheduleChunkUnload(chunkCache, pos);
                try {
                    chunkCache.save(false);
                } catch (Exception e) {
                    MemoryCleanerMod.LOGGER.debug("\u4fdd\u5b58\u533a\u5757\u65f6\u51fa\u9519: {}", e.getMessage());
                }
                try {
                    List<ServerPlayer> players = this.server.getPlayerList().getPlayers();
                    boolean isNearPlayer = false;
                    for (ServerPlayer player : players) {
                        ChunkPos playerPos = player.chunkPosition();
                        int distance = Math.max(Math.abs(pos.x - playerPos.x), Math.abs(pos.z - playerPos.z));
                        if (distance >= 16) continue;
                        isNearPlayer = true;
                        break;
                    }
                    if (isNearPlayer) continue;
                    MemoryCleanerMod.LOGGER.debug("\u5c1d\u8bd5\u5f3a\u5236\u5378\u8f7d\u533a\u5757: {}", pos);
                    level.save(null, false, false);
                } catch (Exception e) {
                    MemoryCleanerMod.LOGGER.debug("\u68c0\u67e5\u73a9\u5bb6\u9644\u8fd1\u533a\u5757\u65f6\u51fa\u9519: {}", e.getMessage());
                }
            }
            return true;
        } catch (Exception e) {
            MemoryCleanerMod.LOGGER.debug("\u5c1d\u8bd5\u5378\u8f7d\u533a\u5757\u65f6\u51fa\u9519: {}", e.getMessage());
            return false;
        }
    }

    private void scheduleChunkUnload(ServerChunkCache chunkCache, ChunkPos pos) {
        try {
            if (this.toDropField == null) {
                this.toDropField = ChunkMap.class.getDeclaredField("toDrop");
                this.toDropField.setAccessible(true);
            }
            LongSet toDrop = (LongSet) this.toDropField.get(chunkCache.chunkMap);
            toDrop.add(pos.toLong());
        } catch (ReflectiveOperationException e) {
            MemoryCleanerMod.LOGGER.debug("\u5f3a\u5236\u5378\u8f7d\u533a\u5757\u65f6\u51fa\u9519: {}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private boolean cleanPlayersBatch(int batchSize) {
        if (this.server == null) {
            return false;
        }
        int cleaned = 0;
        List<ServerPlayer> onlinePlayers = this.server.getPlayerList().getPlayers();
        HashSet<UUID> onlineUUIDs = new HashSet<>();
        for (ServerPlayer player : onlinePlayers) {
            onlineUUIDs.add(player.getUUID());
        }
        MemoryCleanerMod.LOGGER.info("\ud83d\udc64 [\u73a9\u5bb6\u6e05\u7406] \u5f00\u59cb\u68c0\u67e5\u73a9\u5bb6\u5bf9\u8c61\uff0c\u5f53\u524d\u5728\u7ebf: {}", onlinePlayers.size());
        try {
            for (ServerLevel level : this.server.getAllLevels()) {
                cleaned += this.cleanInvalidPlayerReferences(level, onlineUUIDs);
            }
        } catch (Exception e) {
            MemoryCleanerMod.LOGGER.debug("\u6e05\u7406\u73a9\u5bb6\u5f15\u7528\u65f6\u51fa\u9519: {}", e.getMessage());
        }
        try {
            cleaned += this.cleanPlayerDataCache(onlineUUIDs);
        } catch (Exception e) {
            MemoryCleanerMod.LOGGER.debug("\u6e05\u7406\u73a9\u5bb6\u6570\u636e\u7f13\u5b58\u65f6\u51fa\u9519: {}", e.getMessage());
        }
        try {
            HashMap<UUID, Integer> uuidCount = new HashMap<>();
            for (ServerPlayer serverPlayer : onlinePlayers) {
                UUID uuid = serverPlayer.getUUID();
                uuidCount.put(uuid, uuidCount.getOrDefault(uuid, 0) + 1);
            }
            for (Map.Entry<UUID, Integer> entry : uuidCount.entrySet()) {
                if (entry.getValue() <= 1) continue;
                MemoryCleanerMod.LOGGER.warn("\ud83d\udea8 [\u73a9\u5bb6\u6e05\u7406] \u53d1\u73b0\u91cd\u590d\u7684\u73a9\u5bb6 UUID: {}\uff0c\u51fa\u73b0\u6b21\u6570: {}", entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            MemoryCleanerMod.LOGGER.debug("\u68c0\u67e5\u91cd\u590d\u73a9\u5bb6 UUID \u65f6\u51fa\u9519: {}", e.getMessage());
        }
        System.gc();
        if (cleaned > 0) {
            this.totalPlayersCleaned.addAndGet(cleaned);
            MemoryCleanerMod.LOGGER.info("\ud83d\udc64 [\u73a9\u5bb6\u6e05\u7406] \u6e05\u7406\u4e86 {} \u4e2a\u65e0\u6548\u73a9\u5bb6\u5f15\u7528\uff0c\u5f53\u524d\u5728\u7ebf\u73a9\u5bb6: {}", cleaned, onlinePlayers.size());
        } else {
            MemoryCleanerMod.LOGGER.info("\ud83d\udc64 [\u73a9\u5bb6\u6e05\u7406] \u672a\u53d1\u73b0\u65e0\u6548\u73a9\u5bb6\u5bf9\u8c61\uff0c\u5f53\u524d\u5728\u7ebf\u73a9\u5bb6: {}", onlinePlayers.size());
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private int cleanInvalidPlayerReferences(ServerLevel level, Set<UUID> onlineUUIDs) {
        int cleaned = 0;
        try {
            for (Entity entity : level.getAllEntities()) {
                if (!(entity instanceof ServerPlayer player)) continue;
                UUID playerUUID = player.getUUID();
                if (onlineUUIDs.contains(playerUUID)) continue;
                MemoryCleanerMod.LOGGER.warn("\ud83d\udea8 [\u73a9\u5bb6\u6e05\u7406] \u53d1\u73b0\u65e0\u6548\u73a9\u5bb6\u5f15\u7528: {} ({})", player.getName().getString(), playerUUID);
                ++cleaned;
            }
            try {
                PlayerList playerList = this.server.getPlayerList();
                Field playersField = playerList.getClass().getDeclaredField("players");
                playersField.setAccessible(true);
                List<ServerPlayer> playersList = (List<ServerPlayer>) playersField.get(playerList);
                Iterator<ServerPlayer> iterator = playersList.iterator();
                while (iterator.hasNext()) {
                    ServerPlayer player = iterator.next();
                    if (player != null && onlineUUIDs.contains(player.getUUID())) continue;
                    iterator.remove();
                    ++cleaned;
                    MemoryCleanerMod.LOGGER.info("\u2705 [\u73a9\u5bb6\u6e05\u7406] \u5df2\u6e05\u7406\u73a9\u5bb6\u5217\u8868\u4e2d\u7684\u65e0\u6548\u5f15\u7528");
                }
            } catch (Exception e) {
                MemoryCleanerMod.LOGGER.debug("\u6e05\u7406\u73a9\u5bb6\u5217\u8868\u65f6\u51fa\u9519: {}", e.getMessage());
            }
        } catch (Exception e) {
            MemoryCleanerMod.LOGGER.debug("\u68c0\u67e5\u73a9\u5bb6\u5f15\u7528\u65f6\u51fa\u9519: {}", e.getMessage());
        }
        return cleaned;
    }

    @SuppressWarnings("unchecked")
    private int cleanPlayerDataCache(Set<UUID> onlineUUIDs) {
        int cleaned = 0;
        try {
            for (ServerLevel level : this.server.getAllLevels()) {
                try {
                    level.save(null, false, false);
                    ++cleaned;
                } catch (Exception e) {
                    MemoryCleanerMod.LOGGER.debug("\u4fdd\u5b58\u7ef4\u5ea6\u6570\u636e\u65f6\u51fa\u9519: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            MemoryCleanerMod.LOGGER.debug("\u6e05\u7406\u73a9\u5bb6\u6570\u636e\u7f13\u5b58\u65f6\u51fa\u9519: {}", e.getMessage());
        }
        try {
            for (ServerPlayer player : this.server.getPlayerList().getPlayers()) {
                try {
                    player.saveWithoutId(null);
                    ++cleaned;
                } catch (Exception e) {
                    MemoryCleanerMod.LOGGER.debug("\u4fdd\u5b58\u73a9\u5bb6\u6570\u636e\u65f6\u51fa\u9519: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            MemoryCleanerMod.LOGGER.debug("\u6e05\u7406\u73a9\u5bb6\u7edf\u8ba1\u6570\u636e\u65f6\u51fa\u9519: {}", e.getMessage());
        }
        return cleaned;
    }

    private boolean cleanServerLevelBatch(int batchSize) {
        if (this.server == null) {
            return false;
        }
        int cleaned = 0;
        try {
            for (ServerLevel level : this.server.getAllLevels()) {
                level.save(null, false, false);
                ++cleaned;
            }
        } catch (Exception e) {
            MemoryCleanerMod.LOGGER.debug("\u6e05\u7406\u670d\u52a1\u5668\u7ea7\u522b\u5bf9\u8c61\u65f6\u51fa\u9519: {}", e.getMessage());
        }
        try {
            for (ServerLevel level : this.server.getAllLevels()) {
                ServerChunkCache chunkCache = level.getChunkSource();
                chunkCache.save(false);
                ++cleaned;
            }
        } catch (Exception e) {
            MemoryCleanerMod.LOGGER.debug("\u6e05\u7406\u533a\u5757\u7f13\u5b58\u65f6\u51fa\u9519: {}", e.getMessage());
        }
        if (cleaned > 0) {
            MemoryCleanerMod.LOGGER.info("\ud83d\udda5\ufe0f [\u670d\u52a1\u5668\u6e05\u7406] \u5df2\u6267\u884c\u670d\u52a1\u5668\u7ea7\u522b\u5bf9\u8c61\u6e05\u7406");
        }
        return false;
    }

    public void forceClean() {
        MemoryCleanerMod.LOGGER.info("\u5f3a\u5236\u6267\u884c\u5185\u5b58\u6e05\u7406... \ud83d\ude80");
        if (this.reflectionInitialized) {
            Iterator<Map.Entry<ChunkPos, Long>> iterator = this.chunkLastAccessTime.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ChunkPos, Long> entry = iterator.next();
                this.tryUnloadChunk(entry.getKey());
                iterator.remove();
            }
        }
        this.chunkLastAccessTime.clear();
        this.pendingUnloadChunks.clear();
        System.gc();
        long memoryAfter = this.getCurrentMemoryUsage();
        MemoryCleanerMod.LOGGER.info("\u5f3a\u5236\u6e05\u7406\u5b8c\u6210\uff01\u5f53\u524d\u5185\u5b58\u4f7f\u7528: {}MB", memoryAfter);
    }

    public long getCurrentMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / 1024L / 1024L;
    }

    public long getMaxMemory() {
        return Runtime.getRuntime().maxMemory() / 1024L / 1024L;
    }

    public String getStatus() {
        long current = this.getCurrentMemoryUsage();
        long max = this.getMaxMemory();
        int percent = (int) (current * 100L / max);
        return String.format("\u5185\u5b58\u4f7f\u7528: %dMB / %dMB (%d%%) | \u7d2f\u8ba1\u6e05\u7406\u533a\u5757: %d | \u7d2f\u8ba1\u6e05\u7406\u73a9\u5bb6: %d | \u8ffd\u8e2a\u533a\u5757: %d", current, max, percent, this.totalChunksCleaned.get(), this.totalPlayersCleaned.get(), this.chunkLastAccessTime.size());
    }

    public void onPlayerLoggedOut(ServerPlayer player) {
        if (player == null || this.server == null) {
            return;
        }
        UUID playerUUID = player.getUUID();
        String playerName = player.getName().getString();
        MemoryCleanerMod.LOGGER.info("\ud83d\udc4b [\u73a9\u5bb6\u4e0b\u7ebf] \u73a9\u5bb6 {} ({}) \u6b63\u5728\u65ad\u5f00\u8fde\u63a5...", playerName, playerUUID);
        this.cleanupPlayerChunks(player);
        this.cleanupPlayerReferences(player);
        System.gc();
        MemoryCleanerMod.LOGGER.info("\u2705 [\u73a9\u5bb6\u4e0b\u7ebf] \u5df2\u6e05\u7406\u73a9\u5bb6 {} ({}) \u7684\u76f8\u5173\u6570\u636e", playerName, playerUUID);
    }

    private void cleanupPlayerChunks(ServerPlayer player) {
        if (player == null) {
            return;
        }
        try {
            ServerLevel level = player.serverLevel();
            if (level != null) {
                ChunkPos playerPos = player.chunkPosition();
                int cleanupRadius = 32;
                Iterator<Map.Entry<ChunkPos, Long>> iterator = this.chunkLastAccessTime.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<ChunkPos, Long> entry = iterator.next();
                    ChunkPos pos = entry.getKey();
                    int distance = Math.max(Math.abs(pos.x - playerPos.x), Math.abs(pos.z - playerPos.z));
                    if (distance > cleanupRadius) continue;
                    iterator.remove();
                }
                MemoryCleanerMod.LOGGER.debug("\ud83e\uddf9 [\u533a\u5757\u6e05\u7406] \u5df2\u6e05\u7406\u73a9\u5bb6 {} \u9644\u8fd1\u7684\u533a\u5757\u8ffd\u8e2a\u6570\u636e", player.getName().getString());
            }
        } catch (Exception e) {
            MemoryCleanerMod.LOGGER.debug("\u6e05\u7406\u73a9\u5bb6\u533a\u5757\u65f6\u51fa\u9519: {}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void cleanupPlayerReferences(ServerPlayer player) {
        if (player == null) {
            return;
        }
        try {
            UUID playerUUID = player.getUUID();
            int cleaned = 0;
            for (ServerLevel level : this.server.getAllLevels()) {
                for (Entity entity : level.getAllEntities()) {
                    if (!(entity instanceof ServerPlayer otherPlayer)) continue;
                    UUID otherUUID = otherPlayer.getUUID();
                    if (!otherUUID.equals(playerUUID) || otherPlayer == player) continue;
                    MemoryCleanerMod.LOGGER.warn("\ud83d\udea8 [\u73a9\u5bb6\u6e05\u7406] \u53d1\u73b0\u91cd\u590d\u7684\u73a9\u5bb6\u5f15\u7528: {} ({})", otherPlayer.getName().getString(), otherUUID);
                    ++cleaned;
                }
            }
            try {
                PlayerList playerList = this.server.getPlayerList();
                Field playersField = playerList.getClass().getDeclaredField("players");
                playersField.setAccessible(true);
                List<ServerPlayer> playersList = (List<ServerPlayer>) playersField.get(playerList);
                Iterator<ServerPlayer> iterator = playersList.iterator();
                while (iterator.hasNext()) {
                    ServerPlayer otherPlayer = iterator.next();
                    if (otherPlayer == null || !otherPlayer.getUUID().equals(playerUUID) || otherPlayer == player) continue;
                    iterator.remove();
                    ++cleaned;
                    MemoryCleanerMod.LOGGER.info("\u2705 [\u73a9\u5bb6\u6e05\u7406] \u5df2\u6e05\u7406\u73a9\u5bb6\u5217\u8868\u4e2d\u7684\u91cd\u590d\u5f15\u7528");
                }
            } catch (Exception e) {
                MemoryCleanerMod.LOGGER.debug("\u6e05\u7406\u73a9\u5bb6\u5217\u8868\u65f6\u51fa\u9519: {}", e.getMessage());
            }
            if (cleaned > 0) {
                this.totalPlayersCleaned.addAndGet(cleaned);
                MemoryCleanerMod.LOGGER.info("\ud83e\uddf9 [\u73a9\u5bb6\u6e05\u7406] \u5df2\u6e05\u7406\u73a9\u5bb6 {} \u7684 {} \u4e2a\u65e0\u6548\u5f15\u7528", player.getName().getString(), cleaned);
            }
        } catch (Exception e) {
            MemoryCleanerMod.LOGGER.debug("\u6e05\u7406\u73a9\u5bb6\u5f15\u7528\u65f6\u51fa\u9519: {}", e.getMessage());
        }
    }
}