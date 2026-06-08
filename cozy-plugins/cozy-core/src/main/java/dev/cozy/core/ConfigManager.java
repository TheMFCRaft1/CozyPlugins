package dev.cozy.core;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

/**
 * Manages loading and reloading of configuration files for a plugin.
 */
public final class ConfigManager {

    private final JavaPlugin plugin;
    private final File dataFolder;
    private final File configFile;
    private FileConfiguration config;

    /**
     * Creates a new ConfigManager for the specified file.
     *
     * @param plugin   the owning plugin
     * @param fileName the name of the configuration file (e.g. "config.yml")
     */
    public ConfigManager(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
        this.configFile = new File(dataFolder, fileName);

        if (!configFile.exists()) {
            saveDefaultConfig(fileName);
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Saves the default configuration from the plugin's jar resources.
     *
     * @param fileName the resource file name
     */
    private void saveDefaultConfig(String fileName) {
        dataFolder.mkdirs();
        try (InputStream in = plugin.getResource(fileName)) {
            if (in == null) {
                plugin.getLogger().log(Level.WARNING, "Default resource not found: {0}", fileName);
                return;
            }
            Files.copy(in, configFile.toPath());
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save default config: " + fileName, e);
        }
    }

    /**
     * Reloads the configuration from disk.
     */
    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Returns the current {@link FileConfiguration}.
     *
     * @return the loaded configuration
     */
    public FileConfiguration getConfig() {
        return config;
    }
}
