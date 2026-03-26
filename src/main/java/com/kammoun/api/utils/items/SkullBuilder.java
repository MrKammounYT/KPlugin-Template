package com.kammoun.api.utils.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class SkullBuilder extends ItemBuilder {

    private final JavaPlugin plugin;

    public SkullBuilder(JavaPlugin plugin) {
        super(Material.PLAYER_HEAD);
        this.plugin = plugin;
    }

    public SkullBuilder texture(String base64) {
        ItemStack skull = SkullHandler.getSkullByTexture(plugin, base64);
        if (skull != null && skull.hasItemMeta()) {
            String name  = meta.hasDisplayName() ? meta.getDisplayName() : null;
            var lore = meta.hasLore() ? meta.getLore() : null;
            this.itemStack = skull.clone();
            this.meta = this.itemStack.getItemMeta();
            if (name != null) meta.setDisplayName(name);
            if (lore != null) meta.setLore(lore);
        }
        return this;
    }

    public SkullBuilder owner(String playerName) {
        ItemStack skull = SkullHandler.getSkullByName(playerName);
        if (skull != null && skull.hasItemMeta()) {
            String name = meta.hasDisplayName() ? meta.getDisplayName() : null;
            var lore = meta.hasLore() ? meta.getLore() : null;
            this.itemStack = skull.clone();
            this.meta = this.itemStack.getItemMeta();
            if (name != null) meta.setDisplayName(name);
            if (lore != null) meta.setLore(lore);
        }
        return this;
    }
}