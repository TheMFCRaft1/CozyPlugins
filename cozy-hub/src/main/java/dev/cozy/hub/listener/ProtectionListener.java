package dev.cozy.hub.listener;

import dev.cozy.core.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handles all hub protection features.
 * <p>
 * Each protection feature can be individually toggled via config.yml.
 * Prevents PvP, damage, block modification, hunger, item interactions, and more.
 */
public final class ProtectionListener implements Listener {

    private final ConfigManager configManager;

    /**
     * Creates a new ProtectionListener.
     *
     * @param configManager the config manager
     */
    public ProtectionListener(ConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Handles PvP damage between players.
     *
     * @param event the EntityDamageByEntityEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        FileConfiguration config = configManager.getConfig();

        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            if (config.getBoolean("protection.disable-pvp", true)) {
                event.setCancelled(true);
            }
        }

        if (config.getBoolean("protection.disable-damage", true)) {
            event.setCancelled(true);
        }
    }

    /**
     * Handles all entity damage events.
     *
     * @param event the EntityDamageEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent event) {
        if (configManager.getConfig().getBoolean("protection.disable-damage", true)) {
            event.setCancelled(true);
        }
    }

    /**
     * Handles block break events.
     *
     * @param event the BlockBreakEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        if (configManager.getConfig().getBoolean("protection.disable-block-break", true)) {
            event.setCancelled(true);
        }
    }

    /**
     * Handles block place events.
     *
     * @param event the BlockPlaceEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (configManager.getConfig().getBoolean("protection.disable-block-place", true)) {
            event.setCancelled(true);
        }
    }

    /**
     * Handles food level change events.
     *
     * @param event the FoodLevelChangeEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (configManager.getConfig().getBoolean("protection.disable-hunger", true)) {
            event.setCancelled(true);
            if (event.getEntity() instanceof Player player) {
                player.setFoodLevel(20);
            }
        }
    }

    /**
     * Handles player join to set fly permissions.
     *
     * @param event the PlayerJoinEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (configManager.getConfig().getBoolean("protection.hub-fly", true)) {
            Player player = event.getPlayer();
            player.setAllowFlight(true);
            player.setFlying(true);
        }
    }

    /**
     * Handles item drop events.
     *
     * @param event the PlayerDropItemEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (configManager.getConfig().getBoolean("protection.disable-item-drop", true)) {
            event.setCancelled(true);
        }
    }

    /**
     * Handles item pickup events.
     *
     * @param event the EntityPickupItemEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (configManager.getConfig().getBoolean("protection.disable-item-pickup", true)) {
            event.setCancelled(true);
        }
    }

    /**
     * Handles inventory click events.
     *
     * @param event the InventoryClickEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent event) {
        if (configManager.getConfig().getBoolean("protection.disable-inventory-click", false)) {
            event.setCancelled(true);
        }
    }
}
