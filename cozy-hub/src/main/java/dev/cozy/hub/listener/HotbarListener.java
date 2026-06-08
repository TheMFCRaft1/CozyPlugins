package dev.cozy.hub.listener;

import dev.cozy.hub.CozyHub;
import dev.cozy.hub.manager.HotbarManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Handles hotbar item interactions.
 * <p>
 * Detects right-click on hotbar items and delegates to the HotbarManager for action execution.
 */
public final class HotbarListener implements Listener {

    private final CozyHub plugin;
    private final HotbarManager hotbarManager;

    /**
     * Creates a new HotbarListener.
     *
     * @param plugin        the owning plugin
     * @param hotbarManager the hotbar manager
     */
    public HotbarListener(CozyHub plugin, HotbarManager hotbarManager) {
        this.plugin = plugin;
        this.hotbarManager = hotbarManager;
    }

    /**
     * Handles PlayerInteractEvent for right-click actions on hotbar items.
     *
     * @param event the interact event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        int slot = player.getInventory().getHeldItemSlot();

        hotbarManager.handleInteraction(player, slot);
    }
}
