# Commands Reference

## /hub

Main hub command with subcommands.

| Subcommand | Description | Permission |
|------------|-------------|------------|
| `/hub` | Teleport to spawn of current world | `cozyhub.use` |
| `/hub setspawn` | Set spawn to your current location | `cozyhub.setspawn` |
| `/hub reload` | Reload plugin configuration | `cozyhub.admin` |

### Examples

```
/hub              → Teleport to spawn
/hub setspawn     → Set the hub spawn
/hub reload       → Reload config.yml and messages.yml
```

## /visibility

Toggle visibility of other players.

| Command | Description | Permission |
|---------|-------------|------------|
| `/visibility` | Toggle player visibility on/off | `cozyhub.visibility` |
| `/vis` | Alias for /visibility | `cozyhub.visibility` |

### Examples

```
/visibility       → Toggle visibility
/vis              → Same as above
```

## Notes

- All commands support `&` color codes in feedback messages
- Messages are defined in `messages.yml` and can be customized
- The prefix is configured in `messages.yml` under `prefix`
