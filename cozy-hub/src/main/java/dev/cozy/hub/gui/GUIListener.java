package dev.cozy.hub.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Listener for all CozyHub GUI interactions.
 * <p>
 * Checks if the clicked inventory is owned by a CozyHub GUI holder
 * and delegates the click action accordingly.
 */
public final class GUIListener implements Listener {

    private final NavigatorGUI navigatorGUI;
    private final SettingsGUI settingsGUI;

    /**
     * Creates a new GUIListener.
     *
     * @param navigatorGUI the navigator GUI
     * @param settingsGUI  the settings GUI
     */
    public GUIListener(NavigatorGUI navigatorGUI, SettingsGUI settingsGUI) {
        this.navigatorGUI = navigatorGUI;
        this.settingsGUI = settingsGUI;
    }

    /**
     * Handles InventoryClickEvent for CozyHub inventories.
     *
     * @param event the inventory click event
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        if (holder instanceof NavigatorGUI) {
            event.setCancelled(true);

            if (!(event.getWhoClicked() instanceof Player player)) return;
            int slot = event.getRawSlot();

            if (slot < 0 || slot >= inventory.getSize()) return;

            NavigatorGUI.ServerData data = navigatorGUI.getServerAtSlot(slot);
            if (data == null) return;

            switch (data.action()) {
                case "SERVER_CONNECT" -> connectToServer(player, data.server());
                case "RUN_COMMAND" -> player.performCommand(data.server());
                case "CLOSE" -> player.closeInventory();
            }
        } else if (holder instanceof SettingsGUI) {
            event.setCancelled(true);
        }
    }

    /**
     * Handles InventoryCloseEvent to clean up references.
     *
     * @param event the inventory close event
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
    }

    private void connectToServer(Player player, String server) {
        player.closeInventory();
        try {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            java.io.DataOutputStream dos = new java.io.DataOutputStream(out);
            dos.writeUTF("Connect");
            dos.writeUTF(server);
            org.bukkit.plugin.Plugin plugin = org.bukkit.Bukkit.getPluginManager().getPlugin("CozyHub");
            if (plugin != null) {
                player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
            }
        } catch (Exception e) {
            player.sendMessage(org.bukkit.ChatColor.RED + "Could not connect to server: " + server);
        }
    }
}
