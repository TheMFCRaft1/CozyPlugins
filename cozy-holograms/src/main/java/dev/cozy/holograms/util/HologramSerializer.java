package dev.cozy.holograms.util;

import dev.cozy.holograms.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Handles serialization and deserialization of holograms to/from YAML.
 * <p>
 * Holograms are stored in {@code holograms.yml} with support for
 * static lines and animated frames.
 */
public final class HologramSerializer {

    private final File dataFolder;
    private File hologramsFile;
    private FileConfiguration config;

    /**
     * Creates a new HologramSerializer.
     *
     * @param dataFolder the plugin data folder
     */
    public HologramSerializer(File dataFolder) {
        this.dataFolder = dataFolder;
        loadFile();
    }

    /**
     * Loads or creates the holograms.yml file.
     */
    private void loadFile() {
        hologramsFile = new File(dataFolder, "holograms.yml");

        if (!hologramsFile.exists()) {
            dataFolder.mkdirs();
            try (InputStream in = getClass().getResourceAsStream("/holograms.yml")) {
                if (in != null) {
                    Files.copy(in, hologramsFile.toPath());
                } else {
                    hologramsFile.createNewFile();
                }
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Could not create holograms.yml", e);
            }
        }

        config = YamlConfiguration.loadConfiguration(hologramsFile);
    }

    /**
     * Deserializes all holograms from the YAML file.
     *
     * @return a map of hologram ID to Hologram
     */
    public Map<String, Hologram> loadAll() {
        Map<String, Hologram> holograms = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("holograms");
        if (section == null) return holograms;

        for (String id : section.getKeys(false)) {
            ConfigurationSection holoSection = section.getConfigurationSection(id);
            if (holoSection == null) continue;

            Hologram hologram = deserialize(id, holoSection);
            if (hologram != null) {
                holograms.put(id, hologram);
            }
        }

        return holograms;
    }

    /**
     * Serializes all holograms to the YAML file.
     *
     * @param holograms the holograms to save
     */
    public void saveAll(Map<String, Hologram> holograms) {
        config.set("holograms", null);

        for (Map.Entry<String, Hologram> entry : holograms.entrySet()) {
            Hologram holo = entry.getValue();
            if (!holo.isPersistent()) continue;

            String path = "holograms." + entry.getKey();
            Location loc = holo.getLocation();

            config.set(path + ".world", loc.getWorld() != null ? loc.getWorld().getName() : "world");
            config.set(path + ".x", loc.getX());
            config.set(path + ".y", loc.getY());
            config.set(path + ".z", loc.getZ());
            config.set(path + ".animated", holo.isAnimated());
            config.set(path + ".animation-interval", holo.getAnimationInterval());

            List<Object> lineData = new ArrayList<>();
            for (int i = 0; i < holo.getLines().size(); i++) {
                var line = holo.getLines().get(i);
                if (line.getAnimation() != null) {
                    Map<String, Object> animatedLine = new HashMap<>();
                    animatedLine.put("frames", line.getAnimation().getFrames());
                    animatedLine.put("interval", line.getAnimation().getInterval());
                    lineData.add(animatedLine);
                } else {
                    lineData.add(line.getRawText());
                }
            }
            config.set(path + ".lines", lineData);
        }

        try {
            config.save(hologramsFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save holograms.yml", e);
        }
    }

    /**
     * Deserializes a single hologram from a configuration section.
     *
     * @param id      the hologram ID
     * @param section the configuration section
     * @return the deserialized Hologram, or null on error
     */
    private Hologram deserialize(String id, ConfigurationSection section) {
        String worldName = section.getString("world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Bukkit.getLogger().warning("World not found for hologram '" + id + "': " + worldName);
            return null;
        }

        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        Location location = new Location(world, x, y, z);

        boolean animated = section.getBoolean("animated", false);
        int animationInterval = section.getInt("animation-interval", 20);

        Hologram hologram = new Hologram(id, location, true);
        hologram.setAnimated(animated);
        hologram.setAnimationInterval(animationInterval);

        List<?> lines = section.getList("lines");
        if (lines == null) return hologram;

        for (Object lineObj : lines) {
            if (lineObj instanceof String text) {
                hologram.addLine(text);
            } else if (lineObj instanceof Map<?, ?> map) {
                Object framesObj = map.get("frames");
                Object intervalObj = map.get("interval");

                if (framesObj instanceof List<?> framesList) {
                    List<String> frames = new ArrayList<>();
                    for (Object frame : framesList) {
                        frames.add(String.valueOf(frame));
                    }
                    int interval = intervalObj instanceof Number num ? num.intValue() : animationInterval;
                    hologram.addAnimatedLine(frames, interval);
                }
            }
        }

        return hologram;
    }

    /**
     * Reloads the holograms file from disk.
     */
    public void reload() {
        loadFile();
    }
}
