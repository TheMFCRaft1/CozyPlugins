package dev.cozy.hub.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder for creating {@link ItemStack} instances with a fluent API.
 */
public final class ItemBuilder {

    private Material material;
    private String name;
    private List<String> lore;
    private boolean glow;
    private int customModelData;

    /**
     * Creates a new ItemBuilder with the specified material.
     *
     * @param material the item material
     */
    public ItemBuilder(Material material) {
        this.material = material;
        this.lore = new ArrayList<>();
    }

    /**
     * Sets the display name of the item.
     *
     * @param name the display name with optional color codes using {@code &}
     * @return this builder
     */
    public ItemBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the lore lines of the item.
     *
     * @param lore the lore lines with optional color codes
     * @return this builder
     */
    public ItemBuilder lore(String... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }

    /**
     * Sets the lore lines of the item.
     *
     * @param lore the lore lines
     * @return this builder
     */
    public ItemBuilder lore(List<String> lore) {
        this.lore = new ArrayList<>(lore);
        return this;
    }

    /**
     * Enables or disables enchantment glow on the item.
     *
     * @param glow true to enable glow
     * @return this builder
     */
    public ItemBuilder glow(boolean glow) {
        this.glow = glow;
        return this;
    }

    /**
     * Sets the custom model data for the item.
     *
     * @param customModelData the custom model data value
     * @return this builder
     */
    public ItemBuilder customModelData(int customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    /**
     * Builds the final {@link ItemStack} with all configured properties.
     *
     * @return the constructed ItemStack
     */
    public ItemStack build() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        if (name != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        }

        if (!lore.isEmpty()) {
            List<String> translatedLore = new ArrayList<>();
            for (String line : lore) {
                translatedLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(translatedLore);
        }

        if (glow) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if (customModelData > 0) {
            meta.setCustomModelData(customModelData);
        }

        item.setItemMeta(meta);
        return item;
    }
}
