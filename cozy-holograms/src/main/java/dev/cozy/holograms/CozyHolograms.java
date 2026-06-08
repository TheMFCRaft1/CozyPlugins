package dev.cozy.holograms;

import dev.cozy.core.ConfigManager;
import dev.cozy.core.MessageManager;
import dev.cozy.core.UpdateChecker;
import dev.cozy.holograms.api.CozyHologramsAPI;
import dev.cozy.holograms.command.HoloCommand;
import dev.cozy.holograms.listener.PlayerListener;
import dev.cozy.holograms.manager.HologramManager;
import dev.cozy.holograms.util.HologramSerializer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for CozyHolograms.
 * <p>
 * Manages hologram lifecycle, configuration, commands,
 * and PlaceholderAPI integration.
 */
public final class CozyHolograms extends JavaPlugin {

    private ConfigManager configManager;
    private MessageManager messageManager;
    private HologramManager hologramManager;
    private HologramSerializer serializer;
    private boolean placeholderAPIEnabled;

    /**
     * Called when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);
        saveResource("holograms.yml", false);

        configManager = new ConfigManager(this, "config.yml");
        messageManager = new MessageManager(new ConfigManager(this, "messages.yml"));

        serializer = new HologramSerializer(getDataFolder());
        hologramManager = new HologramManager(this, serializer);
        hologramManager.loadAll();
        hologramManager.startUpdateTask();

        CozyHologramsAPI.init(hologramManager);

        placeholderAPIEnabled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        if (placeholderAPIEnabled) {
            getLogger().info("PlaceholderAPI detected - placeholder support enabled.");
        }

        HoloCommand holoCommand = new HoloCommand(this, hologramManager, messageManager);
        getCommand("holo").setExecutor(holoCommand);
        getCommand("holo").setTabCompleter(holoCommand);

        getServer().getPluginManager().registerEvents(new PlayerListener(hologramManager), this);

        new UpdateChecker(this, "cozyholograms").check();

        getLogger().info("CozyHolograms enabled - " + hologramManager.getAllHolograms().size() + " holograms loaded.");
    }

    /**
     * Called when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        hologramManager.stopUpdateTask();
        hologramManager.saveAll();
        hologramManager.despawnAll();

        getLogger().info("CozyHolograms disabled.");
    }

    /**
     * Returns whether PlaceholderAPI is enabled.
     *
     * @return true if PlaceholderAPI is installed
     */
    public boolean isPlaceholderAPIEnabled() {
        return placeholderAPIEnabled;
    }

    /**
     * Returns the hologram manager.
     *
     * @return the HologramManager
     */
    public HologramManager getHologramManager() {
        return hologramManager;
    }

    /**
     * Returns the message manager.
     *
     * @return the MessageManager
     */
    public MessageManager getMessageManager() {
        return messageManager;
    }
}
