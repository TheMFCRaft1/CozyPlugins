package dev.cozy.hub.gui;

import dev.cozy.core.ConfigManager;
import dev.cozy.hub.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Settings GUI placeholder for future extension.
 * <p>
 * Uses the same base structure as {@link NavigatorGUI}.
 * Currently only displays placeholder items.
 * Implements {@link InventoryHolder} to distinguish CozyHub inventories.
 */
public final class SettingsGUI implements InventoryHolder {

    private final ConfigManager configManager;
    private final Map<Integer, ItemStack> placeholderItems = new HashMap<>();

    /**
     * Creates a new SettingsGUI.
     *
     * @param configManager the config manager
     */
    public SettingsGUI(ConfigManager configManager) {
        this.configManager = configManager;
        loadPlaceholders();
    }

    /**
     * Loads placeholder items from config.
     */
    public void loadPlaceholders() {
        placeholderItems.clear();
        ConfigurationSection section = configManager.getConfig().getConfigurationSection("settings-gui.placeholders");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection itemSection = section.getConfigurationSection(key);
            if (itemSection == null) continue;

            int slot = itemSection.getInt("slot", 0);
            String materialName = itemSection.getString("material", "STONE");
            String name = itemSection.getString("name", "&7Placeholder");

            Material material = Material.matchMaterial(materialName);
            if (material == null) material = Material.STONE;

            ItemStack item = new ItemBuilder(material)
                    .name(name)
                    .build();

            placeholderItems.put(slot, item);
        }
    }

    /**
     * Opens the settings GUI for the specified player.
     *
     * @param player the player to open the GUI for
     */
    public void open(Player player) {
        String title = configManager.getConfig().getString("settings-gui.title", "&6Settings");
        int size = configManager.getConfig().getInt("settings-gui.size", 27);

        title = ChatColor.translateAlternateColorCodes('&', title);

        Inventory inventory = Bukkit.createInventory(this, size, title);

        for (Map.Entry<Integer, ItemStack> entry : placeholderItems.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue());
        }

        String fillMaterialName = configManager.getConfig().getString("settings-gui.fill-material", "GRAY_STAINED_GLASS_PANE");
        Material fillMaterial = Material.matchMaterial(fillMaterialName);
        if (fillMaterial == null) fillMaterial = Material.GRAY_STAINED_GLASS_PANE;

        ItemStack fillItem = new ItemBuilder(fillMaterial)
                .name(" ")
                .build();

        for (int i = 0; i < size; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, fillItem);
            }
        }

        player.openInventory(inventory);
    }

    /**
     * Checks if the inventory belongs to this SettingsGUI.
     *
     * @param inventory the inventory to check
     * @return true if this GUI owns the inventory
     */
    public boolean isOurInventory(Inventory inventory) {
        return inventory.getHolder() == this;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
