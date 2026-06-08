package dev.cozy.holograms.manager;

import dev.cozy.holograms.hologram.Hologram;
import dev.cozy.holograms.hologram.HologramLine;
import dev.cozy.holograms.util.HologramSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all holograms on the server.
 * <p>
 * Handles creation, deletion, persistence, and periodic updates
 * of hologram entities with placeholder support.
 */
public final class HologramManager {

    private final Map<String, Hologram> holograms = new ConcurrentHashMap<>();
    private final HologramSerializer serializer;
    private final dev.cozy.holograms.CozyHolograms plugin;
    private BukkitRunnable updateTask;

    /**
     * Creates a new HologramManager.
     *
     * @param plugin     the owning plugin
     * @param serializer the hologram serializer
     */
    public HologramManager(dev.cozy.holograms.CozyHolograms plugin, HologramSerializer serializer) {
        this.plugin = plugin;
        this.serializer = serializer;
    }

    /**
     * Creates a new hologram at the specified location.
     *
     * @param id       the unique hologram ID
     * @param location the location to create it at
     * @return the created Hologram
     */
    public Hologram createHologram(String id, Location location) {
        return createHologram(id, location, true);
    }

    /**
     * Creates a new hologram at the specified location.
     *
     * @param id         the unique hologram ID
     * @param location   the location to create it at
     * @param persistent whether to save to disk
     * @return the created Hologram
     */
    public Hologram createHologram(String id, Location location, boolean persistent) {
        Hologram hologram = new Hologram(id, location, persistent);
        holograms.put(id, hologram);
        saveAll();
        return hologram;
    }

    /**
     * Deletes a hologram by ID.
     *
     * @param id the hologram ID
     * @return true if the hologram was found and deleted
     */
    public boolean deleteHologram(String id) {
        Hologram hologram = holograms.remove(id);
        if (hologram == null) return false;

        hologram.delete();
        saveAll();
        return true;
    }

    /**
     * Returns a hologram by ID.
     *
     * @param id the hologram ID
     * @return an Optional containing the hologram, or empty
     */
    public Optional<Hologram> getHologram(String id) {
        return Optional.ofNullable(holograms.get(id));
    }

    /**
     * Returns all holograms.
     *
     * @return a collection of all holograms
     */
    public Collection<Hologram> getAllHolograms() {
        return holograms.values();
    }

    /**
     * Loads all holograms from holograms.yml.
     */
    public void loadAll() {
        holograms.clear();
        holograms.putAll(serializer.loadAll());
        for (Hologram holo : holograms.values()) {
            holo.respawn();
        }
    }

    /**
     * Saves all holograms to holograms.yml.
     */
    public void saveAll() {
        serializer.saveAll(holograms);
    }

    /**
     * Starts the periodic update task for placeholder-refreshed lines.
     */
    public void startUpdateTask() {
        long interval = plugin.getConfig().getLong("settings.update-interval", 20L);

        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateAll();
            }
        };
        updateTask.runTaskTimer(plugin, interval, interval);
    }

    /**
     * Stops the update task.
     */
    public void stopUpdateTask() {
        if (updateTask != null && !updateTask.isCancelled()) {
            updateTask.cancel();
        }
        updateTask = null;
    }

    /**
     * Removes all hologram entities from the world.
     */
    public void despawnAll() {
        for (Hologram holo : holograms.values()) {
            holo.delete();
        }
    }

    /**
     * Respawns all holograms.
     */
    public void respawnAll() {
        for (Hologram holo : holograms.values()) {
            holo.respawn();
        }
    }

    private void updateAll() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        if (onlinePlayers.isEmpty()) return;

        Player referencePlayer = onlinePlayers.iterator().next();

        for (Hologram holo : holograms.values()) {
            for (HologramLine line : holo.getLines()) {
                if (line.hasPlaceholders() && !line.isAnimated()) {
                    line.update(line.getRawText(), referencePlayer);
                }
            }
        }
    }
}
