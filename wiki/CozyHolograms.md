# CozyHolograms

A hologram plugin for Minecraft servers using Paper's TextDisplay API with MiniMessage support, animations, and PlaceholderAPI integration.

## Features

- **TextDisplay-Based Holograms** — Uses native Paper TextDisplay entities, no armor stands
- **MiniMessage Support** — Full MiniMessage formatting for colors and styles
- **Multi-Line Holograms** — Automatic vertical spacing (0.28 blocks per line)
- **Animations** — Frame-based text animations with configurable interval
- **Placeholder Support** — Built-in `{online}`, `{max}`, `{world}`, `{player}` + PlaceholderAPI
- **Persistence** — Holograms saved to `holograms.yml`, survive restarts
- **Developer API** — Public API for other plugins to create/manage holograms
- **Full Command System** — Create, edit, delete, list, teleport with tab completion

## Installation

1. Build: `mvn clean package`
2. Place `cozy-holograms-1.0.0-SNAPSHOT.jar` into `plugins/`
3. Optionally install PlaceholderAPI for extended placeholder support
4. Start the server

## Quick Start

```
/holo create myhologram        → Creates at your location
/holo addline myhologram &6Hello World    → Adds a line
/holo teleport myhologram      → Teleport to it
```

## Requirements

- Java 21+
- Paper 1.21.4+
- Optional: PlaceholderAPI
