package com.memorycleaner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Config {
    public static final List<ConfigValue> SERVER_VALUES = new ArrayList<>();
    public static final List<ConfigValue> CLIENT_VALUES = new ArrayList<>();

    // server config
    public static final BooleanValue AUTO_CLEAN_ENABLED = server("autoCleanEnabled", true);
    public static final IntValue AUTO_CLEAN_INTERVAL = server("autoCleanInterval", 10);
    public static final IntValue MEMORY_THRESHOLD_PERCENT = server("memoryThresholdPercent", 80);
    public static final BooleanValue CLEAN_UNLOADED_CHUNKS = server("cleanUnloadedChunks", true);
    public static final IntValue CHUNK_UNLOAD_THRESHOLD_TICKS = server("chunkUnloadThresholdTicks", 600);
    public static final IntValue MAX_CHUNKS_PER_CLEAN = server("maxChunksPerClean", 1000);
    public static final BooleanValue CLEAN_INVALID_PLAYERS = server("cleanInvalidPlayers", true);
    public static final IntValue PLAYER_CLEAN_INTERVAL = server("playerCleanInterval", 5);
    public static final IntValue CLEAN_BATCH_SIZE = server("cleanBatchSize", 50);
    public static final IntValue CLEAN_DELAY_TICKS = server("cleanDelayTicks", 5);
    public static final BooleanValue AGGRESSIVE_MODE = server("aggressiveMode", false);
    public static final IntValue CLEAN_COOLDOWN_SECONDS = server("cleanCooldownSeconds", 60);
    public static final BooleanValue AUTO_CLEAN_ON_THRESHOLD = server("autoCleanOnThreshold", true);

    // client config
    public static final BooleanValue CLIENT_AUTO_CLEAN = client("clientAutoClean", true);
    public static final IntValue CLIENT_CLEAN_INTERVAL = client("clientCleanInterval", 60);
    public static final IntValue CLIENT_MEMORY_THRESHOLD = client("clientMemoryThreshold", 85);
    public static final BooleanValue CLIENT_SHOW_DEBUG = client("clientShowDebug", false);

    private static final File SERVER_CONFIG_FILE = getConfigFile("memorycleaner-server.json");
    private static final File CLIENT_CONFIG_FILE = getConfigFile("memorycleaner-client.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static {
        loadConfig(SERVER_CONFIG_FILE, SERVER_VALUES);
        loadConfig(CLIENT_CONFIG_FILE, CLIENT_VALUES);
    }

    private static File getConfigFile(String name) {
        return FabricLoader.getInstance().getConfigDir().resolve(name).toFile();
    }

    private static BooleanValue server(String key, boolean defaultValue) {
        BooleanValue value = new BooleanValue(key, defaultValue);
        SERVER_VALUES.add(value);
        return value;
    }

    private static IntValue server(String key, int defaultValue) {
        IntValue value = new IntValue(key, defaultValue);
        SERVER_VALUES.add(value);
        return value;
    }

    private static BooleanValue client(String key, boolean defaultValue) {
        BooleanValue value = new BooleanValue(key, defaultValue);
        CLIENT_VALUES.add(value);
        return value;
    }

    private static IntValue client(String key, int defaultValue) {
        IntValue value = new IntValue(key, defaultValue);
        CLIENT_VALUES.add(value);
        return value;
    }

    public static void reload() {
        loadConfig(SERVER_CONFIG_FILE, SERVER_VALUES);
        loadConfig(CLIENT_CONFIG_FILE, CLIENT_VALUES);
        MemoryCleanerMod.LOGGER.info("\u914d\u7f6e\u5df2\u91cd\u65b0\u52a0\u8f7d \u2713");
    }

    private static void saveFor(ConfigValue value) {
        if (SERVER_VALUES.contains(value)) {
            saveConfig(SERVER_CONFIG_FILE, SERVER_VALUES);
        }
        if (CLIENT_VALUES.contains(value)) {
            saveConfig(CLIENT_CONFIG_FILE, CLIENT_VALUES);
        }
    }

    private static void loadConfig(File file, List<ConfigValue> values) {
        if (!file.exists()) {
            saveConfig(file, values);
            MemoryCleanerMod.LOGGER.info("\ud83d\udcdd \u5df2\u521b\u5efa\u9ed8\u8ba4\u914d\u7f6e\u6587\u4ef6: {}", file.getName());
            return;
        }
        try (FileReader reader = new FileReader(file)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            for (ConfigValue value : values) {
                if (json.has(value.key)) {
                    value.loadFrom(json);
                }
            }
            MemoryCleanerMod.LOGGER.info("\u2705 \u914d\u7f6e\u5df2\u52a0\u8f7d: {}", file.getName());
        } catch (Exception e) {
            MemoryCleanerMod.LOGGER.warn("\u274c \u52a0\u8f7d\u914d\u7f6e\u5931\u8d25: {} - {}", file.getName(), e.getMessage());
        }
    }

    private static void saveConfig(File file, List<ConfigValue> values) {
        JsonObject json = new JsonObject();
        for (ConfigValue value : values) {
            json.add(value.key, value.toJson());
        }
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(json, writer);
        } catch (IOException e) {
            MemoryCleanerMod.LOGGER.warn("\u274c \u4fdd\u5b58\u9144\u7f6e\u5931\u8d25: {} - {}", file.getName(), e.getMessage());
        }
    }

    public abstract static class ConfigValue {
        protected final String key;
        protected final JsonElement defaultValue;

        ConfigValue(String key, JsonElement defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public abstract JsonElement toJson();

        protected abstract void loadFrom(JsonObject json);
    }

    public static class BooleanValue extends ConfigValue {
        private boolean value;

        BooleanValue(String key, boolean defaultValue) {
            super(key, GSON.toJsonTree(defaultValue));
            this.value = defaultValue;
        }

        public boolean get() {
            return value;
        }

        public void set(boolean value) {
            this.value = value;
            saveFor(this);
        }

        @Override
        public JsonElement toJson() {
            return GSON.toJsonTree(value);
        }

        @Override
        protected void loadFrom(JsonObject json) {
            this.value = json.get(key).getAsBoolean();
        }
    }

    public static class IntValue extends ConfigValue {
        private int value;

        IntValue(String key, int defaultValue) {
            super(key, GSON.toJsonTree(defaultValue));
            this.value = defaultValue;
        }

        public int get() {
            return value;
        }

        public void set(int value) {
            this.value = value;
            saveFor(this);
        }

        @Override
        public JsonElement toJson() {
            return GSON.toJsonTree(value);
        }

        @Override
        protected void loadFrom(JsonObject json) {
            this.value = json.get(key).getAsInt();
        }
    }
}