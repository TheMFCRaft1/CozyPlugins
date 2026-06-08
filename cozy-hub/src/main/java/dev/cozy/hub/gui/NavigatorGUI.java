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
import java.util.List;
import java.util.Map;

/**
 * Server navigator GUI for BungeeCord/Velocity server switching.
 * <p>
 * Displays a chest inventory with configurable server items.
 * Clicking a server item sends a BungeeCord connect plugin message.
 * Implements {@link InventoryHolder} to distinguish CozyHub inventories from others.
 */
public final class NavigatorGUI implements InventoryHolder {

    private final ConfigManager configManager;
    private final Map<Integer, ServerData> serverSlots = new HashMap<>();
    private final Map<Inventory, Player> openInventories = new HashMap<>();

    /**
     * Creates a new NavigatorGUI.
     *
     * @param configManager the config manager
     */
    public NavigatorGUI(ConfigManager configManager) {
        this.configManager = configManager;
        loadServers();
    }

    /**
     * Loads server configurations from config.yml.
     */
    public void loadServers() {
        serverSlots.clear();
        ConfigurationSection section = configManager.getConfig().getConfigurationSection("servers");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection serverSection = section.getConfigurationSection(key);
            if (serverSection == null) continue;

            int slot = serverSection.getInt("slot", 0);
            String name = serverSection.getString("name", key);
            String materialName = serverSection.getString("material", "STONE");
            List<String> lore = serverSection.getStringList("lore");
            String server = serverSection.getString("server", key);
            String action = serverSection.getString("action", "SERVER_CONNECT");

            Material material = Material.matchMaterial(materialName);
            if (material == null) material = Material.STONE;

            ItemStack item = new ItemBuilder(material)
                    .name(name)
                    .lore(lore)
                    .build();

            serverSlots.put(slot, new ServerData(slot, item, server, action));
        }
    }

    /**
     * Opens the navigator GUI for the specified player.
     *
     * @param player the player to open the GUI for
     */
    public void open(Player player) {
        String title = configManager.getConfig().getString("navigator.title", "&6Server Navigator");
        int size = configManager.getConfig().getInt("navigator.size", 27);

        title = ChatColor.translateAlternateColorCodes('&', title);

        Inventory inventory = Bukkit.createInventory(this, size, title);

        for (ServerData data : serverSlots.values()) {
            inventory.setItem(data.slot(), data.item());
        }

        String fillMaterialName = configManager.getConfig().getString("navigator.fill-material", "GRAY_STAINED_GLASS_PANE");
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

        openInventories.put(inventory, player);
        player.openInventory(inventory);
    }

    /**
     * Returns the server data for the specified slot, or null if none.
     *
     * @param slot the inventory slot
     * @return the server data
     */
    public ServerData getServerAtSlot(int slot) {
        return serverSlots.get(slot);
    }

    /**
     * Checks if the inventory belongs to this NavigatorGUI.
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

    /**
     * Server data for a single GUI slot.
     *
     * @param slot   the slot number
     * @param item   the display item
     * @param server the target server name
     * @param action the action type (SERVER_CONNECT, RUN_COMMAND, CLOSE)
     */
    public record ServerData(int slot, ItemStack item, String server, String action) {
    }
}
