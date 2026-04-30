# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ServerLog is a **Minecraft Paper plugin** (version 1.21.11) that automatically logs detailed server activities including block placements/breaks, player chat, commands, item interactions, and server metrics. It provides structured, categorized logging with configurable language support.

**Version:** 1.0.1  
**Target Java:** 21  
**Build System:** Gradle

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

The plugin registers event listeners to Bukkit's plugin manager, which capture and log specific server activities:

- **BlockListener** - logs block placement and destruction events
- **PlayerListener** - logs player join/leave, death, gamemode changes, and teleportation
- **ChatListener** - logs chat messages with player names
- **CommandListener** - logs command execution with player context and location
- **ItemListener** - logs item actions (drop, spawn, pickup) with inventory details
- **BucketListener** - logs bucket interactions (water, lava)
- **ServerInfoListener** - periodically logs server metrics (loaded chunks, entities, player counts per world)

### Logging Infrastructure

**ServerLogUtils** handles all file I/O:
- Creates timestamped log files in `plugins/ServerLog/logs/` subdirectories
- Uses format templates from language config files to format log entries
- Supports dynamic time placeholders: `[time.normal]` and `[time.full]`
- File names are generated using `time.file` format from language YAML

**Message enum** defines all log message keys and their corresponding file paths, centralizing logging configuration.

### Plugin Initialization (ServerLog.java)

1. Loads default config from `config.yml`
2. Registers all event listeners
3. Schedules periodic server info tasks (configurable interval, default 5 minutes = `serverInfo.interval`)

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

- **No external dependencies** - uses only Paper API (compile-only)
- **Thread-safe file writes** - uses buffered writers with proper resource management
- **Language abstraction** - all log messages are externalized to YAML files, allowing easy localization
- **Multi-world support** - server info listener counts metrics per world
- **Scheduled tasks** - uses Bukkit scheduler for periodic server info logging (sync repeating tasks)

## File Organization

```
src/main/java/kr/guinnessgroup/serverLog/
├── ServerLog.java                    # Main plugin class
├── events/                           # Event listeners (7 listeners)
│   ├── BlockListener.java
│   ├── PlayerListener.java
│   ├── ChatListener.java
│   ├── CommandListener.java
│   ├── ItemListener.java
│   ├── BucketListener.java
│   └── ServerInfoListener.java
└── utils/
    ├── ServerLogUtils.java           # File I/O and formatting
    └── Message.java                  # Log message constants/enum
```

## Adding New Log Features

When adding a new event type:
1. Create a new `*Listener.java` class implementing `Listener`
2. Define `@EventHandler` methods for each event type
3. Add message keys to `Message.java` enum with paths
4. Add log message templates to language YAML files (en.yml, ko.yml) with placeholders
5. Register the listener in `ServerLog.registerEvents()`

## Notes for Future Development

- Paper API version must match `1.21.11` in both `build.gradle` and `plugin.yml`
- All file writes should use UTF-8 encoding (already handled in ServerLogUtils)
- Component serialization uses `PlainTextComponentSerializer` for display names and chat text
