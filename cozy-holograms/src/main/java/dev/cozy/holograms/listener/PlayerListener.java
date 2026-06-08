package dev.cozy.holograms.listener;

import dev.cozy.holograms.manager.HologramManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handles player events for hologram management.
 * <p>
 * Ensures holograms are visible when players join by forcing
 * chunk loads near hologram locations.
 */
public final class PlayerListener implements Listener {

    private final HologramManager hologramManager;

    /**
     * Creates a new PlayerListener.
     *
     * @param hologramManager the hologram manager
     */
    public PlayerListener(HologramManager hologramManager) {
        this.hologramManager = hologramManager;
    }

    /**
     * Handles PlayerJoinEvent to ensure hologram chunks are loaded.
     *
     * @param event the join event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var holograms = hologramManager.getAllHolograms();
        for (var holo : holograms) {
            if (holo.getLocation().getWorld() != null) {
                holo.getLocation().getChunk().load(true);
            }
        }
    }
}
