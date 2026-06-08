package dev.cozy.holograms.hologram;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a single line within a hologram.
 * <p>
 * Each line is backed by a {@link TextDisplay} entity with configurable
 * billboard, background, shadow, and alignment settings.
 */
public final class HologramLine {

    private static final Map<Character, String> COLOR_CODES = new HashMap<>();

    static {
        COLOR_CODES.put('0', "<black>");
        COLOR_CODES.put('1', "<dark_blue>");
        COLOR_CODES.put('2', "<dark_green>");
        COLOR_CODES.put('3', "<dark_aqua>");
        COLOR_CODES.put('4', "<dark_red>");
        COLOR_CODES.put('5', "<dark_purple>");
        COLOR_CODES.put('6', "<gold>");
        COLOR_CODES.put('7', "<gray>");
        COLOR_CODES.put('8', "<dark_gray>");
        COLOR_CODES.put('9', "<blue>");
        COLOR_CODES.put('a', "<green>");
        COLOR_CODES.put('b', "<aqua>");
        COLOR_CODES.put('c', "<red>");
        COLOR_CODES.put('d', "<light_purple>");
        COLOR_CODES.put('e', "<yellow>");
        COLOR_CODES.put('f', "<white>");
        COLOR_CODES.put('k', "<obfuscated>");
        COLOR_CODES.put('l', "<bold>");
        COLOR_CODES.put('m', "<strikethrough>");
        COLOR_CODES.put('n', "<underlined>");
        COLOR_CODES.put('o', "<italic>");
        COLOR_CODES.put('r', "<reset>");
    }

    private String rawText;
    private TextDisplay entity;
    private int lineIndex;
    private HologramAnimation animation;

    /**
     * Creates a new HologramLine.
     *
     * @param rawText   the raw text (supports MiniMessage and color codes)
     * @param lineIndex the zero-based line index
     */
    public HologramLine(String rawText, int lineIndex) {
        this.rawText = rawText;
        this.lineIndex = lineIndex;
    }

    /**
     * Spawns the TextDisplay entity at the specified location.
     *
     * @param location the location to spawn at
     */
    public void spawn(Location location) {
        if (entity != null && !entity.isDead()) {
            return;
        }

        entity = (TextDisplay) location.getWorld().spawnEntity(
                location, EntityType.TEXT_DISPLAY);

        applySettings();
        update(rawText);
    }

    /**
     * Applies default TextDisplay settings from config.
     */
    private void applySettings() {
        if (entity == null) return;

        entity.setBillboard(Display.Billboard.CENTER);
        entity.setBackgroundColor(Color.fromARGB(0x00000000));
        entity.setShadowed(true);
        entity.setSeeThrough(false);
        entity.setDisplayWidth(0.0f);
        entity.setDisplayHeight(0.0f);

        Transformation transformation = new Transformation(
                new Vector3f(0, 0, 0),
                new AxisAngle4f(0, 0, 0, 1),
                new Vector3f(1, 1, 1),
                new AxisAngle4f(0, 0, 0, 1)
        );
        entity.setTransformation(transformation);
    }

    /**
     * Updates the displayed text.
     *
     * @param text the new text
     */
    public void update(String text) {
        this.rawText = text;
        if (entity != null && !entity.isDead()) {
            Component component = parseText(text);
            entity.text(component);
        }
    }

    /**
     * Updates the displayed text with placeholder support for a specific viewer.
     *
     * @param text   the new text
     * @param viewer the player viewer for placeholder resolution
     */
    public void update(String text, org.bukkit.entity.Player viewer) {
        this.rawText = text;
        if (entity != null && !entity.isDead()) {
            String resolved = resolvePlaceholders(text, viewer);
            Component component = parseText(resolved);
            entity.text(component);
        }
    }

    /**
     * Parses text into a Component, handling both legacy &amp; codes and MiniMessage tags.
     *
     * @param text the text to parse
     * @return the parsed Component
     */
    private Component parseText(String text) {
        if (text.contains("&")) {
            text = translateLegacyCodes(text);
        }
        return MiniMessage.miniMessage().deserialize(text);
    }

    /**
     * Translates legacy &amp;color codes to MiniMessage tags.
     *
     * @param text the text with legacy codes
     * @return text with MiniMessage tags
     */
    private String translateLegacyCodes(String text) {
        char[] chars = text.toCharArray();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '&' && i + 1 < chars.length) {
                String tag = COLOR_CODES.get(Character.toLowerCase(chars[i + 1]));
                if (tag != null) {
                    result.append(tag);
                    i++;
                } else {
                    result.append(chars[i]);
                }
            } else {
                result.append(chars[i]);
            }
        }
        return result.toString();
    }

    /**
     * Removes the TextDisplay entity.
     */
    public void remove() {
        if (entity != null && !entity.isDead()) {
            entity.remove();
        }
        entity = null;
    }

    /**
     * Returns the raw text of this line.
     *
     * @return the raw text
     */
    public String getRawText() {
        return rawText;
    }

    /**
     * Returns the TextDisplay entity.
     *
     * @return the entity, or null if not spawned
     */
    public TextDisplay getEntity() {
        return entity;
    }

    /**
     * Returns the zero-based line index.
     *
     * @return the line index
     */
    public int getLineIndex() {
        return lineIndex;
    }

    /**
     * Sets the line index.
     *
     * @param lineIndex the new line index
     */
    public void setLineIndex(int lineIndex) {
        this.lineIndex = lineIndex;
    }

    /**
     * Returns the animation for this line, if any.
     *
     * @return the animation, or null
     */
    public HologramAnimation getAnimation() {
        return animation;
    }

    /**
     * Sets the animation for this line.
     *
     * @param animation the animation to set
     */
    public void setAnimation(HologramAnimation animation) {
        this.animation = animation;
    }

    /**
     * Returns whether this line has an active animation.
     *
     * @return true if animated
     */
    public boolean isAnimated() {
        return animation != null && animation.isRunning();
    }

    /**
     * Checks if this line contains placeholder text.
     *
     * @return true if placeholders are present
     */
    public boolean hasPlaceholders() {
        return rawText != null && rawText.contains("{");
    }

    private String resolvePlaceholders(String text, org.bukkit.entity.Player viewer) {
        if (viewer == null) return text;

        dev.cozy.holograms.CozyHolograms plugin = (dev.cozy.holograms.CozyHolograms)
                org.bukkit.Bukkit.getPluginManager().getPlugin("CozyHolograms");

        if (plugin != null && plugin.isPlaceholderAPIEnabled()) {
            try {
                Class<?> papiClass = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
                java.lang.reflect.Method setMethod = papiClass.getMethod("setPlaceholders",
                        org.bukkit.entity.Player.class, String.class);
                text = (String) setMethod.invoke(null, viewer, text);
            } catch (Exception ignored) {
            }
        }

        text = text.replace("{online}", String.valueOf(org.bukkit.Bukkit.getOnlinePlayers().size()));
        text = text.replace("{max}", String.valueOf(org.bukkit.Bukkit.getMaxPlayers()));
        text = text.replace("{world}", viewer.getWorld().getName());
        text = text.replace("{player}", viewer.getName());

        return text;
    }
}
