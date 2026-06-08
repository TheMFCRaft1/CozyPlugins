package dev.cozy.hub;

import dev.cozy.core.ConfigManager;
import dev.cozy.core.MessageManager;
import dev.cozy.core.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;

public final class CozyHub extends JavaPlugin {

    private ConfigManager configManager;
    private MessageManager messageManager;
    private SpawnManager spawnManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this, "config.yml");
        messageManager = new MessageManager(new ConfigManager(this, "messages.yml"));
        spawnManager = new SpawnManager(configManager);

        getServer().getPluginManager().registerEvents(
                new HubListener(this, spawnManager, messageManager), this
        );
        getCommand("hub").setExecutor(
                new HubCommand(this, spawnManager, configManager, messageManager)
        );

        new UpdateChecker(this, "PLACEHOLDER").check();

        getLogger().info("CozyHub has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("CozyHub has been disabled!");
    }
}
