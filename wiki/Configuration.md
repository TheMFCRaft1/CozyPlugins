# Configuration Reference

All configuration is in `plugins/CozyHub/config.yml`. Messages are in `plugins/CozyHub/messages.yml`.

## config.yml

```yaml
# Spawn System
spawn:
  warmup-seconds: 3           # Seconds before teleport (0 = instant)
  teleport-on-join: true      # Teleport players to spawn on join
  reset-inventory-on-join: false  # Clear inventory on teleport

# Join Title
title:
  join-title: "&aWillkommen!"
  join-subtitle: "&7Schön dass du da bist, &f{player}"
  fade-in: 10                 # Title fade-in ticks
  stay: 60                    # Title stay ticks
  fade-out: 20                # Title fade-out ticks
  show-actionbar: false       # Show actionbar after title
  actionbar-message: "&eWillkommen auf dem Server!"

# Starter Kit (first join only)
starter-kit:
  items:
    - COMPASS
    - BREAD

# Hub Protection
protection:
  disable-pvp: true
  disable-damage: true
  disable-block-break: true
  disable-block-place: true
  disable-hunger: true
  hub-fly: true               # Enable flight in hub
  disable-item-drop: true
  disable-item-pickup: true
  disable-inventory-click: false

# Double Jump
double-jump:
  enabled: true
  permission: "cozyhub.doublejump"
  velocity-x: 1.0             # Horizontal velocity multiplier
  velocity-y: 0.8             # Vertical velocity
  particle: CLOUD              # Particle effect
  sound: ENTITY_BAT_TAKEOFF    # Sound effect
  cooldown-ticks: 0            # Cooldown between jumps

# Hotbar Items
hotbar:
  items:
    0:
      slot: 0
      material: COMPASS
      name: "&eServer wählen"
      lore: ["&7Rechtsklick zum Öffnen"]
      action: OPEN_NAVIGATOR
      glow: true
    1:
      slot: 4
      material: ENDER_EYE
      name: "&bSpieler ausblenden"
      lore: ["&7Rechtsklick zum Umschalten"]
      action: TOGGLE_VISIBILITY
      glow: false
    2:
      slot: 8
      material: BED
      name: "&cDisconnect"
      lore: ["&7Rechtsklick zum Verlassen"]
      action: DISCONNECT
      glow: false

# Sidebar
sidebar:
  enabled: true
  title: "&6&lCozy Network"
  update-interval: 20          # Ticks between updates
  lines:
    - "&7"
    - "&fSpieler: &a{online}"
    - "&fWelt: &a{world}"
    - "&7"
    - "&fDein Rang: &a{rank}"
    - "&7"
    - "&ewww.example.net"

# Tab List
tablist:
  enabled: true
  update-interval: 40
  header:
    - "&6&lCozy Network"
    - "&7"
  footer:
    - "&7"
    - "&fOnline: &a{online}&7/&f{max}"
    - "&ewww.example.net"

# Player Visibility
visibility:
  default-hidden: false        # Default visibility state
  hotbar-slot: 8               # Slot for visibility toggle item
  item-show:
    material: ENDER_EYE
    name: "&aSpieler einblenden"
  item-hide:
    material: ENDER_EYE
    name: "&cSpieler ausblenden"

# Cosmetics
cosmetics:
  join-firework:
    enabled: true
    power: 1
    colors: [RED, AQUA]
    fade-colors: [WHITE]
    type: BALL_LARGE
    trail: true
    flicker: false
  join-sound:
    enabled: true
    sound: ENTITY_PLAYER_LEVELUP
    volume: 1.0
    pitch: 1.0

# Navigator GUI
navigator:
  title: "&6Server Navigator"
  size: 27                     # Inventory size (9, 18, 27, 36, 45, 54)
  fill-material: GRAY_STAINED_GLASS_PANE

# Settings GUI (placeholder for future extension)
settings-gui:
  title: "&6Settings"
  size: 27
  fill-material: GRAY_STAINED_GLASS_PANE
  placeholders: {}

# BungeeCord Servers
servers:
  survival:
    slot: 11
    name: "&aSurvival"
    material: GRASS_BLOCK
    lore: ["&7Klicke um zu verbinden"]
    server: "survival"
    action: SERVER_CONNECT
  skyblock:
    slot: 13
    name: "&9Skyblock"
    material: END_STONE
    lore: ["&7Klicke um zu verbinden"]
    server: "skyblock"
    action: SERVER_CONNECT
  creative:
    slot: 15
    name: "&bCreative"
    material: DIAMOND_BLOCK
    lore: ["&7Klicke um zu verbinden"]
    server: "creative"
    action: SERVER_CONNECT
```

## messages.yml

```yaml
prefix: "&8[&aCo&2zy&8] &r"

join-message: "&7+ &a{player}"
quit-message: "&7- &c{player}"
first-join-message: "&7&l* &e&l{player} &7hat den Server zum ersten Mal betreten!"

visibility-shown: "&aSpieler werden jetzt angezeigt."
visibility-hidden: "&cSpieler werden jetzt ausgeblendet."

command:
  hub-usage: "&cVerwendung: /hub [setspawn|reload]"
  spawn-not-set: "&cDer Spawn wurde noch nicht gesetzt!"
  teleported: "&aDu wurdest zum Hub-Spawn teleportiert!"
  spawn-set: "&aDer Hub-Spawn wurde gesetzt!"
  reloaded: "&aDie Konfiguration wurde neu geladen!"
  no-permission: "&cDazu hast du keine Berechtigung!"
```

## Placeholders

The following placeholders are available in sidebar, tab list, and messages:

| Placeholder | Description |
|-------------|-------------|
| `{player}` | Player's display name |
| `{online}` | Number of online players |
| `{max}` | Maximum player slots |
| `{world}` | Current world name |
| `{rank}` | Player's rank (default: "Spieler") |

Color codes use `&` prefix (e.g., `&a` for green, `&6` for gold).
