package dev.cozy.hub.listener;

import dev.cozy.core.MessageManager;
import dev.cozy.hub.CozyHub;
import dev.cozy.hub.manager.HotbarManager;
import dev.cozy.hub.manager.SidebarManager;
import dev.cozy.hub.manager.TabListManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player quit events for the hub.
 * <p>
 * Sends custom quit messages, clears tab list and sidebar, and removes hotbar items.
 */
public final class PlayerQuitListener implements Listener {

    private final CozyHub plugin;
    private final MessageManager messageManager;
    private final SidebarManager sidebarManager;
    private final TabListManager tabListManager;
    private final HotbarManager hotbarManager;

    /**
     * Creates a new PlayerQuitListener.
     *
     * @param plugin         the owning plugin
     * @param messageManager the message manager
     * @param sidebarManager the sidebar manager
     * @param tabListManager the tab list manager
     * @param hotbarManager  the hotbar manager
     */
    public PlayerQuitListener(CozyHub plugin, MessageManager messageManager,
                              SidebarManager sidebarManager, TabListManager tabListManager,
                              HotbarManager hotbarManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
        this.sidebarManager = sidebarManager;
        this.tabListManager = tabListManager;
        this.hotbarManager = hotbarManager;
    }

    /**
     * Handles the PlayerQuitEvent.
     *
     * @param event the quit event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.setQuitMessage(null);

        String quitMessage = messageManager.get("quit-message");
        quitMessage = quitMessage.replace("{player}", player.getName());
        broadcastMessage(quitMessage);

        sidebarManager.removeSidebar(player);
        tabListManager.clearTabList(player);
        hotbarManager.removeHotbarItems(player);
    }

    private void broadcastMessage(String message) {
        String translated = ChatColor.translateAlternateColorCodes('&', message);
        for (Player online : plugin.getServer().getOnlinePlayers()) {
            online.sendMessage(translated);
        }
    }
}
