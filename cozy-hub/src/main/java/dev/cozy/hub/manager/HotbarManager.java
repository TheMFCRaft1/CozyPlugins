package dev.cozy.hub.manager;

import dev.cozy.core.ConfigManager;
import dev.cozy.hub.CozyHub;
import dev.cozy.hub.gui.NavigatorGUI;
import dev.cozy.hub.listener.HotbarListener;
import dev.cozy.hub.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the hub hotbar items displayed to players.
 * <p>
 * Items are configured in config.yml under {@code hotbar.items} and support
 * various actions such as opening the navigator, toggling visibility, disconnecting,
 * running commands, or opening URLs.
 */
public final class HotbarManager {

    private final CozyHub plugin;
    private final ConfigManager configManager;
    private final NavigatorGUI navigatorGUI;
    private final VisibilityManager visibilityManager;

    private final Map<Integer, HotbarItemData> hotbarItems = new HashMap<>();

    /**
     * Creates a new HotbarManager.
     *
     * @param plugin            the owning plugin
     * @param configManager     the config manager
     * @param navigatorGUI      the navigator GUI
     * @param visibilityManager the visibility manager
     */
    public HotbarManager(CozyHub plugin, ConfigManager configManager,
                         NavigatorGUI navigatorGUI, VisibilityManager visibilityManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.navigatorGUI = navigatorGUI;
        this.visibilityManager = visibilityManager;
        loadItems();
    }

    /**
     * Loads hotbar item configurations from config.yml.
     */
    public void loadItems() {
        hotbarItems.clear();
        ConfigurationSection section = configManager.getConfig().getConfigurationSection("hotbar.items");
        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection itemSection = section.getConfigurationSection(key);
            if (itemSection == null) continue;

            int slot = itemSection.getInt("slot", 0);
            String materialName = itemSection.getString("material", "COMPASS");
            String name = itemSection.getString("name", "");
            List<String> lore = itemSection.getStringList("lore");
            String actionName = itemSection.getString("action", "OPEN_NAVIGATOR");
            boolean glow = itemSection.getBoolean("glow", false);
            String command = itemSection.getString("command", "");

            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                material = Material.STONE;
            }

            HotbarAction action;
            try {
                action = HotbarAction.valueOf(actionName.toUpperCase());
            } catch (IllegalArgumentException e) {
                action = HotbarAction.OPEN_NAVIGATOR;
            }

            ItemStack item = new ItemBuilder(material)
                    .name(name)
                    .lore(lore)
                    .glow(glow)
                    .build();

            hotbarItems.put(slot, new HotbarItemData(slot, item, action, command));
        }
    }

    /**
     * Gives the configured hotbar items to the specified player.
     *
     * @param player the player to give items to
     */
    public void giveHotbarItems(Player player) {
        for (HotbarItemData data : hotbarItems.values()) {
            player.getInventory().setItem(data.slot(), data.item());
        }
    }

    /**
     * Removes all hotbar items from the specified player.
     *
     * @param player the player to remove items from
     */
    public void removeHotbarItems(Player player) {
        for (int slot : hotbarItems.keySet()) {
            if (slot >= 0 && slot < player.getInventory().getSize()) {
                player.getInventory().clear(slot);
            }
        }
    }

    /**
     * Handles a hotbar item interaction.
     *
     * @param player the player interacting
     * @param slot   the slot clicked
     */
    public void handleInteraction(Player player, int slot) {
        HotbarItemData data = hotbarItems.get(slot);
        if (data == null) return;

        switch (data.action()) {
            case OPEN_NAVIGATOR -> navigatorGUI.open(player);
            case TOGGLE_VISIBILITY -> {
                visibilityManager.toggleVisibility(player);
                boolean hidden = visibilityManager.isCurrentlyHidden(player.getUniqueId());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        hidden ? "&cSpieler ausgeblendet" : "&aSpieler eingeblendet"));
                updateVisibilityItem(player, hidden);
            }
            case DISCONNECT -> player.kickPlayer(ChatColor.translateAlternateColorCodes('&',
                    "&cDu wurdest gekickt!"));
            case RUN_COMMAND -> {
                if (!data.command().isEmpty()) {
                    player.performCommand(data.command().substring(1));
                }
            }
            case OPEN_URL -> {
            }
        }
    }

    private void updateVisibilityItem(Player player, boolean hidden) {
        ConfigurationSection section = configManager.getConfig()
                .getConfigurationSection("visibility." + (hidden ? "item-show" : "item-hide"));
        if (section == null) return;

        String materialName = section.getString("material", "ENDER_EYE");
        String name = section.getString("name", hidden ? "&aSpieler einblenden" : "&cSpieler ausblenden");
        int hotbarSlot = configManager.getConfig().getInt("visibility.hotbar-slot", 8);

        Material material = Material.matchMaterial(materialName);
        if (material == null) material = Material.ENDER_EYE;

        ItemStack item = new ItemBuilder(material)
                .name(name)
                .build();

        player.getInventory().setItem(hotbarSlot, item);
    }

    /**
     * Represents a parsed hotbar item with its associated action.
     *
     * @param slot    the inventory slot
     * @param item    the item stack
     * @param action  the action to perform
     * @param command the command for RUN_COMMAND action
     */
    public record HotbarItemData(int slot, ItemStack item, HotbarAction action, String command) {
    }

    /**
     * Available actions for hotbar items.
     */
    public enum HotbarAction {
        OPEN_NAVIGATOR,
        TOGGLE_VISIBILITY,
        DISCONNECT,
        RUN_COMMAND,
        OPEN_URL
    }
}
