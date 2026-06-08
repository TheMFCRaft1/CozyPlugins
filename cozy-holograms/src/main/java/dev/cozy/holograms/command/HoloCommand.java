package dev.cozy.holograms.command;

import dev.cozy.core.ConfigManager;
import dev.cozy.core.MessageManager;
import dev.cozy.holograms.CozyHolograms;
import dev.cozy.holograms.hologram.Hologram;
import dev.cozy.holograms.manager.HologramManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the /holo command with all subcommands.
 * <p>
 * Supports: create, delete, edit, addline, removeline, list, teleport, reload, info.
 */
public final class HoloCommand implements CommandExecutor, TabCompleter {

    private final CozyHolograms plugin;
    private final HologramManager hologramManager;
    private final MessageManager messageManager;

    /**
     * Creates a new HoloCommand.
     *
     * @param plugin         the owning plugin
     * @param hologramManager the hologram manager
     * @param messageManager the message manager
     */
    public HoloCommand(CozyHolograms plugin, HologramManager hologramManager, MessageManager messageManager) {
        this.plugin = plugin;
        this.hologramManager = hologramManager;
        this.messageManager = messageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            messageManager.send(sender, "command.holo-usage");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> {
                if (!player.hasPermission("cozyholo.create")) {
                    messageManager.send(sender, "command.no-permission");
                    return true;
                }
                return handleCreate(player, args);
            }
            case "delete" -> {
                if (!player.hasPermission("cozyholo.delete")) {
                    messageManager.send(sender, "command.no-permission");
                    return true;
                }
                return handleDelete(player, args);
            }
            case "edit" -> {
                if (!player.hasPermission("cozyholo.edit")) {
                    messageManager.send(sender, "command.no-permission");
                    return true;
                }
                return handleEdit(player, args);
            }
            case "addline" -> {
                if (!player.hasPermission("cozyholo.edit")) {
                    messageManager.send(sender, "command.no-permission");
                    return true;
                }
                return handleAddLine(player, args);
            }
            case "removeline" -> {
                if (!player.hasPermission("cozyholo.edit")) {
                    messageManager.send(sender, "command.no-permission");
                    return true;
                }
                return handleRemoveLine(player, args);
            }
            case "list" -> {
                if (!player.hasPermission("cozyholo.list")) {
                    messageManager.send(sender, "command.no-permission");
                    return true;
                }
                return handleList(player);
            }
            case "teleport", "tp" -> {
                if (!player.hasPermission("cozyholo.edit")) {
                    messageManager.send(sender, "command.no-permission");
                    return true;
                }
                return handleTeleport(player, args);
            }
            case "reload" -> {
                if (!player.hasPermission("cozyholo.admin")) {
                    messageManager.send(sender, "command.no-permission");
                    return true;
                }
                return handleReload(player);
            }
            case "info" -> {
                if (!player.hasPermission("cozyholo.list")) {
                    messageManager.send(sender, "command.no-permission");
                    return true;
                }
                return handleInfo(player, args);
            }
            default -> {
                messageManager.send(sender, "command.holo-usage");
                return true;
            }
        }
    }

    private boolean handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            messageManager.send(player, "command.holo-create-usage");
            return true;
        }

        String id = args[1];
        if (hologramManager.getHologram(id).isPresent()) {
            messageManager.send(player, "command.holo-already-exists");
            return true;
        }

        Hologram holo = hologramManager.createHologram(id, player.getLocation());
        holo.addLine("&6" + id);
        messageManager.send(player, "command.holo-created");
        return true;
    }

    private boolean handleDelete(Player player, String[] args) {
        if (args.length < 2) {
            messageManager.send(player, "command.holo-delete-usage");
            return true;
        }

        String id = args[1];
        if (!hologramManager.deleteHologram(id)) {
            messageManager.send(player, "command.holo-not-found");
            return true;
        }

        messageManager.send(player, "command.holo-deleted");
        return true;
    }

    private boolean handleEdit(Player player, String[] args) {
        if (args.length < 4) {
            messageManager.send(player, "command.holo-edit-usage");
            return true;
        }

        String id = args[1];
        var holo = hologramManager.getHologram(id);
        if (holo.isEmpty()) {
            messageManager.send(player, "command.holo-not-found");
            return true;
        }

        int lineIndex;
        try {
            lineIndex = Integer.parseInt(args[2]) - 1;
        } catch (NumberFormatException e) {
            messageManager.send(player, "command.holo-invalid-line");
            return true;
        }

        if (lineIndex < 0 || lineIndex >= holo.get().getLines().size()) {
            messageManager.send(player, "command.holo-invalid-line");
            return true;
        }

        String text = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        holo.get().setLine(lineIndex, text);
        hologramManager.saveAll();
        messageManager.send(player, "command.holo-edited");
        return true;
    }

    private boolean handleAddLine(Player player, String[] args) {
        if (args.length < 3) {
            messageManager.send(player, "command.holo-addline-usage");
            return true;
        }

        String id = args[1];
        var holo = hologramManager.getHologram(id);
        if (holo.isEmpty()) {
            messageManager.send(player, "command.holo-not-found");
            return true;
        }

        String text = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        holo.get().addLine(text);
        hologramManager.saveAll();
        messageManager.send(player, "command.holo-line-added");
        return true;
    }

    private boolean handleRemoveLine(Player player, String[] args) {
        if (args.length < 3) {
            messageManager.send(player, "command.holo-removeline-usage");
            return true;
        }

        String id = args[1];
        var holo = hologramManager.getHologram(id);
        if (holo.isEmpty()) {
            messageManager.send(player, "command.holo-not-found");
            return true;
        }

        int lineIndex;
        try {
            lineIndex = Integer.parseInt(args[2]) - 1;
        } catch (NumberFormatException e) {
            messageManager.send(player, "command.holo-invalid-line");
            return true;
        }

        if (lineIndex < 0 || lineIndex >= holo.get().getLines().size()) {
            messageManager.send(player, "command.holo-invalid-line");
            return true;
        }

        holo.get().removeLine(lineIndex);
        hologramManager.saveAll();
        messageManager.send(player, "command.holo-line-removed");
        return true;
    }

    private boolean handleList(Player player) {
        var holograms = hologramManager.getAllHolograms();
        if (holograms.isEmpty()) {
            messageManager.send(player, "command.holo-list-empty");
            return true;
        }

        messageManager.send(player, "command.holo-list-header");
        for (Hologram holo : holograms) {
            String msg = ChatColor.GRAY + "- " + ChatColor.GREEN + holo.getId()
                    + ChatColor.GRAY + " | " + ChatColor.WHITE + holo.getLocation().getWorld().getName()
                    + ChatColor.GRAY + " | " + ChatColor.YELLOW + holo.getLines().size() + " lines";
            player.sendMessage(msg);
        }
        return true;
    }

    private boolean handleTeleport(Player player, String[] args) {
        if (args.length < 2) {
            messageManager.send(player, "command.holo-teleport-usage");
            return true;
        }

        String id = args[1];
        var holo = hologramManager.getHologram(id);
        if (holo.isEmpty()) {
            messageManager.send(player, "command.holo-not-found");
            return true;
        }

        player.teleport(holo.get().getLocation());
        messageManager.send(player, "command.holo-teleported");
        return true;
    }

    private boolean handleReload(Player player) {
        hologramManager.stopUpdateTask();
        hologramManager.despawnAll();
        plugin.reloadConfig();
        hologramManager.loadAll();
        hologramManager.startUpdateTask();
        messageManager.send(player, "command.holo-reloaded");
        return true;
    }

    private boolean handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            messageManager.send(player, "command.holo-info-usage");
            return true;
        }

        String id = args[1];
        var holo = hologramManager.getHologram(id);
        if (holo.isEmpty()) {
            messageManager.send(player, "command.holo-not-found");
            return true;
        }

        Hologram h = holo.get();
        player.sendMessage(ChatColor.GREEN + "=== Hologram: " + h.getId() + " ===");
        player.sendMessage(ChatColor.GRAY + "World: " + ChatColor.WHITE + h.getLocation().getWorld().getName());
        player.sendMessage(ChatColor.GRAY + "X: " + ChatColor.WHITE + h.getLocation().getX());
        player.sendMessage(ChatColor.GRAY + "Y: " + ChatColor.WHITE + h.getLocation().getY());
        player.sendMessage(ChatColor.GRAY + "Z: " + ChatColor.WHITE + h.getLocation().getZ());
        player.sendMessage(ChatColor.GRAY + "Lines: " + ChatColor.WHITE + h.getLines().size());
        player.sendMessage(ChatColor.GRAY + "Persistent: " + ChatColor.WHITE + h.isPersistent());
        player.sendMessage(ChatColor.GRAY + "Animated: " + ChatColor.WHITE + h.isAnimated());

        for (int i = 0; i < h.getLines().size(); i++) {
            player.sendMessage(ChatColor.GRAY + "  Line " + (i + 1) + ": " + ChatColor.WHITE + h.getLines().get(i).getRawText());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("create", "delete", "edit", "addline", "removeline",
                    "list", "teleport", "tp", "reload", "info"));
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("delete") || sub.equals("edit") || sub.equals("addline")
                    || sub.equals("removeline") || sub.equals("teleport") || sub.equals("tp")
                    || sub.equals("info")) {
                completions.addAll(hologramManager.getAllHolograms().stream()
                        .map(Hologram::getId)
                        .collect(Collectors.toList()));
            }
        } else if (args.length == 3) {
            String sub = args[0].toLowerCase();
            if (sub.equals("edit") || sub.equals("removeline")) {
                var holo = hologramManager.getHologram(args[1]);
                holo.ifPresent(h -> {
                    for (int i = 1; i <= h.getLines().size(); i++) {
                        completions.add(String.valueOf(i));
                    }
                });
            }
        }

        String lastArg = args[args.length - 1].toLowerCase();
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(lastArg))
                .collect(Collectors.toList());
    }
}
