package dev.cozy.hub;

import dev.cozy.core.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Manages the hub spawn location, persisting it to config.yml.
 */
public final class SpawnManager {

    private final ConfigManager configManager;

    /**
     * Creates a new SpawnManager.
     *
     * @param configManager the ConfigManager for the plugin's config.yml
     */
    public SpawnManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Saves the spawn location to the config and writes it to disk.
     *
     * @param location the new spawn location
     */
    public void setSpawn(Location location) {
        ConfigurationSection section = configManager.getConfig().createSection("hub.spawn");
        section.set("world", location.getWorld().getName());
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
        section.set("yaw", (double) location.getYaw());
        section.set("pitch", (double) location.getPitch());

        configManager.getConfig().set("hub.spawn", section);
        configManager.getConfig().options().copyDefaults(true);
        saveConfig();
    }

    /**
     * Returns the saved spawn location, or null if not set.
     *
     * @return the spawn Location, or null
     */
    public Location getSpawn() {
        ConfigurationSection section = configManager.getConfig().getConfigurationSection("hub.spawn");
        if (section == null) {
            return null;
        }

        World world = org.bukkit.Bukkit.getWorld(section.getString("world", "world"));
        if (world == null) {
            return null;
        }

        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        float yaw = (float) section.getDouble("yaw");
        float pitch = (float) section.getDouble("pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Teleports a player to the saved spawn with a welcome title.
     *
     * @param player the player to teleport
     * @return true if the spawn was set and teleportation succeeded
     */
    public boolean teleportToSpawn(Player player) {
        Location spawn = getSpawn();
        if (spawn == null) {
            return false;
        }

        player.teleport(spawn);
        player.sendTitle(
                ChatColor.translateAlternateColorCodes('&', "&aWillkommen!"),
                "",
                10, 40, 10
        );
        return true;
    }

    /**
     * Saves the current config to disk.
     */
    private void saveConfig() {
        try {
            configManager.getConfig().save(new java.io.File(
                    configManager.getConfig().getCurrentPath() != null
                            ? configManager.getConfig().getCurrentPath().toString()
                            : "plugins/CozyHub/config.yml"
            ));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
