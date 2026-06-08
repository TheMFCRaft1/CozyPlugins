package dev.cozy.hub;

import dev.cozy.core.ConfigManager;
import dev.cozy.core.MessageManager;
import dev.cozy.core.UpdateChecker;
import dev.cozy.hub.command.HubCommand;
import dev.cozy.hub.command.VisibilityCommand;
import dev.cozy.hub.gui.GUIListener;
import dev.cozy.hub.gui.NavigatorGUI;
import dev.cozy.hub.gui.SettingsGUI;
import dev.cozy.hub.listener.HotbarListener;
import dev.cozy.hub.listener.MovementListener;
import dev.cozy.hub.listener.PlayerJoinListener;
import dev.cozy.hub.listener.PlayerQuitListener;
import dev.cozy.hub.listener.ProtectionListener;
import dev.cozy.hub.manager.CosmeticManager;
import dev.cozy.hub.manager.HotbarManager;
import dev.cozy.hub.manager.SidebarManager;
import dev.cozy.hub.manager.SpawnManager;
import dev.cozy.hub.manager.TabListManager;
import dev.cozy.hub.manager.VisibilityManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for CozyHub.
 * <p>
 * Initializes all managers, registers all listeners and commands,
 * and starts the update checker.
 */
public final class CozyHub extends JavaPlugin {

    private ConfigManager configManager;
    private MessageManager messageManager;

    private SpawnManager spawnManager;
    private HotbarManager hotbarManager;
    private SidebarManager sidebarManager;
    private TabListManager tabListManager;
    private CosmeticManager cosmeticManager;
    private VisibilityManager visibilityManager;

    private NavigatorGUI navigatorGUI;
    private SettingsGUI settingsGUI;

    /**
     * Called when the plugin is enabled.
     * Initializes all components and registers event listeners and commands.
     */
    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);

        configManager = new ConfigManager(this, "config.yml");
        messageManager = new MessageManager(new ConfigManager(this, "messages.yml"));

        MovementListener movementListener = new MovementListener(this, configManager);
        spawnManager = new SpawnManager(this, configManager, movementListener);
        visibilityManager = new VisibilityManager(configManager);
        navigatorGUI = new NavigatorGUI(configManager);
        settingsGUI = new SettingsGUI(configManager);
        hotbarManager = new HotbarManager(this, configManager, navigatorGUI, visibilityManager);
        sidebarManager = new SidebarManager(this, configManager);
        tabListManager = new TabListManager(this, configManager);
        cosmeticManager = new CosmeticManager(this, configManager);

        getServer().getPluginManager().registerEvents(
                new PlayerJoinListener(this, spawnManager, messageManager, hotbarManager,
                        tabListManager, cosmeticManager, visibilityManager, configManager), this);
        getServer().getPluginManager().registerEvents(
                new PlayerQuitListener(this, messageManager, sidebarManager, tabListManager, hotbarManager), this);
        getServer().getPluginManager().registerEvents(
                new ProtectionListener(configManager), this);
        getServer().getPluginManager().registerEvents(
                new HotbarListener(this, hotbarManager), this);
        getServer().getPluginManager().registerEvents(movementListener, this);
        getServer().getPluginManager().registerEvents(
                new GUIListener(navigatorGUI, settingsGUI), this);

        getCommand("hub").setExecutor(
                new HubCommand(this, spawnManager, configManager, messageManager));
        getCommand("visibility").setExecutor(
                new VisibilityCommand(visibilityManager, messageManager));

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        new UpdateChecker(this, "cozyhub").check();

        getLogger().info("CozyHub has been enabled!");
    }

    /**
     * Called when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        getLogger().info("CozyHub has been disabled!");
    }
}
