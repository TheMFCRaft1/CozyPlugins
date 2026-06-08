package dev.cozy.holograms.api;

import dev.cozy.holograms.hologram.Hologram;
import dev.cozy.holograms.manager.HologramManager;
import org.bukkit.Location;

import java.util.Collection;
import java.util.Optional;

/**
 * Public developer API for CozyHolograms.
 * <p>
 * Provides static access to hologram creation, deletion, and querying.
 * Obtain the instance via {@link #get()} after the plugin has been enabled.
 */
public final class CozyHologramsAPI {

    private static CozyHologramsAPI instance;
    private HologramManager hologramManager;

    private CozyHologramsAPI() {
    }

    /**
     * Returns the singleton API instance.
     *
     * @return the API instance
     * @throws IllegalStateException if the API has not been initialized
     */
    public static CozyHologramsAPI get() {
        if (instance == null) {
            throw new IllegalStateException("CozyHologramsAPI has not been initialized yet.");
        }
        return instance;
    }

    /**
     * Initializes the API instance with the given manager.
     *
     * @param manager the hologram manager
     */
    public static void init(HologramManager manager) {
        instance = new CozyHologramsAPI();
        instance.hologramManager = manager;
    }

    /**
     * Creates a persistent hologram at the specified location.
     *
     * @param id       the unique hologram ID
     * @param location the location
     * @return the created Hologram
     */
    public Hologram createHologram(String id, Location location) {
        return hologramManager.createHologram(id, location, true);
    }

    /**
     * Creates a hologram at the specified location.
     *
     * @param id         the unique hologram ID
     * @param location   the location
     * @param persistent whether to save to disk
     * @return the created Hologram
     */
    public Hologram createHologram(String id, Location location, boolean persistent) {
        return hologramManager.createHologram(id, location, persistent);
    }

    /**
     * Deletes a hologram by ID.
     *
     * @param id the hologram ID
     * @return true if deleted
     */
    public boolean deleteHologram(String id) {
        return hologramManager.deleteHologram(id);
    }

    /**
     * Returns a hologram by ID.
     *
     * @param id the hologram ID
     * @return an Optional containing the hologram
     */
    public Optional<Hologram> getHologram(String id) {
        return hologramManager.getHologram(id);
    }

    /**
     * Returns all loaded holograms.
     *
     * @return a collection of holograms
     */
    public Collection<Hologram> getAllHolograms() {
        return hologramManager.getAllHolograms();
    }

    /**
     * Returns whether the API is loaded and ready.
     *
     * @return true if initialized
     */
    public boolean isLoaded() {
        return hologramManager != null;
    }
}
