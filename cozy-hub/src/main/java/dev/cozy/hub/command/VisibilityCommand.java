package dev.cozy.hub.command;

import dev.cozy.core.MessageManager;
import dev.cozy.hub.manager.VisibilityManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the /visibility command to toggle player visibility.
 * <p>
 * Requires {@code cozyhub.visibility} permission.
 */
public final class VisibilityCommand implements CommandExecutor {

    private final VisibilityManager visibilityManager;
    private final MessageManager messageManager;

    /**
     * Creates a new VisibilityCommand.
     *
     * @param visibilityManager the visibility manager
     * @param messageManager    the message manager
     */
    public VisibilityCommand(VisibilityManager visibilityManager, MessageManager messageManager) {
        this.visibilityManager = visibilityManager;
        this.messageManager = messageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (!sender.hasPermission("cozyhub.visibility")) {
            messageManager.send(sender, "command.no-permission");
            return true;
        }

        visibilityManager.toggleVisibility(player);

        boolean hidden = visibilityManager.isCurrentlyHidden(player.getUniqueId());
        String messageKey = hidden ? "visibility-hidden" : "visibility-shown";
        messageManager.send(sender, messageKey);

        return true;
    }
}
