package dev.cozy.hub;

import dev.cozy.core.ConfigManager;
import dev.cozy.core.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the /hub command with subcommands setspawn and reload.
 */
public final class HubCommand implements CommandExecutor {

    private final CozyHub plugin;
    private final SpawnManager spawnManager;
    private final ConfigManager configManager;
    private final MessageManager messageManager;

    /**
     * Creates a new HubCommand.
     *
     * @param plugin         the owning plugin
     * @param spawnManager   the spawn manager
     * @param configManager  the config manager
     * @param messageManager the message manager
     */
    public HubCommand(CozyHub plugin, SpawnManager spawnManager, ConfigManager configManager, MessageManager messageManager) {
        this.plugin = plugin;
        this.spawnManager = spawnManager;
        this.configManager = configManager;
        this.messageManager = messageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return handleTeleport(sender);
        }

        switch (args[0].toLowerCase()) {
            case "setspawn":
                return handleSetSpawn(sender);
            case "reload":
                return handleReload(sender);
            default:
                messageManager.send(sender, "command.hub-usage");
                return true;
        }
    }

    /**
     * Teleports the sender to the hub spawn.
     */
    private boolean handleTeleport(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (!spawnManager.teleportToSpawn(player)) {
            messageManager.send(sender, "command.spawn-not-set");
            return true;
        }

        messageManager.send(sender, "command.teleported");
        return true;
    }

    /**
     * Sets the hub spawn to the sender's current location.
     */
    private boolean handleSetSpawn(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (!sender.hasPermission("cozyhub.setspawn")) {
            messageManager.send(sender, "command.no-permission");
            return true;
        }

        spawnManager.setSpawn(player.getLocation());
        messageManager.send(sender, "command.spawn-set");
        return true;
    }

    /**
     * Reloads the plugin configuration.
     */
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("cozyhub.admin")) {
            messageManager.send(sender, "command.no-permission");
            return true;
        }

        configManager.reload();
        messageManager.reload();
        messageManager.send(sender, "command.reloaded");
        return true;
    }
}
