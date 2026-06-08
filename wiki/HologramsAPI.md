# CozyHolograms Developer API

## Getting the API

```java
CozyHologramsAPI api = CozyHologramsAPI.get();
```

The API is initialized after CozyHolograms enables. Always check availability first:

```java
if (Bukkit.getPluginManager().getPlugin("CozyHolograms") != null) {
    CozyHologramsAPI api = CozyHologramsAPI.get();
}
```

## Creating Holograms

```java
// Create a persistent hologram
Hologram holo = api.createHologram("myid", location);

// Create a non-persistent (temporary) hologram
Hologram holo = api.createHologram("myid", location, false);

// Add lines
holo.addLine("&6Welcome!");
holo.addLine("&7Players: {online}");

// Add animated line
holo.addAnimatedLine(Arrays.asList("&aFrame1", "&bFrame2", "&cFrame3"), 10);
```

## Managing Holograms

```java
// Get a hologram
Optional<Hologram> holo = api.getHologram("myid");

// Get all holograms
Collection<Hologram> all = api.getAllHolograms();

// Delete a hologram
boolean deleted = api.deleteHologram("myid");

// Edit lines
holo.get().setLine(0, "&6New first line");

// Remove a line
holo.get().removeLine(1);

// Teleport
holo.get().teleport(newLocation);

// Check if loaded
boolean ready = api.isLoaded();
```

## Hologram Properties

```java
Hologram holo = api.getHologram("myid").orElse(null);

holo.getId();                    // "myid"
holo.getLocation();              // Location
holo.getLines();                 // List<HologramLine>
holo.isPersistent();             // true/false
holo.setPersistent(false);       // Don't save to disk
holo.isAnimated();               // true/false
holo.setAnimationInterval(20);   // Ticks between frames
```

## HologramLine Properties

```java
HologramLine line = holo.getLines().get(0);

line.getRawText();               // "&6Welcome!"
line.getLineIndex();             // 0
line.getEntity();                // TextDisplay entity
line.hasPlaceholders();          // true if contains "{"
line.isAnimated();               // true if has running animation
```
