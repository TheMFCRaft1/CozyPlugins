package dev.cozy.core;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Manages loading and sending of translatable messages from messages.yml.
 */
public final class MessageManager {

    private final ConfigManager configManager;
    private String prefix;

    /**
     * Creates a new MessageManager that loads messages from "messages.yml".
     *
     * @param configManager the ConfigManager to use for loading messages.yml
     */
    public MessageManager(ConfigManager configManager) {
        this.configManager = configManager;
        loadPrefix();
    }

    /**
     * Loads the prefix value from messages.yml.
     */
    private void loadPrefix() {
        this.prefix = configManager.getConfig().getString("prefix", "&8[&aCo&2zy&8] &r");
    }

    /**
     * Reloads the messages configuration and refreshes the prefix.
     */
    public void reload() {
        configManager.reload();
        loadPrefix();
    }

    /**
     * Returns the translated message for the given key.
     *
     * @param key the configuration key
     * @return the translated message, or the key itself if not found
     */
    public String get(String key) {
        String raw = configManager.getConfig().getString(key, key);
        return ChatColor.translateAlternateColorCodes('&', raw);
    }

    /**
     * Sends the translated message for the given key to the specified sender.
     * The prefix is prepended to the message.
     *
     * @param sender the command sender to message
     * @param key    the configuration key
     */
    public void send(CommandSender sender, String key) {
        String translatedPrefix = ChatColor.translateAlternateColorCodes('&', prefix);
        sender.sendMessage(translatedPrefix + get(key));
    }
}
