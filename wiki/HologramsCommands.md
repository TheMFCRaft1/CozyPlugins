# CozyHolograms Commands

## /holo

Main command for managing holograms. Aliases: `/holograms`, `/ch`

| Subcommand | Description | Permission |
|------------|-------------|------------|
| `/holo create <name>` | Create a hologram at your location | `cozyholo.create` |
| `/holo delete <name>` | Delete a hologram | `cozyholo.delete` |
| `/holo edit <name> <line> <text>` | Edit a specific line (1-based) | `cozyholo.edit` |
| `/holo addline <name> <text>` | Add a line to a hologram | `cozyholo.edit` |
| `/holo removeline <name> <line>` | Remove a line (1-based) | `cozyholo.edit` |
| `/holo list` | List all holograms | `cozyholo.list` |
| `/holo teleport <name>` | Teleport to a hologram | `cozyholo.edit` |
| `/holo info <name>` | Show hologram details | `cozyholo.list` |
| `/holo reload` | Reload all holograms from config | `cozyholo.admin` |

## Examples

### Create and set up a hologram

```
/holo create welcome
/holo addline welcome &6&lWelcome to the Server!
/holo addline welcome &7Players online: &a{online}
/holo addline welcome &7Have fun!
```

### Edit an existing line

```
/holo edit welcome 2 &7Players online: &a{online} &7/ &f{max}
```

### Remove a line

```
/holo removeline welcome 3
```

### List all holograms

```
/holo list
```

### View hologram info

```
/holo info welcome
```

### Teleport to a hologram

```
/holo teleport welcome
```

### Reload all holograms

```
/holo reload
```

## Tab Completion

All subcommands support tab completion:
- Argument 1: subcommand names
- Argument 2: existing hologram names
- Argument 3 (edit/removeline): line numbers
