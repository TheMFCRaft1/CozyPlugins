# FAQ

## General

### Does CozyHub require BungeeCord or Velocity?

CozyHub works with both BungeeCord and Velocity for the server navigator. The server selector sends a `BungeeCord` plugin message channel with the `Connect` sub-channel, which is compatible with both proxy types.

### Does CozyHub require PlaceholderAPI?

No. CozyHub has built-in placeholder support for `{player}`, `{online}`, `{max}`, `{world}`, and `{rank}`. If PlaceholderAPI is installed, CozyHub will use it for additional placeholders, but it is not required.

### Can I use CozyHub without the other modules?

Yes. Each module is independent. CozyHub only depends on `cozy-core` which is shaded into the JAR automatically.

## Spawn

### How do I set the spawn?

Use `/hub setspawn` while standing where you want the spawn to be. The spawn is saved per-world.

### What happens if no spawn is set?

Players will be teleported to the world's default spawn location (the one set with `/setworldspawn` in vanilla).

### How does the warmup work?

When `spawn.warmup-seconds` is greater than 0, players see an actionbar countdown before teleporting. If they move during the warmup, the teleport is cancelled.

### Can I disable the warmup?

Set `spawn.warmup-seconds` to `0` for instant teleportation.

## Protection

### Can I disable individual protection features?

Yes. Each protection feature has its own toggle in `config.yml` under the `protection` section. Set any feature to `false` to disable it.

### Does hub-fly affect creative/spectator mode?

No. The double jump and flight features only apply to players in survival or adventure mode. Creative and spectator players are not affected.

### Why can't players break blocks?

The `protection.disable-block-break` option is enabled by default. Set it to `false` in `config.yml` to allow block breaking.

## Hotbar

### How do I add new hotbar items?

Add a new entry under `hotbar.items` in `config.yml`:

```yaml
hotbar:
  items:
    3:
      slot: 3
      material: PAPER
      name: "&eInfo"
      lore: ["&7Click for info"]
      action: RUN_COMMAND
      command: "/help"
      glow: false
```

### What actions are available?

- `OPEN_NAVIGATOR` — Opens the server selector GUI
- `TOGGLE_VISIBILITY` — Toggles player visibility
- `DISCONNECT` — Kicks the player from the server
- `RUN_COMMAND` — Executes a command (set `command` field)
- `OPEN_URL` — Reserved for future use

## Navigator

### How do I add servers to the navigator?

Add entries under `servers` in `config.yml`:

```yaml
servers:
  myserver:
    slot: 11
    name: "&aMy Server"
    material: DIAMOND_SWORD
    lore:
      - "&7Click to connect"
    server: "myserver"       # BungeeCord server name
    action: SERVER_CONNECT
```

### What is the fill material?

The fill material is the item that fills empty slots in the GUI. Default is gray stained glass pane. Change it with `navigator.fill-material`.

### Can I change the GUI size?

Yes. Set `navigator.size` to 9, 18, 27, 36, 45, or 54 ( multiples of 9).

## Cosmetics

### How do I change the join firework colors?

Edit the `cosmetics.join-firework.colors` list in `config.yml`. Available colors: `RED`, `AQUA`, `WHITE`, `BLUE`, `GREEN`, `YELLOW`, `PURPLE`, `ORANGE`, `PINK`, `LIME`, `CYAN`, `MAGENTA`.

### How do I change the join sound?

Edit `cosmetics.join-sound.sound` in `config.yml`. Use any valid Bukkit sound name (e.g., `ENTITY_PLAYER_LEVELUP`, `ENTITY_EXPERIENCE_ORB_PICKUP`).

## Troubleshooting

### Players can't fly in the hub

Make sure `protection.hub-fly` is set to `true` in `config.yml`.

### Double jump doesn't work

1. Ensure `double-jump.enabled` is `true`
2. Ensure `protection.hub-fly` is `true` (double jump requires flight)
3. Ensure the player has the `cozyhub.doublejump` permission

### Navigator doesn't connect to servers

1. Ensure your BungeeCord/Velocity proxy is running
2. Ensure the server names in `config.yml` match your proxy config
3. Ensure the `BungeeCord` plugin message channel is registered

### Spawn teleport doesn't work

1. Ensure a spawn has been set with `/hub setspawn`
2. Check that `spawns.<worldName>` exists in `config.yml`
