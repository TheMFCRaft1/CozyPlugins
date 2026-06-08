package dev.cozy.hub;

import dev.cozy.core.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player join and quit events for the hub.
 */
public final class HubListener implements Listener {

    private final CozyHub plugin;
    private final SpawnManager spawnManager;
    private final MessageManager messageManager;

    /**
     * Creates a new HubListener.
     *
     * @param plugin         the owning plugin
     * @param spawnManager   the spawn manager
     * @param messageManager the message manager
     */
    public HubListener(CozyHub plugin, SpawnManager spawnManager, MessageManager messageManager) {
        this.plugin = plugin;
        this.spawnManager = spawnManager;
        this.messageManager = messageManager;
    }

    /**
     * Teleports the player to spawn on join if configured and sends the custom join message.
     *
     * @param event the PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = plugin.getConfig();

        if (config.getBoolean("hub.teleport-on-join", false)) {
            spawnManager.teleportToSpawn(player);
        }

        String joinMessage = config.getString("messages.join", "&7{player} &ahat den Server betreten");
        joinMessage = joinMessage.replace("{player}", player.getName());
        event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', joinMessage));
    }

    /**
     * Sends the custom quit message.
     *
     * @param event the PlayerQuitEvent
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = plugin.getConfig();

        String quitMessage = config.getString("messages.quit", "&7{player} &ahat den Server verlassen");
        quitMessage = quitMessage.replace("{player}", player.getName());
        event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', quitMessage));
    }
}
