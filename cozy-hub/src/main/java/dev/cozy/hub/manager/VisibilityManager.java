package dev.cozy.hub.manager;

import dev.cozy.core.ConfigManager;
import dev.cozy.hub.CozyHub;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Manages player visibility toggling.
 * <p>
 * Tracks which players have hidden others and applies showPlayer/hidePlayer
 * accordingly. Supports a default-hidden config option.
 */
public final class VisibilityManager {

    private final ConfigManager configManager;

    /** Maps player UUID to the set of UUIDs they have hidden. */
    private final Map<UUID, Set<UUID>> hiddenPlayers = new HashMap<>();

    /** Maps player UUID to their current visibility state (true = showing all). */
    private final Map<UUID, Boolean> visibilityState = new HashMap<>();

    /**
     * Creates a new VisibilityManager.
     *
     * @param configManager the config manager
     */
    public VisibilityManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Toggles the visibility state for the specified player.
     *
     * @param player the player toggling visibility
     */
    public void toggleVisibility(Player player) {
        UUID uuid = player.getUniqueId();
        boolean currentlyHidden = visibilityState.getOrDefault(uuid,
                !configManager.getConfig().getBoolean("visibility.default-hidden", false));
        visibilityState.put(uuid, !currentlyHidden);
        updateAllVisibilityFor(player);
    }

    /**
     * Returns whether the player currently has visibility disabled.
     *
     * @param uuid the player's UUID
     * @return true if the player's view is hidden
     */
    public boolean isCurrentlyHidden(UUID uuid) {
        return !visibilityState.getOrDefault(uuid,
                !configManager.getConfig().getBoolean("visibility.default-hidden", false));
    }

    /**
     * Hides the target player from the specified observer.
     *
     * @param observer the player who wants to hide
     * @param target   the player to hide
     */
    public void hidePlayer(Player observer, Player target) {
        UUID observerUUID = observer.getUniqueId();
        hiddenPlayers.computeIfAbsent(observerUUID, k -> new HashSet<>()).add(target.getUniqueId());
        observer.hidePlayer(getPlugin(), target);
    }

    /**
     * Shows the target player to the specified observer.
     *
     * @param observer the player who wants to show
     * @param target   the player to show
     */
    public void showPlayer(Player observer, Player target) {
        UUID observerUUID = observer.getUniqueId();
        Set<UUID> hidden = hiddenPlayers.get(observerUUID);
        if (hidden != null) {
            hidden.remove(target.getUniqueId());
        }
        observer.showPlayer(getPlugin(), target);
    }

    /**
     * Updates visibility of a target player for all observers.
     *
     * @param target the player whose visibility should be updated
     */
    public void updateVisibility(Player target) {
        for (Player observer : Bukkit.getOnlinePlayers()) {
            if (observer.getUniqueId().equals(target.getUniqueId())) continue;

            Set<UUID> hidden = hiddenPlayers.get(observer.getUniqueId());
            boolean shouldHide = hidden != null && hidden.contains(target.getUniqueId());

            if (shouldHide) {
                observer.hidePlayer(getPlugin(), target);
            } else {
                observer.showPlayer(getPlugin(), target);
            }
        }
    }

    /**
     * Applies visibility for a newly joined player based on all observers' settings.
     *
     * @param joinedPlayer the player who just joined
     */
    public void applyOnJoin(Player joinedPlayer) {
        for (Player observer : Bukkit.getOnlinePlayers()) {
            if (observer.getUniqueId().equals(joinedPlayer.getUniqueId())) continue;

            Set<UUID> hidden = hiddenPlayers.get(observer.getUniqueId());
            if (hidden != null && hidden.contains(joinedPlayer.getUniqueId())) {
                observer.hidePlayer(getPlugin(), joinedPlayer);
            }
        }
    }

    private void updateAllVisibilityFor(Player player) {
        UUID uuid = player.getUniqueId();
        boolean hidden = isCurrentlyHidden(uuid);

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.getUniqueId().equals(uuid)) continue;

            if (hidden) {
                player.hidePlayer(getPlugin(), target);
            } else {
                player.showPlayer(getPlugin(), target);
            }
        }
    }

    private JavaPlugin getPlugin() {
        return (JavaPlugin) Bukkit.getPluginManager().getPlugin("CozyHub");
    }
}
