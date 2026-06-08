# CozyHub

A comprehensive hub/lobby plugin with spawn management, server navigator, sidebar, tab list, cosmetics, protection, double jump, and player visibility controls.

## Features

- **Spawn System** — Per-world spawn locations with configurable warmup teleportation
- **Server Navigator** — Chest GUI for BungeeCord/Velocity server switching
- **Hotbar** — Configurable hotbar items with multiple actions
- **Sidebar** — Per-player scoreboard with placeholder support
- **Tab List** — Custom header and footer with live placeholders
- **Cosmetics** — Join firework and sound effects
- **Protection** — Toggleable PvP, damage, block, hunger, item, and inventory protection
- **Double Jump** — Configurable velocity, particles, and sound
- **Player Visibility** — Toggle visibility of other players
- **Join/Quit** — Custom messages, join title, first-join logic, starter kit
- **BungeeCord/Velocity** — Server connect via plugin messaging channels

## Installation

1. Build the project: `mvn clean package`
2. Place `cozy-hub-1.0.0-SNAPSHOT.jar` into your server's `plugins/` folder
3. Start or restart the server
4. Edit `plugins/CozyHub/config.yml` to your liking
5. Use `/hub reload` to apply changes without restart

## Package Structure

```
dev.cozy.hub/
├── CozyHub.java                    # Main plugin class
├── command/
│   ├── HubCommand.java             # /hub command
│   └── VisibilityCommand.java      # /visibility command
├── gui/
│   ├── NavigatorGUI.java           # Server selector chest GUI
│   ├── SettingsGUI.java            # Settings GUI (placeholder)
│   └── GUIListener.java            # Inventory click handler
├── listener/
│   ├── PlayerJoinListener.java     # Join events
│   ├── PlayerQuitListener.java     # Quit events
│   ├── ProtectionListener.java     # Hub protection
│   ├── HotbarListener.java         # Hotbar interactions
│   └── MovementListener.java       # Double jump + movement tracking
├── manager/
│   ├── SpawnManager.java           # Spawn locations + warmup
│   ├── HotbarManager.java          # Hotbar items + actions
│   ├── SidebarManager.java         # Scoreboard sidebar
│   ├── TabListManager.java         # Tab list header/footer
│   ├── VisibilityManager.java      # Player visibility toggle
│   └── CosmeticManager.java        # Join firework + sound
└── util/
    └── ItemBuilder.java            # Fluent ItemStack builder
```

## How It Works

### Spawn System

Spawns are stored per-world in `config.yml` under `spawns.<worldName>`. When a player types `/hub`, they are teleported to the spawn of their current world with a configurable warmup countdown displayed as an actionbar message. If the player moves during warmup, the teleport is cancelled.

### Server Navigator

The navigator is a chest GUI that displays server items configured in `config.yml`. Clicking a server sends a BungeeCord `Connect` plugin message. Items, slots, materials, lore, and actions are all configurable.

### Hotbar

The hub hotbar gives players special items in specific slots. Each item has an associated action:

| Action | Description |
|--------|-------------|
| `OPEN_NAVIGATOR` | Opens the server navigator GUI |
| `TOGGLE_VISIBILITY` | Toggles player visibility |
| `DISCONNECT` | Kicks the player |
| `RUN_COMMAND` | Executes a command |
| `OPEN_URL` | Opens a URL (future) |

### Protection

All protection features are individually toggleable in `config.yml`. When enabled, the corresponding event is cancelled. The `hub-fly` option grants players flight on join.

### Double Jump

When enabled, players can double jump by pressing space while in the air. This uses the flight system — `hub-fly` must be enabled for double jump to work. Velocity, particles, and sound are configurable.

### Player Visibility

Players can toggle visibility of all other players using the `/visibility` command or the hotbar item. Hidden players are tracked per-player and applied on join.

### Cosmetics

On join, players can receive a firework effect and play a sound. Both are configurable with colors, type, power, trail, flicker, volume, and pitch.

## Configuration

See [Configuration Reference](Configuration.md) for all config options.

## Commands

See [Commands Reference](Commands.md) for all commands.

## Permissions

See [Permissions Reference](Permissions.md) for all permissions.
