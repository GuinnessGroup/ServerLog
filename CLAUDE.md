# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ServerLog is a **Minecraft Paper plugin** (version 26.1.2) that automatically logs detailed server activities including block placements/breaks, player chat, commands, item interactions, and server metrics. It provides structured, categorized logging with configurable language support.

**Version:** 1.0.1  
**Target Java:** 21  
**Build System:** Gradle  
**Paper API:** 26.1.2.build.+

## Build & Run Commands

```bash
# Build the plugin jar
./gradlew build

# Run an embedded Paper server with the plugin loaded (development)
./gradlew runServer

# Clean build artifacts
./gradlew clean
```

The built jar file will be in `build/libs/`.

## Project Architecture

### Event-Driven Logging System

All event listeners extend `BaseListener` and are injected with a shared `ServerLogUtils` instance. Each listener captures events and writes structured log entries to both timestamped files and (future) database.

- **BaseListener** - abstract base class; holds `logUtils`, provides `log(Message, String)` and `template(Message)` helpers
- **BlockListener** - logs block placement and destruction events
- **PlayerListener** - logs player join/leave, death, gamemode changes, and teleportation
- **ChatListener** - logs chat messages (`AsyncChatEvent` delegated to main thread via `runTask`)
- **CommandListener** - logs command execution with player context and location
- **ItemListener** - logs item actions (drop, spawn, pickup) with inventory details
- **BucketListener** - logs bucket interactions (water, lava, entity capture)
- **ServerInfoTask** - periodically logs server metrics (chunks, entities, player counts per world); **not** a Bukkit Listener

### Logging Infrastructure

**ServerLogUtils** — single shared instance created in `ServerLog.onEnable()`, injected into all listeners:
- Lang config (`FileConfiguration`) and `SimpleDateFormat` instances cached at construction — **no per-event disk reads**
- `template(Message)` — returns the lang YAML string for a message key
- `appendString(String path, String line)` — writes to dated `.txt` files with UTF-8 encoding
- `toPlainText(Component)` — Adventure → plain string serialization

**Message enum** defines all 19 log message keys with their file paths and lang keys:

| Category | Messages |
|---|---|
| Player | PLAYER_JOIN, PLAYER_QUIT, PLAYER_KICK, PLAYER_DEATH, PLAYER_TELEPORT, PLAYER_GAMEMODE |
| Bucket | BUCKET_EMPTY, BUCKET_FILL, BUCKET_ENTITY |
| Item | ITEM_EGG_SPAWN, ITEM_DROP_ITEM, ITEM_PICKUP_ITEM |
| Block | BLOCK_BREAK, BLOCK_PLACE |
| Etc | CHAT, COMMAND, CHUNK_LOAD, ENTITY_COUNT, PLAYER_COUNT |

### Plugin Initialization (ServerLog.java)

1. Loads default config from `config.yml`
2. Creates single `ServerLogUtils` instance
3. Registers all event listeners (injecting shared `logUtils`)
4. Schedules `ServerInfoTask` via Bukkit sync repeating task (`serverInfo.interval` minutes)

## Configuration Structure

**config.yml** - Main plugin configuration:
- `lang` - language selection (default: "en")
- `serverInfo.interval` - minutes between server metric logs (default: 5)
- `serverStop.interval` - minutes for server stop interval (default: 10)

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
- **UTF-8 enforced** — `OutputStreamWriter` with `StandardCharsets.UTF_8` (not `FileWriter`)
- **Language abstraction** — all log messages externalized to YAML for easy localization
- **Multi-world support** — `ServerInfoTask` counts metrics per world
- **`ServerInfoTask` is not a Listener** — periodic task called by scheduler, lives in `tasks/` package

## File Organization

```txt
src/main/java/kr/guinnessgroup/serverLog/
├── ServerLog.java                    # Main plugin class
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
1. Create `XListener.java` extending `BaseListener` with constructor `(ServerLogUtils logUtils)`
2. Define `@EventHandler` methods using `log(Message.X, template(Message.X).replace(...))` pattern
3. Add message constants to `Message.java` enum with path and langKey
4. Add log message templates to `en.yml` and `ko.yml` with placeholders
5. Register in `ServerLog.registerEvents()` as `new XListener(logUtils)`

## Notes for Future Development

- Paper API version is `26.1.2.build.+` in `build.gradle` and `26.1.2` in `plugin.yml`
- `World.getName()` is noted as future-deprecated in Paper 26.x — prefer `world.key().value()` when it is formally deprecated
- `PlayerCommandPreprocessEvent` is still valid; avoid `getRecipients()` and `setPlayer()` (deprecated methods)
- Component serialization uses `PlainTextComponentSerializer` for display names and chat text
- MariaDB integration is planned — see issue tracker for schema design decisions
