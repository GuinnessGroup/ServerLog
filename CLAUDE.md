# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ServerLog is a **Minecraft Paper plugin** (version 26.1.2) that automatically logs detailed server activities including block placements/breaks, player chat, commands, item interactions, and server metrics. Supports both file-based logging and MariaDB database logging, configurable via `config.yml`.

**Version:** 1.0.1  
**Target Java:** 25  
**Build System:** Gradle + Shadow  
**Paper API:** 26.1.2.build.+

## Build & Run Commands

```bash
# Build the plugin jar (shadow jar with MariaDB driver included)
./gradlew build

# Run an embedded Paper server with the plugin loaded (development)
./gradlew runServer

# Clean build artifacts
./gradlew clean
```

The built jar file will be in `build/libs/`.

## Project Architecture

### Event-Driven Logging System

All event listeners extend `BaseListener` and are injected with a shared `ServerLogUtils` instance and optional `EventRepository`. Each listener writes log entries to files and/or the database depending on `config.yml`.

- **BaseListener** — abstract base class; holds `plugin`, `logUtils`, `repository`; provides `log()`, `template()`, `logDb()`, `logDbDirect()`, `esc()` helpers
- **BlockListener** — logs block placement and destruction events
- **PlayerListener** — logs player join/leave, death, gamemode changes, and teleportation
- **ChatListener** — logs chat messages (`AsyncChatEvent`; DB written directly on async thread, file delegated to main thread via `runTask`)
- **CommandListener** — logs command execution with player context and location
- **ItemListener** — logs item actions (drop, spawn, pickup) with inventory details
- **BucketListener** — logs bucket interactions (water, lava, entity capture)
- **ServerInfoTask** — periodically logs server metrics (chunks, entities, player counts per world); **not** a Bukkit Listener

### Logging Infrastructure

**ServerLogUtils** — single shared instance created in `ServerLog.onEnable()`, injected into all listeners:
- Lang config (`FileConfiguration`) and `SimpleDateFormat` instances cached at construction — **no per-event disk reads**
- `isFileEnabled()` / `isDatabaseEnabled()` — checks `output` list in config
- `template(Message)` — returns the lang YAML string for a message key
- `appendString(String path, String line)` — writes to dated `.txt` files with UTF-8 encoding; no-ops if file logging is disabled
- `toPlainText(Component)` — Adventure → plain string serialization

**DatabaseManager** — HikariCP connection pool; creates tables on startup:
- `{prefix}events` — all player/block/item/bucket/chat/command events
- `{prefix}server_metrics` — periodic server metrics (chunks, entities, players)

**EventRepository** — thin DAO layer over `DatabaseManager`:
- `insertEvent(eventType, playerUuid, playerName, world, x, y, z, dataJson)` — inserts into `{prefix}events`
- `insertMetric(metricType, world, count)` — inserts into `{prefix}server_metrics`

**Message enum** defines all 19 log message keys with their file paths and lang keys:

| Category | Messages |
|---|---|
| Player | PLAYER_JOIN, PLAYER_QUIT, PLAYER_KICK, PLAYER_DEATH, PLAYER_TELEPORT, PLAYER_GAMEMODE |
| Bucket | BUCKET_EMPTY, BUCKET_FILL, BUCKET_ENTITY |
| Item | ITEM_EGG_SPAWN, ITEM_DROP_ITEM, ITEM_PICKUP_ITEM |
| Block | BLOCK_BREAK, BLOCK_PLACE |
| Etc | CHAT, COMMAND, CHUNK_LOAD, ENTITY_COUNT, PLAYER_COUNT |

### DB Schema

**`{prefix}events`**
```sql
id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY
event_type   VARCHAR(32)   -- e.g. "BLOCK_PLACE", "CHAT"
player_uuid  CHAR(36)      -- UUID string
player_name  VARCHAR(16)
world        VARCHAR(64)
x, y, z      INT
timestamp    DATETIME(3)   DEFAULT CURRENT_TIMESTAMP(3)
data         JSON          -- event-specific fields
INDEX (event_type), INDEX (player_uuid), INDEX (timestamp)
```

**`{prefix}server_metrics`**
```sql
id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY
metric_type  VARCHAR(32)   -- "CHUNK_LOAD", "ENTITY_COUNT", "PLAYER_COUNT"
world        VARCHAR(64)
count        INT
timestamp    DATETIME(3)   DEFAULT CURRENT_TIMESTAMP(3)
INDEX (timestamp)
```

### Plugin Initialization (ServerLog.java)

