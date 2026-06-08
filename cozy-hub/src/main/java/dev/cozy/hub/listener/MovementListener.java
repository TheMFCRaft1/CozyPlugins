package dev.cozy.hub.listener;

import dev.cozy.core.ConfigManager;
import dev.cozy.hub.CozyHub;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles movement-related features including double jump.
 * <p>
 * Tracks player locations for teleport warmup cancellation and implements
 * a configurable double jump mechanic with velocity, particles, and sound.
 */
public final class MovementListener implements Listener {

    private final CozyHub plugin;
    private final ConfigManager configManager;
    private final Map<UUID, Location> lastLocations = new HashMap<>();

    /**
     * Creates a new MovementListener.
     *
     * @param plugin        the owning plugin
     * @param configManager the config manager
     */
    public MovementListener(CozyHub plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    /**
     * Sets the last recorded location for a player.
     *
     * @param uuid     the player's UUID
     * @param location the location to store
     */
    public void setLastLocation(UUID uuid, Location location) {
        lastLocations.put(uuid, location);
    }

    /**
     * Gets the last recorded location for a player.
     *
     * @param uuid the player's UUID
     * @return the last location, or null if not tracked
     */
    public Location getLastLocation(UUID uuid) {
        return lastLocations.get(uuid);
    }

    /**
     * Handles PlayerMoveEvent to update last tracked location.
     *
     * @param event the move event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        if (to != null) {
            lastLocations.put(player.getUniqueId(), to.clone());
        }
    }

    /**
     * Handles PlayerToggleFlightEvent for double jump.
     *
     * @param event the toggle flight event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE
                || player.getGameMode() == org.bukkit.GameMode.SPECTATOR) {
            return;
        }

        ConfigurationSection section = configManager.getConfig().getConfigurationSection("double-jump");
        if (section == null || !section.getBoolean("enabled", false)) {
            return;
        }

        String permission = section.getString("permission", "cozyhub.doublejump");
        if (!player.hasPermission(permission)) {
            return;
        }

        event.setCancelled(true);
        player.setFlying(false);

        double velocityX = section.getDouble("velocity-x", 1.0);
        double velocityY = section.getDouble("velocity-y", 0.8);

        Vector direction = player.getLocation().getDirection();
        Vector velocity = direction.multiply(velocityX).setY(velocityY);
        player.setVelocity(velocity);

        String particleName = section.getString("particle", "CLOUD");
        try {
            Particle particle = Particle.valueOf(particleName.toUpperCase());
            player.getWorld().spawnParticle(particle, player.getLocation(), 15, 0.5, 0.5, 0.5, 0.1);
        } catch (IllegalArgumentException ignored) {
        }

        String soundName = section.getString("sound", "ENTITY_BAT_TAKEOFF");
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException ignored) {
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    player.setAllowFlight(true);
                }
            }
        }.runTaskLater(plugin, 1L);
    }
}
