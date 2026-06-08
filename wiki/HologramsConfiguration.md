# CozyHolograms Configuration

All configuration is in `plugins/CozyHolograms/config.yml`.

## config.yml

```yaml
settings:
  update-interval: 20           # Ticks between placeholder updates (20 = 1 second)
  view-distance: 48             # View distance for hologram updates
```

## messages.yml

```yaml
prefix: "&8[&aCo&2zy&8] &r"

command:
  holo-usage: "&cUsage: /holo <create|delete|edit|addline|removeline|list|teleport|reload|info>"
  holo-create-usage: "&cUsage: /holo create <name>"
  holo-delete-usage: "&cUsage: /holo delete <name>"
  holo-edit-usage: "&cUsage: /holo edit <name> <line> <text>"
  holo-addline-usage: "&cUsage: /holo addline <name> <text>"
  holo-removeline-usage: "&cUsage: /holo removeline <name> <line>"
  holo-teleport-usage: "&cUsage: /holo teleport <name>"
  holo-info-usage: "&cUsage: /holo info <name>"
  holo-already-exists: "&cA hologram with that name already exists!"
  holo-not-found: "&cHologram not found!"
  holo-created: "&aHologram created successfully!"
  holo-deleted: "&aHologram deleted successfully!"
  holo-edited: "&aHologram line edited successfully!"
  holo-line-added: "&aLine added to hologram!"
  holo-line-removed: "&aLine removed from hologram!"
  holo-teleported: "&aTeleported to hologram!"
  holo-reloaded: "&aAll holograms reloaded!"
  holo-invalid-line: "&cInvalid line number!"
  holo-list-header: "&6=== Holograms ==="
  holo-list-empty: "&7No holograms found."
  no-permission: "&cYou don't have permission to do that!"
```

## holograms.yml

Holograms are automatically stored in `holograms.yml`:

```yaml
holograms:
  welcome:
    world: world
    x: 0.5
    y: 65.0
    z: 0.5
    animated: false
    animation-interval: 20
    lines:
      - "&6&lWelcome!"
      - "&7Players online: &a{online}"
      - "&7World: &a{world}"
  animated-banner:
    world: world
    x: 10.0
    y: 70.0
    z: 10.0
    animated: true
    animation-interval: 10
    lines:
      - "&6Static first line"
      - frames:
          - "&aFrame 1"
          - "&bFrame 2"
          - "&cFrame 3"
        interval: 10
```

## Placeholders

### Built-in Placeholders

| Placeholder | Description |
|-------------|-------------|
| `{online}` | Number of online players |
| `{max}` | Maximum player slots |
| `{world}` | Player's current world |
| `{player}` | Player's name |

### PlaceholderAPI

If PlaceholderAPI is installed, all PAPI placeholders work in hologram text:

```
%player_displayname%
%player_health%
%server_time HH:mm%
```

## MiniMessage Format

Holograms use MiniMessage for formatting:

| Tag | Description |
|-----|-------------|
| `<red>`, `<blue>`, `<green>` etc. | Colors |
| `<bold>`, `<italic>`, `<underlined>` | Styles |
| `<gradient:red:blue>text</gradient>` | Gradient |
| `<rainbow>text</rainbow>` | Rainbow |
| `<#rrggbb>text</#rrggbb>` | Hex colors |
| `<click:run_command:/help>Click me</click>` | Click events |
| `<hover:show_text:Hover text>Hover me</hover>` | Hover events |

Legacy color codes (`&a`, `&6`, etc.) also work.
