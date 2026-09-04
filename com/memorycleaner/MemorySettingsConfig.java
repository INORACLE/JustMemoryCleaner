/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraftforge.fml.loading.FMLPaths
 */
package com.memorycleaner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.memorycleaner.MemoryCleanerMod;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import net.minecraftforge.fml.loading.FMLPaths;

public class MemorySettingsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_FILE_NAME = "memorysettings.json";
    private int minimumClient = 2500;
    private int maximumClient = 8500;
    private int minimumServer = 2500;
    private int maximumServer = 8500;
    private boolean disableWarnings = false;
    private int warningTolerance = 30;
    private double memoryThresholdPercent = 80.0;
    private static MemorySettingsConfig instance;

    public static MemorySettingsConfig getInstance() {
        if (instance == null) {
            instance = new MemorySettingsConfig();
            instance.load();
        }
        return instance;
    }

    public void load() {
        File configFile = this.getConfigFile();
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile);){
                JsonObject json = JsonParser.parseReader((Reader)reader).getAsJsonObject();
                if (json.has("minimumClient")) {
                    this.minimumClient = json.getAsJsonObject("minimumClient").get("minimumClient").getAsInt();
                }
                if (json.has("maximumClient")) {
                    this.maximumClient = json.getAsJsonObject("maximumClient").get("maximumClient").getAsInt();
                }
                if (json.has("minimumServer")) {
                    this.minimumServer = json.getAsJsonObject("minimumServer").get("minimumServer").getAsInt();
                }
                if (json.has("maximumServer")) {
                    this.maximumServer = json.getAsJsonObject("maximumServer").get("maximumServer").getAsInt();
                }
                if (json.has("disableWarnings")) {
                    this.disableWarnings = json.getAsJsonObject("disableWarnings").get("disableWarnings").getAsBoolean();
                }
                if (json.has("warningTolerance")) {
                    this.warningTolerance = json.getAsJsonObject("warningTolerance").get("warningTolerance").getAsInt();
                }
                if (json.has("memoryThresholdPercent")) {
                    this.memoryThresholdPercent = json.getAsJsonObject("memoryThresholdPercent").get("memoryThresholdPercent").getAsDouble();
                }
                MemoryCleanerMod.LOGGER.info("\u2705 MemorySettings \u914d\u7f6e\u5df2\u52a0\u8f7d");
            }
            catch (IOException e) {
                MemoryCleanerMod.LOGGER.error("\u274c \u52a0\u8f7d MemorySettings \u914d\u7f6e\u5931\u8d25: {}", (Object)e.getMessage());
                this.createDefaultConfig();
            }
        } else {
            this.createDefaultConfig();
        }
    }

    public void save() {
        File configFile = this.getConfigFile();
        try (FileWriter writer = new FileWriter(configFile);){
            JsonObject json = new JsonObject();
            JsonObject minClientObj = new JsonObject();
            minClientObj.addProperty("desc:", "\u8bbe\u7f6e\u5ba2\u6237\u7aef\u6700\u4f4e\u5185\u5b58\u8b66\u544a\u9608\u503c\uff08MB\uff09\u3002\u9009\u62e9\u4fdd\u6301\u6574\u5408\u5305\u53ef\u8fd0\u884c\u7684\u6700\u4f4e\u53ef\u80fd\u503c\u3002\u9ed8\u8ba4\u503c\uff1a2500\uff0c\u6700\u5c0f\u503c\uff1a2500\uff0c\u6700\u5927\u503c\uff1a25000");
            minClientObj.addProperty("minimumClient", (Number)this.minimumClient);
            json.add("minimumClient", (JsonElement)minClientObj);
            JsonObject maxClientObj = new JsonObject();
            maxClientObj.addProperty("desc:", "\u8bbe\u7f6e\u5ba2\u6237\u7aef\u6700\u9ad8\u5185\u5b58\u8b66\u544a\u9608\u503c\uff08MB\uff09\u3002\u9009\u62e9\u4e00\u4e2a\u5bbd\u88d5\u7684\u6700\u5927\u503c\uff0c\u6bd4\u6240\u9700\u5185\u5b58\u591a\u4e00\u4e9b");
            maxClientObj.addProperty("maximumClient", (Number)this.maximumClient);
            json.add("maximumClient", (JsonElement)maxClientObj);
            JsonObject minServerObj = new JsonObject();
            minServerObj.addProperty("desc:", "\u8bbe\u7f6e\u670d\u52a1\u7aef\u6700\u4f4e\u5185\u5b58\u8b66\u544a\u9608\u503c\uff08MB\uff09\u3002\u9009\u62e9\u4fdd\u6301\u6574\u5408\u5305\u53ef\u8fd0\u884c\u7684\u6700\u4f4e\u53ef\u80fd\u503c");
            minServerObj.addProperty("minimumServer", (Number)this.minimumServer);
            json.add("minimumServer", (JsonElement)minServerObj);
            JsonObject maxServerObj = new JsonObject();
            maxServerObj.addProperty("desc:", "\u8bbe\u7f6e\u670d\u52a1\u7aef\u6700\u9ad8\u5185\u5b58\u8b66\u544a\u9608\u503c\uff08MB\uff09\u3002\u9009\u62e9\u4e00\u4e2a\u5bbd\u88d5\u7684\u6700\u5927\u503c");
            maxServerObj.addProperty("maximumServer", (Number)this.maximumServer);
            json.add("maximumServer", (JsonElement)maxServerObj);
            JsonObject disableWarnObj = new JsonObject();
            disableWarnObj.addProperty("desc:", "\u7981\u7528\u5185\u5b58\u8b66\u544a\uff0c\u9ed8\u8ba4\u503c\uff1afalse");
            disableWarnObj.addProperty("disableWarnings", Boolean.valueOf(this.disableWarnings));
            json.add("disableWarnings", (JsonElement)disableWarnObj);
            JsonObject toleranceObj = new JsonObject();
            toleranceObj.addProperty("desc:", "\u8bbe\u7f6e\u7cfb\u7edf\u5185\u5b58\u4e0e\u63a8\u8350\u503c\u7684\u504f\u5dee\u767e\u5206\u6bd4\uff0c\u8d85\u8fc7\u6b64\u503c\u65f6\u53d1\u51fa\u8b66\u544a\uff0c\u9ed8\u8ba4\u503c\uff1a30\uff0c\u6700\u5927\u503c\uff1a100");
            toleranceObj.addProperty("warningTolerance", (Number)this.warningTolerance);
            json.add("warningTolerance", (JsonElement)toleranceObj);
            JsonObject thresholdObj = new JsonObject();
            thresholdObj.addProperty("desc:", "\u8bbe\u7f6e\u5185\u5b58\u8b66\u544a\u9608\u503c\u767e\u5206\u6bd4\uff08\u57fa\u4e8e\u6700\u5927\u503c\u7684\u767e\u5206\u6bd4\uff09\uff0c\u9ed8\u8ba4\u503c\uff1a80.0\uff0c\u8303\u56f4\uff1a50-95");
            thresholdObj.addProperty("memoryThresholdPercent", (Number)this.memoryThresholdPercent);
            json.add("memoryThresholdPercent", (JsonElement)thresholdObj);
            JsonObject linkObj = new JsonObject();
            linkObj.addProperty("desc:", "\u8bbe\u7f6e\u7528\u4e8e\u5f15\u5bfc\u73a9\u5bb6\u8bbf\u95ee\u5305\u542b\u66f4\u6539\u5185\u5b58\u5206\u914d\u8bf4\u660e\u7684\u7f51\u7ad9\u7684\u94fe\u63a5");
            linkObj.addProperty("howtolink", "https://apexminecrafthosting.com/how-to-allocate-more-ram/");
            json.add("howtolink", (JsonElement)linkObj);
            GSON.toJson((JsonElement)json, (Appendable)writer);
            MemoryCleanerMod.LOGGER.info("\u2705 MemorySettings \u914d\u7f6e\u5df2\u4fdd\u5b58");
        }
        catch (IOException e) {
            MemoryCleanerMod.LOGGER.error("\u274c \u4fdd\u5b58 MemorySettings \u914d\u7f6e\u5931\u8d25: {}", (Object)e.getMessage());
        }
    }

    private void createDefaultConfig() {
        MemoryCleanerMod.LOGGER.info("\ud83d\udcdd \u521b\u5efa\u9ed8\u8ba4 MemorySettings \u914d\u7f6e");
        this.save();
    }

    private File getConfigFile() {
        Path configPath = FMLPaths.CONFIGDIR.get();
        return configPath.resolve(CONFIG_FILE_NAME).toFile();
    }

    public int getMinimumClient() {
        return this.minimumClient;
    }

    public int getMaximumClient() {
        return this.maximumClient;
    }

    public int getMinimumServer() {
        return this.minimumServer;
    }

    public int getMaximumServer() {
        return this.maximumServer;
    }

    public boolean isDisableWarnings() {
        return this.disableWarnings;
    }

    public int getWarningTolerance() {
        return this.warningTolerance;
    }

    public double getMemoryThresholdPercent() {
        return this.memoryThresholdPercent;
    }

    public int getCalculatedClientThreshold() {
        return (int)((double)this.maximumClient * this.memoryThresholdPercent / 100.0);
    }

    public int getCalculatedServerThreshold() {
        return (int)((double)this.maximumServer * this.memoryThresholdPercent / 100.0);
    }

    public boolean setMinimumClient(int value) {
        if (value >= 2500 && value <= 25000 && value < this.maximumClient) {
            this.minimumClient = value;
            return true;
        }
        return false;
    }

    public boolean setMaximumClient(int value) {
        if (value >= 2500 && value <= 25000 && value > this.minimumClient) {
            this.maximumClient = value;
            return true;
        }
        return false;
    }

    public boolean setMinimumServer(int value) {
        if (value >= 2500 && value <= 25000 && value < this.maximumServer) {
            this.minimumServer = value;
            return true;
        }
        return false;
    }

    public boolean setMaximumServer(int value) {
        if (value >= 2500 && value <= 25000 && value > this.minimumServer) {
            this.maximumServer = value;
            return true;
        }
        return false;
    }

    public void setDisableWarnings(boolean value) {
        this.disableWarnings = value;
    }

    public boolean setWarningTolerance(int value) {
        if (value >= 0 && value <= 100) {
            this.warningTolerance = value;
            return true;
        }
        return false;
    }

    public boolean setMemoryThresholdPercent(double value) {
        if (value >= 50.0 && value <= 95.0) {
            this.memoryThresholdPercent = value;
            return true;
        }
        return false;
    }

    public String getStatusInfo() {
        return String.format("\u5ba2\u6237\u7aef: %dMB - %dMB (\u9608\u503c: %dMB, %.1f%%) | \u670d\u52a1\u7aef: %dMB - %dMB (\u9608\u503c: %dMB, %.1f%%) | \u8b66\u544a: %s", this.minimumClient, this.maximumClient, this.getCalculatedClientThreshold(), this.memoryThresholdPercent, this.minimumServer, this.maximumServer, this.getCalculatedServerThreshold(), this.memoryThresholdPercent, this.disableWarnings ? "\u5df2\u7981\u7528" : "\u5df2\u542f\u7528");
    }
}

