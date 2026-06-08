# CozyHolograms FAQ

## General

### Does CozyHolograms use armor stands?

No. CozyHolograms uses Paper's native `TextDisplay` entities, which are lighter and more performant than armor stands.

### Does CozyHolograms require PlaceholderAPI?

No. PlaceholderAPI is a soft dependency. CozyHolograms has built-in placeholders (`{online}`, `{max}`, `{world}`, `{player}`), but if PlaceholderAPI is installed, all PAPI placeholders work in hologram text.

### Can other plugins use CozyHolograms?

Yes. CozyHolograms provides a public API via `CozyHologramsAPI`:

```java
CozyHologramsAPI api = CozyHologramsAPI.get();
Hologram holo = api.createHologram("test", location);
holo.addLine("&aHello from another plugin!");
```

## Creating Holograms

### How do I create a hologram?

```
/holo create <name>
/holo addline <name> &6Your text here
```

### How do I add animated lines?

Animated lines are configured in `holograms.yml` directly:

```yaml
holograms:
  my-holo:
    world: world
    x: 0.5
    y: 65.0
    z: 0.5
    lines:
      - "&6Static line"
      - frames:
          - "&aFrame 1"
          - "&bFrame 2"
          - "&cFrame 3"
        interval: 10
```

### How do I use colors and formatting?

Holograms support both MiniMessage and legacy color codes:

```
/holo addline myhologram <green>Hello!</green>
/holo addline myhologram &6Gold text &ahere
/holo addline myhologram <bold><red>Bold Red</red></bold>
```

### How do I use placeholders?

Just write them in the text:

```
/holo addline myhologram Players online: {online}
/holo addline myhologram Welcome, {player}!
```

If PlaceholderAPI is installed, PAPI placeholders also work:

```
/holo addline myhologram Your health: %player_health%
```

## Troubleshooting

### Hologram not visible

1. Check the world is loaded
2. Make sure the chunk is loaded (walk near it)
3. Use `/holo info <name>` to verify location
4. Try `/holo reload` to respawn all holograms

### Placeholders not updating

Check `config.yml` `settings.update-interval`. Default is 20 ticks (1 second). Also ensure players are within view distance.

### Cannot delete hologram

Ensure you have the `cozyholo.delete` permission.

### Hologram disappeared after restart

Check that the hologram is persistent (it should be by default). Look at `holograms.yml` to verify it was saved.