1. Loads default config from `config.yml`
2. Creates single `ServerLogUtils` instance
3. If `database` is in `output` list: initializes `DatabaseManager` + `EventRepository`; on failure, disables DB silently
4. Registers all event listeners (injecting shared `logUtils` and `eventRepository`)
5. Schedules `ServerInfoTask` via Bukkit sync repeating task (`serverInfo.interval` minutes)
6. `onDisable()` closes the HikariCP pool if open

## Configuration Structure

**config.yml** - Main plugin configuration:
- `lang` — language selection (default: `en`)
- `output` — list of outputs; valid values: `file`, `database` (default: `[file]`)
- `database.host/port/name/username/password` — MariaDB connection settings
- `database.table-prefix` — table name prefix (default: `sl_`)
- `database.pool-size` — HikariCP max connections (default: `5`)
- `serverInfo.interval` — minutes between server metric logs (default: `5`)
- `serverStop.interval` — minutes for server stop interval (default: `10`)

**Language files** (`src/main/resources/lang/`):
- `en.yml` - English messages
- `ko.yml` - Korean messages

Language files contain:
- Time format definitions: `time.file`, `time.full`, `time.normal`
- Log message templates with replaceable placeholders like `[player]`, `[world]`, `[x]`, `[y]`, `[z]`, etc.

## Key Design Notes

- **Single `ServerLogUtils` instance** — created once in `ServerLog`, injected into all listeners; no per-listener instantiation
- **Lang config cached** — YAML loaded once at startup, not on every event
- **Thread-safe file writes** — `SimpleDateFormat` fields used only on main thread; `ChatListener` delegates async event to main thread via `runTask` before accessing them
- **DB writes are async** — `logDb()` calls `runTaskAsynchronously`; `logDbDirect()` for already-async contexts (e.g. `AsyncChatEvent`)
- **DB is optional** — `eventRepository` is `null` when DB disabled; all `logDb*()` calls are no-ops when `repository == null`
- **UTF-8 enforced** — `OutputStreamWriter` with `StandardCharsets.UTF_8` (not `FileWriter`)
- **Language abstraction** — all log messages externalized to YAML for easy localization
- **Multi-world support** — `ServerInfoTask` counts metrics per world
- **`ServerInfoTask` is not a Listener** — periodic task called by scheduler, lives in `tasks/` package
- **MariaDB JDBC driver shaded** — relocated to `kr.guinnessgroup.serverLog.libs.mariadb` to avoid classpath conflicts; HikariCP is `compileOnly` (bundled in Paper)

## File Organization

```txt
src/main/java/kr/guinnessgroup/serverLog/
├── ServerLog.java                    # Main plugin class
├── db/
│   ├── DatabaseManager.java          # HikariCP pool + table auto-creation
│   └── EventRepository.java          # insertEvent() / insertMetric() DAO
├── events/
│   ├── BaseListener.java             # Abstract base for all listeners
│   ├── BlockListener.java
│   ├── BucketListener.java
│   ├── ChatListener.java
│   ├── CommandListener.java
│   ├── ItemListener.java
│   └── PlayerListener.java
├── tasks/
│   └── ServerInfoTask.java           # Scheduled server metrics collector
└── utils/
    ├── Message.java                  # Log message enum (path + langKey)
    └── ServerLogUtils.java           # File I/O, lang cache, formatting
```

## Adding New Log Features

When adding a new event type:
1. Create `XListener.java` extending `BaseListener` with constructor `(JavaPlugin plugin, ServerLogUtils logUtils, EventRepository repository)`
2. Define `@EventHandler` methods using `log(Message.X, template(Message.X).replace(...))` for file and `logDb("EVENT_TYPE", uuid, name, world, x, y, z, "{...}")` for DB
3. Add message constants to `Message.java` enum with path and langKey
4. Add log message templates to `en.yml` and `ko.yml` with placeholders
5. Register in `ServerLog.registerEvents()` as `new XListener(this, logUtils, eventRepository)`

## Notes for Future Development

- Paper API version is `26.1.2.build.+` in `build.gradle` and `26.1.2` in `plugin.yml`
- `World.getName()` is noted as future-deprecated in Paper 26.x — prefer `world.key().value()` when it is formally deprecated
- `PlayerCommandPreprocessEvent` is still valid; avoid `getRecipients()` and `setPlayer()` (deprecated methods)
- Component serialization uses `PlainTextComponentSerializer` for display names and chat text
- Web viewer for DB logs is planned — query `{prefix}events` filtered by `event_type`, `player_uuid`, or `timestamp` range; use browser-side UUID → player name API with caching
