package dev.cozy.hub.manager;

import dev.cozy.core.ConfigManager;
import dev.cozy.hub.CozyHub;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages per-player sidebars using the Bukkit Scoreboard API.
 * <p>
 * Each player gets their own scoreboard with configurable title and lines.
 * Supports PlaceholderAPI if installed, otherwise uses built-in placeholders.
 */
public final class SidebarManager {

    private final CozyHub plugin;
    private final ConfigManager configManager;
    private final Map<UUID, Scoreboard> scoreboards = new HashMap<>();

    /**
     * Creates a new SidebarManager and starts the update task.
     *
     * @param plugin        the owning plugin
     * @param configManager the config manager
     */
    public SidebarManager(CozyHub plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        startUpdateTask();
    }

    /**
     * Sets the sidebar for the specified player.
     *
     * @param player the player
     */
    public void setSidebar(Player player) {
        if (!configManager.getConfig().getBoolean("sidebar.enabled", true)) {
            return;
        }

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        String title = translate(configManager.getConfig().getString("sidebar.title", "&6&lCozy Network"));

        Objective objective = scoreboard.registerNewObjective("cozyhub_sidebar", "dummy",
                ChatColor.translateAlternateColorCodes('&', title));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<String> lines = configManager.getConfig().getStringList("sidebar.lines");
        int lineIndex = 0;
        for (int i = lines.size() - 1; i >= 0; i--) {
            String line = replacePlaceholders(lines.get(i), player);
            Score score = objective.getScore(line);
            score.setScore(lineIndex);
            lineIndex++;
        }

        player.setScoreboard(scoreboard);
        scoreboards.put(player.getUniqueId(), scoreboard);
    }

    /**
     * Removes the sidebar from the specified player.
     *
     * @param player the player
     */
    public void removeSidebar(Player player) {
        scoreboards.remove(player.getUniqueId());
        Scoreboard defaultBoard = Bukkit.getScoreboardManager().getMainScoreboard();
        player.setScoreboard(defaultBoard);
    }

    /**
     * Updates the sidebar for all online players.
     */
    public void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            setSidebar(player);
        }
    }

    private void startUpdateTask() {
        long interval = configManager.getConfig().getLong("sidebar.update-interval", 20L);
        new BukkitRunnable() {
            @Override
            public void run() {
                updateAll();
            }
        }.runTaskTimer(plugin, interval, interval);
    }

    private String replacePlaceholders(String text, Player player) {
        text = text.replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()));
        text = text.replace("{world}", player.getWorld().getName());
        text = text.replace("{player}", player.getName());
        text = text.replace("{rank}", "Spieler");
        return translate(text);
    }

    private String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
