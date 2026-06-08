package dev.cozy.hub.listener;

import dev.cozy.core.ConfigManager;
import dev.cozy.core.MessageManager;
import dev.cozy.hub.CozyHub;
import dev.cozy.hub.manager.CosmeticManager;
import dev.cozy.hub.manager.HotbarManager;
import dev.cozy.hub.manager.SpawnManager;
import dev.cozy.hub.manager.TabListManager;
import dev.cozy.hub.manager.VisibilityManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Handles player join events for the hub.
 * <p>
 * Manages custom join messages, join titles, actionbar messages, first-join logic,
 * starter kits, spawn teleportation, and cosmetic effects.
 */
public final class PlayerJoinListener implements Listener {

    private final CozyHub plugin;
    private final SpawnManager spawnManager;
    private final MessageManager messageManager;
    private final HotbarManager hotbarManager;
    private final TabListManager tabListManager;
    private final CosmeticManager cosmeticManager;
    private final VisibilityManager visibilityManager;
    private final ConfigManager configManager;

    /**
     * Creates a new PlayerJoinListener.
     *
     * @param plugin            the owning plugin
     * @param spawnManager      the spawn manager
     * @param messageManager    the message manager
     * @param hotbarManager     the hotbar manager
     * @param tabListManager    the tab list manager
     * @param cosmeticManager   the cosmetic manager
     * @param visibilityManager the visibility manager
     * @param configManager     the config manager
     */
    public PlayerJoinListener(CozyHub plugin, SpawnManager spawnManager,
                              MessageManager messageManager, HotbarManager hotbarManager,
                              TabListManager tabListManager, CosmeticManager cosmeticManager,
                              VisibilityManager visibilityManager, ConfigManager configManager) {
        this.plugin = plugin;
        this.spawnManager = spawnManager;
        this.messageManager = messageManager;
        this.hotbarManager = hotbarManager;
        this.tabListManager = tabListManager;
        this.cosmeticManager = cosmeticManager;
        this.visibilityManager = visibilityManager;
        this.configManager = configManager;
    }

    /**
     * Handles the PlayerJoinEvent.
     *
     * @param event the join event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ConfigurationSection titleConfig = configManager.getConfig().getConfigurationSection("title");

        event.setJoinMessage(null);

        String joinMessage = messageManager.get("join-message");
        joinMessage = joinMessage.replace("{player}", player.getName());
        broadcastMessage(joinMessage);

        if (player.hasPlayedBefore()) {
            handleReturningPlayer(player);
        } else {
            handleFirstJoin(player);
        }

        tabListManager.setTabList(player);
        hotbarManager.giveHotbarItems(player);

        visibilityManager.applyOnJoin(player);

        cosmeticManager.applyJoinEffects(player);

        if (titleConfig != null) {
            String title = replacePlaceholders(titleConfig.getString("join-title", "&aWillkommen!"), player);
            String subtitle = replacePlaceholders(titleConfig.getString("join-subtitle", "&7Schön dass du da bist, &f{player}"), player);
            int fadeIn = titleConfig.getInt("fade-in", 10);
            int stay = titleConfig.getInt("stay", 60);
            int fadeOut = titleConfig.getInt("fade-out", 20);

            player.sendTitle(
                    ChatColor.translateAlternateColorCodes('&', title),
                    ChatColor.translateAlternateColorCodes('&', subtitle),
                    fadeIn, stay, fadeOut
            );
        }

        if (configManager.getConfig().getBoolean("title.show-actionbar", false)) {
            String actionbar = replacePlaceholders(
                    configManager.getConfig().getString("title.actionbar-message", "&eWillkommen auf dem Server!"), player);
            player.sendActionBar(ChatColor.translateAlternateColorCodes('&', actionbar));
        }
    }

    private void handleReturningPlayer(Player player) {
        if (spawnManager.isTeleportOnJoin()) {
            spawnManager.teleportToSpawn(player);
        }
    }

    private void handleFirstJoin(Player player) {
        String firstJoinMsg = messageManager.get("first-join-message");
        firstJoinMsg = firstJoinMsg.replace("{player}", player.getName());
        broadcastMessage(firstJoinMsg);

        giveStarterKit(player);

        if (spawnManager.isTeleportOnJoin()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    spawnManager.teleportToSpawn(player);
                }
            }.runTaskLater(plugin, 5L);
        }
    }

    private void giveStarterKit(Player player) {
        ConfigurationSection kitSection = configManager.getConfig().getConfigurationSection("starter-kit");
        if (kitSection == null) return;

        List<String> items = kitSection.getStringList("items");
        for (String itemName : items) {
            org.bukkit.Material material = org.bukkit.Material.matchMaterial(itemName);
            if (material != null) {
                player.getInventory().addItem(new ItemStack(material));
            }
        }
    }

    private void broadcastMessage(String message) {
        String translated = ChatColor.translateAlternateColorCodes('&', message);
        for (Player online : plugin.getServer().getOnlinePlayers()) {
            online.sendMessage(translated);
        }
    }

    private String replacePlaceholders(String text, Player player) {
        text = text.replace("{player}", player.getName());
        text = text.replace("{online}", String.valueOf(plugin.getServer().getOnlinePlayers().size()));
        text = text.replace("{world}", player.getWorld().getName());
        return text;
    }
}
