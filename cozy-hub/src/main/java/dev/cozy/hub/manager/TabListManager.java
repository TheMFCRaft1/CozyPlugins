package dev.cozy.hub.manager;

import dev.cozy.core.ConfigManager;
import dev.cozy.hub.CozyHub;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Manages tab list header and footer for all players.
 * <p>
 * Header and footer are configured in config.yml and support placeholders.
 * Updated at a configurable interval via a repeating task.
 */
public final class TabListManager {

    private final CozyHub plugin;
    private final ConfigManager configManager;

    /**
     * Creates a new TabListManager and starts the update task.
     *
     * @param plugin        the owning plugin
     * @param configManager the config manager
     */
    public TabListManager(CozyHub plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        startUpdateTask();
    }

    /**
     * Sets the tab list header and footer for the specified player.
     *
     * @param player the player
     */
    public void setTabList(Player player) {
        if (!configManager.getConfig().getBoolean("tablist.enabled", true)) {
            return;
        }

        String header = buildHeader(player);
        String footer = buildFooter(player);

        player.sendPlayerListHeaderAndFooter(
                LegacyComponentSerializer.legacySection().deserialize(header),
                LegacyComponentSerializer.legacySection().deserialize(footer)
        );
    }

    /**
     * Clears the tab list header and footer for the specified player.
     *
     * @param player the player
     */
    public void clearTabList(Player player) {
        player.sendPlayerListHeaderAndFooter(Component.empty(), Component.empty());
    }

    /**
     * Updates the tab list for all online players.
     */
    public void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            setTabList(player);
        }
    }

    private void startUpdateTask() {
        long interval = configManager.getConfig().getLong("tablist.update-interval", 40L);
        new BukkitRunnable() {
            @Override
            public void run() {
                updateAll();
            }
        }.runTaskTimer(plugin, interval, interval);
    }

    private String buildHeader(Player player) {
        List<String> lines = configManager.getConfig().getStringList("tablist.header");
        return joinLines(lines, player);
    }

    private String buildFooter(Player player) {
        List<String> lines = configManager.getConfig().getStringList("tablist.footer");
        return joinLines(lines, player);
    }

    private String joinLines(List<String> lines, Player player) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            String line = replacePlaceholders(lines.get(i), player);
            sb.append(line);
            if (i < lines.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private String replacePlaceholders(String text, Player player) {
        text = text.replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()));
        text = text.replace("{max}", String.valueOf(Bukkit.getMaxPlayers()));
        text = text.replace("{player}", player.getName());
        text = text.replace("{world}", player.getWorld().getName());
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
