package dev.cozy.holograms.hologram;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles frame-based animation for a single hologram line.
 * <p>
 * Cycles through a list of text frames at a configurable interval,
 * updating the associated {@link HologramLine} entity.
 */
public final class HologramAnimation {

    private final List<String> frames;
    private final int interval;
    private int currentFrame;
    private BukkitRunnable task;
    private boolean running;

    /**
     * Creates a new HologramAnimation.
     *
     * @param frames   the animation frames
     * @param interval the interval in ticks between frame changes
     */
    public HologramAnimation(List<String> frames, int interval) {
        this.frames = new ArrayList<>(frames);
        this.interval = interval;
        this.currentFrame = 0;
        this.running = false;
    }

    /**
     * Starts the animation on the specified hologram line.
     *
     * @param line the line to animate
     */
    public void start(HologramLine line) {
        if (running || frames.isEmpty()) return;

        running = true;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!running) {
                    cancel();
                    return;
                }
                line.update(nextFrame());
            }
        };
        task.runTaskTimer(
                org.bukkit.Bukkit.getPluginManager().getPlugin("CozyHolograms"),
                interval, interval
        );
    }

    /**
     * Stops the animation.
     */
    public void stop() {
        running = false;
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
        task = null;
    }

    /**
     * Advances to the next frame and returns its text.
     *
     * @return the next frame text
     */
    public String nextFrame() {
        String frame = frames.get(currentFrame);
        currentFrame = (currentFrame + 1) % frames.size();
        return frame;
    }

    /**
     * Returns the animation frames.
     *
     * @return the frame list
     */
    public List<String> getFrames() {
        return new ArrayList<>(frames);
    }

    /**
     * Returns the interval in ticks.
     *
     * @return the interval
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Returns whether the animation is currently running.
     *
     * @return true if running
     */
    public boolean isRunning() {
        return running;
    }
}
