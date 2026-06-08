package dev.cozy.hub.manager;

import dev.cozy.core.ConfigManager;
import dev.cozy.hub.CozyHub;
import dev.cozy.hub.listener.MovementListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Manages spawn locations per world with configurable warmup teleportation.
 * <p>
 * Spawn locations are persisted in config.yml under {@code spawns.<worldName>}.
 * Supports a configurable warmup period during which movement cancels the teleport.
 */
public final class SpawnManager {

    private final CozyHub plugin;
    private final ConfigManager configManager;
    private final MovementListener movementListener;

    /**
     * Creates a new SpawnManager.
     *
     * @param plugin          the owning plugin
     * @param configManager   the config manager for config.yml
     * @param movementListener the movement listener for tracking player movement
     */
    public SpawnManager(CozyHub plugin, ConfigManager configManager, MovementListener movementListener) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.movementListener = movementListener;
    }

    /**
     * Saves the spawn location for the player's current world to config.yml.
     *
     * @param player the player whose location becomes the spawn
     */
    public void setSpawn(Player player) {
        Location loc = player.getLocation();
        String worldName = loc.getWorld().getName();

        String path = "spawns." + worldName;
        configManager.getConfig().set(path + ".x", loc.getX());
        configManager.getConfig().set(path + ".y", loc.getY());
        configManager.getConfig().set(path + ".z", loc.getZ());
        configManager.getConfig().set(path + ".yaw", (double) loc.getYaw());
        configManager.getConfig().set(path + ".pitch", (double) loc.getPitch());

        saveConfig();
    }

    /**
     * Returns the spawn location for the specified world.
     * Falls back to the world's default spawn if no custom spawn is configured.
     *
     * @param world the world to get the spawn for
     * @return the spawn location
     */
    public Location getSpawn(World world) {
        String path = "spawns." + world.getName();
        ConfigurationSection section = configManager.getConfig().getConfigurationSection(path);

        if (section != null) {
            double x = section.getDouble("x");
            double y = section.getDouble("y");
            double z = section.getDouble("z");
            float yaw = (float) section.getDouble("yaw");
            float pitch = (float) section.getDouble("pitch");
            return new Location(world, x, y, z, yaw, pitch);
        }

        return world.getSpawnLocation();
    }

    /**
     * Teleports the player to the spawn of their current world with a warmup.
     * <p>
     * During the warmup, an actionbar countdown is displayed.
     * If the player moves, the teleport is cancelled.
     *
     * @param player the player to teleport
     * @return true if the warmup started, false if already teleporting
     */
    public boolean teleportToSpawn(Player player) {
        int warmupSeconds = configManager.getConfig().getInt("spawn.warmup-seconds", 3);

        if (warmupSeconds <= 0) {
            doTeleport(player);
            return true;
        }

        if (isTeleporting(player.getUniqueId())) {
            return false;
        }

        Location startLocation = player.getLocation().clone();
        movementListener.setLastLocation(player.getUniqueId(), startLocation);

        new BukkitRunnable() {
            int remaining = warmupSeconds;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    removeTeleporting(player.getUniqueId());
                    return;
                }

                Location current = player.getLocation();
                Location last = movementListener.getLastLocation(player.getUniqueId());
                if (last != null && (current.getX() != last.getX()
                        || current.getY() != last.getY()
                        || current.getZ() != last.getZ())) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&cTeleportation abgebrochen!"));
                    cancel();
                    removeTeleporting(player.getUniqueId());
                    return;
                }

                if (remaining <= 0) {
                    cancel();
                    removeTeleporting(player.getUniqueId());
                    doTeleport(player);
                    return;
                }

                player.sendActionBar(ChatColor.translateAlternateColorCodes('&',
                        "&eTeleportiere in &f" + remaining + "s&e..."));
                movementListener.setLastLocation(player.getUniqueId(), player.getLocation().clone());
                remaining--;
            }
        }.runTaskTimer(plugin, 20L, 20L);

        addTeleporting(player.getUniqueId());
        return true;
    }

    /**
     * Checks if a player is currently in the teleport warmup phase.
     *
     * @param uuid the player's UUID
     * @return true if the player is warming up
     */
    public boolean isTeleporting(java.util.UUID uuid) {
        return teleportingPlayers.contains(uuid);
    }

    private final java.util.Set<java.util.UUID> teleportingPlayers = new java.util.HashSet<>();

    private void addTeleporting(java.util.UUID uuid) {
        teleportingPlayers.add(uuid);
    }

    private void removeTeleporting(java.util.UUID uuid) {
        teleportingPlayers.remove(uuid);
    }

    private void doTeleport(Player player) {
        Location spawn = getSpawn(player.getWorld());
        player.teleport(spawn);

        if (configManager.getConfig().getBoolean("spawn.reset-inventory-on-join", false)) {
            player.getInventory().clear();
        }
    }

    /**
     * Returns whether teleport-on-join is enabled.
     *
     * @return true if players should be teleported on join
     */
    public boolean isTeleportOnJoin() {
        return configManager.getConfig().getBoolean("spawn.teleport-on-join", true);
    }

    /**
     * Returns whether inventory should be reset on join.
     *
     * @return true if inventory should be cleared
     */
    public boolean isResetInventoryOnJoin() {
        return configManager.getConfig().getBoolean("spawn.reset-inventory-on-join", false);
    }

    private void saveConfig() {
        try {
            java.io.File file = new java.io.File(
                    plugin.getDataFolder(), "config.yml"
            );
            configManager.getConfig().save(file);
        } catch (java.io.IOException e) {
            plugin.getLogger().severe("Could not save config.yml: " + e.getMessage());
        }
    }
}
