# Just Memory Cleaner

一个轻量级的 Minecraft 内存清理模组，帮助缓解内存占用问题，提升服务器和客户端运行效率。

![Build](https://github.com/INORACLE/JustMemoryCleaner/actions/workflows/build.yml/badge.svg)
![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1%20%7C%201.21.1-blue)
![Platform](https://img.shields.io/badge/Platform-Fabric%20%7C%20NeoForge-orange)
![License](https://img.shields.io/badge/License-MIT-green)

## 特性

- **服务端区块卸载**：自动追踪并清理长时间未使用的区块，释放服务端内存
- **玩家对象清理**：清理无效/重复的玩家引用，避免内存泄漏
- **自动内存清理**：内存使用率达到阈值时自动触发清理，支持冷却时间控制
- **客户端清理**：在客户端本地执行内存清理，不影响服务器
- **渐进式清理**：分批执行清理，避免卡顿，并支持「激进模式」
- **可视化命令**：内置 `/mc` 与 `/memorycleaner` 命令，可查看状态和调整设置
- **JSON 配置**：所有设置持久化到 `memorycleaner-server.json` 与 `memorycleaner-client.json`

## 版本支持

| 分支             | 平台      | Minecraft | Java | 加载器                                      |
| ---------------- | --------- | --------- | ---- | ------------------------------------------- |
| `main`           | Fabric    | 1.20.1    | 17   | Fabric Loader >= 0.19.3 + Fabric API        |
| `fabric-1.21.1`  | Fabric    | 1.21.1    | 21   | Fabric Loader >= 0.19.3 + Fabric API        |
| `neoforge-1.21.1`| NeoForge  | 1.21.1    | 21   | NeoForge 21.1.249                           |

对应版本的 jar 由 GitHub Actions 自动构建，可在 [Actions 页面](https://github.com/INORACLE/JustMemoryCleaner/actions) 的 Artifacts 中下载：
- `memorycleaner-1.20.1` (Fabric 1.20.1)
- `memorycleaner-fabric-1.21.1` (Fabric 1.21.1)
- `memorycleaner-neoforge-1.21.1` (NeoForge 1.21.1)

## 安装

1. 下载对应平台的 jar 文件
2. 放到 Minecraft 的 `mods` 目录
3. 启动游戏 / 服务器即可，无需额外配置

## 构建

克隆仓库后，切换到对应版本分支构建：

```bash
git clone https://github.com/INORACLE/JustMemoryCleaner.git
cd JustMemoryCleaner

# Fabric 1.20.1
./gradlew build

# 其他版本
git checkout fabric-1.21.1   # 或 neoforge-1.21.1
./gradlew build
```

构建产物位于 `build/libs/`。Fabric 版本需要 Java 17/21，NeoForge 版本需要 Java 21。

## 命令

### 客户端命令（所有玩家可用）

```
/mc clean           手动清理客户端内存
/mc status          查看客户端内存使用情况
/mc setting         查看客户端设置
/mc setting threshold <50-95>        设置内存阈值（%）
/mc setting interval <10-300>        设置清理间隔（秒）
/mc setting autoclean <true/false>   开关自动清理
/mc setting debug <true/false>       开关调试日志
/mc help            查看帮助
```

### 服务端命令（需要管理员权限）

```
/mc server clean               手动清理服务端内存
/mc server status              查看服务端内存与区块状态
/mc server setting             查看服务端设置
/mc server setting threshold <50-95>       设置内存阈值（%）
/mc server setting interval <1-60>         设置自动清理间隔（分钟）
/mc server setting autoclean <true/false>  开关自动清理
/mc server setting unloadchunks <true/false>  开关区块卸载
/mc server setting cleanplayers <true/false>  开关玩家对象清理
```

`/memorycleaner` 为 `/mc` 的等价别名。

## 配置

配置文件生成在 Fabric 的 `config/` 目录（服务端为 `config/memorycleaner-server.json`）。

### 服务端配置

| 配置项 | 默认值 | 说明 |
| --- | --- | --- |
| `autoCleanEnabled` | `true` | 是否启用自动清理 |
| `autoCleanInterval` | `10` | 自动清理间隔（分钟） |
| `memoryThresholdPercent` | `80` | 触发自动清理的内存阈值（%） |
| `autoCleanOnThreshold` | `true` | 内存达到阈值时才清理 |
| `cleanCooldownSeconds` | `60` | 清理冷却时间（秒） |
| `cleanUnloadedChunks` | `true` | 是否定期卸载未使用区块 |
| `chunkUnloadThresholdTicks` | `600` | 区块闲置多少 tick 后视为可卸载 |
| `maxChunksPerClean` | `1000` | 每次清理的最大区块数 |
| `cleanInvalidPlayers` | `true` | 是否清理无效玩家引用 |
| `playerCleanInterval` | `5` | 玩家清理间隔（分钟） |
| `cleanBatchSize` | `50` | 每批清理数量 |
| `cleanDelayTicks` | `5` | 每批之间的间隔 tick |
| `aggressiveMode` | `false` | 激进模式（加大批量、加快节奏） |

### 客户端配置（`config/memorycleaner-client.json`）

| 配置项 | 默认值 | 说明 |
| --- | --- | --- |
| `clientAutoClean` | `true` | 客户端自动清理 |
| `clientCleanInterval` | `60` | 清理间隔（秒） |
| `clientMemoryThreshold` | `85` | 内存阈值（%） |
| `clientShowDebug` | `false` | 调试日志 |

## 项目结构

```
src/main/java/com/memorycleaner   服务端 / 公共逻辑
  ├── MemoryCleanerMod.java       模组主入口
  ├── MemoryCleaner.java          服务端内存清理核心
  ├── ModCommands.java            服务端命令
  ├── Config.java                 配置系统
src/client/java/com/memorycleaner/client   客户端逻辑
  ├── ClientInitializer.java      客户端入口
  ├── ClientMemoryCleaner.java    客户端清理器
  ├── ClientCommandHandler.java   客户端命令
```

## 许可证

本项目采用 [MIT License](LICENSE)。