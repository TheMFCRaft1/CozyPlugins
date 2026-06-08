package dev.cozy.holograms.hologram;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a hologram consisting of one or more {@link HologramLine}s.
 * <p>
 * Holograms are composed of TextDisplay entities spawned vertically
 * with a configurable line spacing of 0.28 blocks.
 */
public final class Hologram {

    private static final double LINE_SPACING = 0.28;

    private final String id;
    private Location location;
    private final List<HologramLine> lines;
    private boolean persistent;
    private boolean animated;
    private int animationInterval;

    /**
     * Creates a new Hologram.
     *
     * @param id        the unique hologram identifier
     * @param location  the base location (top line position)
     * @param persistent whether to save to disk
     */
    public Hologram(String id, Location location, boolean persistent) {
        this.id = id;
        this.location = location.clone();
        this.lines = new ArrayList<>();
        this.persistent = persistent;
        this.animated = false;
        this.animationInterval = 20;
    }

    /**
     * Adds a new text line to the hologram.
     *
     * @param text the line text
     * @return the created HologramLine
     */
    public HologramLine addLine(String text) {
        int index = lines.size();
        HologramLine line = new HologramLine(text, index);
        Location lineLoc = calculateLineLocation(index);
        line.spawn(lineLoc);
        lines.add(line);
        return line;
    }

    /**
     * Adds a new animated line to the hologram.
     *
     * @param frames   the animation frames
     * @param interval the interval in ticks
     * @return the created HologramLine
     */
    public HologramLine addAnimatedLine(List<String> frames, int interval) {
        if (frames.isEmpty()) return null;

        int index = lines.size();
        HologramLine line = new HologramLine(frames.get(0), index);
        Location lineLoc = calculateLineLocation(index);
        line.spawn(lineLoc);

        HologramAnimation animation = new HologramAnimation(frames, interval);
        line.setAnimation(animation);
        animation.start(line);

        lines.add(line);
        return line;
    }

    /**
     * Removes the line at the specified index.
     *
     * @param index the zero-based line index
     */
    public void removeLine(int index) {
        if (index < 0 || index >= lines.size()) return;

        HologramLine line = lines.remove(index);
        if (line.getAnimation() != null) {
            line.getAnimation().stop();
        }
        line.remove();

        for (int i = index; i < lines.size(); i++) {
            lines.get(i).setLineIndex(i);
            lines.get(i).remove();
            lines.get(i).spawn(calculateLineLocation(i));
        }
    }

    /**
     * Updates the text of the line at the specified index.
     *
     * @param index the zero-based line index
     * @param text  the new text
     */
    public void setLine(int index, String text) {
        if (index < 0 || index >= lines.size()) return;
        lines.get(index).update(text);
    }

    /**
     * Teleports the hologram to a new location.
     *
     * @param newLocation the new base location
     */
    public void teleport(Location newLocation) {
        this.location = newLocation.clone();
        for (int i = 0; i < lines.size(); i++) {
            HologramLine line = lines.get(i);
            line.remove();
            line.spawn(calculateLineLocation(i));
        }
    }

    /**
     * Deletes all entities and clears all lines.
     */
    public void delete() {
        for (HologramLine line : lines) {
            if (line.getAnimation() != null) {
                line.getAnimation().stop();
            }
            line.remove();
        }
        lines.clear();
    }

    /**
     * Respawns all lines (for reload purposes).
     */
    public void respawn() {
        for (HologramLine line : lines) {
            if (line.getAnimation() != null) {
                line.getAnimation().stop();
            }
            line.remove();
        }
        for (int i = 0; i < lines.size(); i++) {
            HologramLine line = lines.get(i);
            line.spawn(calculateLineLocation(i));
            if (line.getAnimation() != null) {
                line.getAnimation().start(line);
            }
        }
    }

    /**
     * Returns the hologram ID.
     *
     * @return the ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the base location.
     *
     * @return the location
     */
    public Location getLocation() {
        return location.clone();
    }

    /**
     * Returns an unmodifiable view of the lines.
     *
     * @return the lines
     */
    public List<HologramLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    /**
     * Returns whether the hologram is persistent.
     *
     * @return true if persistent
     */
    public boolean isPersistent() {
        return persistent;
    }

    /**
     * Sets whether the hologram is persistent.
     *
     * @param persistent true to persist
     */
    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    /**
     * Returns whether the hologram has animated lines.
     *
     * @return true if animated
     */
    public boolean isAnimated() {
        return animated;
    }

    /**
     * Sets whether the hologram uses animation.
     *
     * @param animated true to enable animation
     */
    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    /**
     * Returns the animation interval in ticks.
     *
     * @return the interval
     */
    public int getAnimationInterval() {
        return animationInterval;
    }

    /**
     * Sets the animation interval in ticks.
     *
     * @param ticks the interval
     */
    public void setAnimationInterval(int ticks) {
        this.animationInterval = ticks;
    }

    /**
     * Calculates the location for a specific line index.
     *
     * @param index the zero-based line index
     * @return the location for this line
     */
    private Location calculateLineLocation(int index) {
        double yOffset = -(index * LINE_SPACING);
        return new Location(
                location.getWorld(),
                location.getX(),
                location.getY() + yOffset,
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }
}
