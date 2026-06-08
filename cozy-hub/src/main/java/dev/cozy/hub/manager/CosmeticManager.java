package dev.cozy.hub.manager;

import dev.cozy.core.ConfigManager;
import dev.cozy.hub.CozyHub;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages cosmetic effects for player join events.
 * <p>
 * Supports configurable firework effects and join sounds, both togglable via config.
 */
public final class CosmeticManager {

    private final CozyHub plugin;
    private final ConfigManager configManager;

    private static final Map<String, Integer> COLOR_MAP;

    static {
        COLOR_MAP = new HashMap<>();
        COLOR_MAP.put("RED", 0xFF0000);
        COLOR_MAP.put("AQUA", 0x00FFFF);
        COLOR_MAP.put("WHITE", 0xFFFFFF);
        COLOR_MAP.put("BLUE", 0x0000FF);
        COLOR_MAP.put("GREEN", 0x00FF00);
        COLOR_MAP.put("YELLOW", 0xFFFF00);
        COLOR_MAP.put("PURPLE", 0x800080);
        COLOR_MAP.put("ORANGE", 0xFFA500);
        COLOR_MAP.put("PINK", 0xFFC0CB);
        COLOR_MAP.put("LIME", 0x00FF00);
        COLOR_MAP.put("CYAN", 0x00FFFF);
        COLOR_MAP.put("MAGENTA", 0xFF00FF);
    }

    /**
     * Creates a new CosmeticManager.
     *
     * @param plugin        the owning plugin
     * @param configManager the config manager
     */
    public CosmeticManager(CozyHub plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    /**
     * Spawns a firework at the player's location if configured.
     *
     * @param player the player
     */
    public void spawnJoinFirework(Player player) {
        ConfigurationSection section = configManager.getConfig()
                .getConfigurationSection("cosmetics.join-firework");
        if (section == null || !section.getBoolean("enabled", false)) {
            return;
        }

        int power = section.getInt("power", 1);
        boolean trail = section.getBoolean("trail", true);
        boolean flicker = section.getBoolean("flicker", false);

        List<String> colorNames = section.getStringList("colors");
        List<String> fadeColorNames = section.getStringList("fade-colors");

        FireworkEffect.Builder builder = FireworkEffect.builder();
        builder.trail(trail);
        builder.flicker(flicker);

        for (String colorName : colorNames) {
            Integer rgb = COLOR_MAP.get(colorName.toUpperCase());
            if (rgb != null) {
                builder.withColor(org.bukkit.Color.fromRGB(rgb));
            }
        }

        for (String fadeName : fadeColorNames) {
            Integer rgb = COLOR_MAP.get(fadeName.toUpperCase());
            if (rgb != null) {
                builder.withFade(org.bukkit.Color.fromRGB(rgb));
            }
        }

        String typeName = section.getString("type", "BALL_LARGE");
        try {
            builder.with(FireworkEffect.Type.valueOf(typeName.toUpperCase()));
        } catch (IllegalArgumentException e) {
            builder.with(FireworkEffect.Type.BALL_LARGE);
        }

        Firework firework = (Firework) player.getWorld().spawnEntity(
                player.getLocation(), EntityType.FIREWORK_ROCKET);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(builder.build());
        meta.setPower(power);
        firework.setFireworkMeta(meta);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!firework.isDead()) {
                    firework.detonate();
                }
            }
        }.runTaskLater(plugin, 2L);
    }

    /**
     * Plays the join sound at the player's location if configured.
     *
     * @param player the player
     */
    public void playJoinSound(Player player) {
        ConfigurationSection section = configManager.getConfig()
                .getConfigurationSection("cosmetics.join-sound");
        if (section == null || !section.getBoolean("enabled", false)) {
            return;
        }

        String soundName = section.getString("sound", "ENTITY_PLAYER_LEVELUP");
        float volume = (float) section.getDouble("volume", 1.0);
        float pitch = (float) section.getDouble("pitch", 1.0);

        try {
            org.bukkit.Sound sound = org.bukkit.Sound.valueOf(soundName.toUpperCase());
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid sound in config: " + soundName);
        }
    }

    /**
     * Applies all configured join cosmetics to the player.
     *
     * @param player the player
     */
    public void applyJoinEffects(Player player) {
        spawnJoinFirework(player);
        playJoinSound(player);
    }
}
