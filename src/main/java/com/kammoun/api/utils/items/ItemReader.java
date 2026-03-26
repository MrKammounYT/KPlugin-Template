package com.kammoun.api.utils.items;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

public final class ItemReader {

    private ItemReader() {}

    public static ItemBuilder readItem(ConfigurationSection section) {
        return readItem(section, null);
    }

    public static ItemBuilder readItem(ConfigurationSection section, JavaPlugin plugin) {
        if (section == null) return null;

        String matName = section.getString("material");
        if (matName == null) return null;

        Material material;
        try {
            material = Material.valueOf(matName.toUpperCase());
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("[ItemReader] Unknown material: " + matName);
            return null;
        }

        ItemBuilder builder = (material == Material.PLAYER_HEAD && plugin != null)
                ? new SkullBuilder(plugin)
                : new ItemBuilder(material);

        builder.amount(section.getInt("amount", 1));
        if (section.contains("name"))        builder.name(section.getString("name"));
        if (section.contains("displayName")) builder.name(section.getString("displayName"));
        if (section.contains("lore"))        builder.lore(section.getStringList("lore"));
        if (section.contains("damage"))      builder.damage(section.getInt("damage"));
        if (section.contains("custom-model-data")) builder.modelData(section.getInt("custom-model-data"));
        if (section.getBoolean("unbreakable"))     builder.unbreakable(true);
        if (section.getBoolean("glow"))            builder.glow();

        readFlags(section, builder);
        readEnchantments(section, builder);

        if (material == Material.PLAYER_HEAD && builder instanceof SkullBuilder skull) {
            if (section.contains("texture"))     skull.texture(section.getString("texture"));
            else if (section.contains("skull-owner")) skull.owner(section.getString("skull-owner"));
        }

        readLeatherColor(section, material, builder);
        readPotion(section, material, builder);
        readNbt(section, builder);

        return builder;
    }

    public static MenuItem readMenuItem(ConfigurationSection section) {
        return readMenuItem(section, null);
    }

    public static MenuItem readMenuItem(ConfigurationSection section, JavaPlugin plugin) {
        ItemBuilder builder = readItem(section, plugin);
        if (builder == null) return null;

        MenuItem item = MenuItem.of(builder);
        if (section.contains("slot"))       item.slot(section.getInt("slot"));
        if (section.contains("price"))      item.price(section.getDouble("price"));
        if (section.contains("permission")) item.permission(section.getString("permission"));

        if (section.contains("commands")) {
            String typeStr = section.getString("CommandType", "PLAYER").toUpperCase();
            ClickAction.Type type;
            try { type = ClickAction.Type.valueOf(typeStr); }
            catch (IllegalArgumentException e) { type = ClickAction.Type.PLAYER; }

            item.action(new ClickAction(section.getStringList("commands"), type));
        }

        return item;
    }

    private static void readFlags(ConfigurationSection s, ItemBuilder b) {
        if (!s.contains("flags")) return;
        s.getStringList("flags").forEach(f -> {
            try { b.flag(ItemFlag.valueOf(f.toUpperCase())); }
            catch (IllegalArgumentException e) { Bukkit.getLogger().warning("[ItemReader] Bad flag: " + f); }
        });
    }

    private static void readEnchantments(ConfigurationSection s, ItemBuilder b) {
        if (!s.contains("enchantments")) return;
        s.getStringList("enchantments").forEach(line -> {
            String[] p = line.split(":");
            if (p.length != 2) return;
            Enchantment ench = Enchantment.getByName(p[0].toUpperCase());
            if (ench == null) return;
            try { b.enchant(ench, Integer.parseInt(p[1])); }
            catch (NumberFormatException ignored) {}
        });
    }

    private static void readLeatherColor(ConfigurationSection s, Material mat, ItemBuilder b) {
        if (!mat.name().startsWith("LEATHER_") || !s.contains("color")) return;
        String[] rgb = s.getString("color").split(",");
        if (rgb.length != 3) return;
        try {
            b.armorColor(Color.fromRGB(
                    Integer.parseInt(rgb[0].trim()),
                    Integer.parseInt(rgb[1].trim()),
                    Integer.parseInt(rgb[2].trim())));
        } catch (NumberFormatException ignored) {}
    }

    private static void readPotion(ConfigurationSection s, Material mat, ItemBuilder b) {
        boolean isPotion = mat == Material.POTION || mat == Material.SPLASH_POTION || mat == Material.LINGERING_POTION;
        if (!isPotion) return;
        if (s.contains("potion-color")) {
            String[] rgb = s.getString("potion-color").split(",");
            if (rgb.length == 3) {
                try { b.potionColor(Color.fromRGB(Integer.parseInt(rgb[0].trim()), Integer.parseInt(rgb[1].trim()), Integer.parseInt(rgb[2].trim()))); }
                catch (NumberFormatException ignored) {}
            }
        }
        if (s.contains("potion-effects")) {
            s.getStringList("potion-effects").forEach(line -> {
                String[] p = line.split(":");
                if (p.length != 3) return;
                PotionEffectType type = PotionEffectType.getByName(p[0].toUpperCase());
                if (type == null) return;
                try { b.potionEffect(type, Integer.parseInt(p[1]) * 20, Integer.parseInt(p[2])); }
                catch (NumberFormatException ignored) {}
            });
        }
    }

    private static void readNbt(ConfigurationSection s, ItemBuilder b) {
        if (!s.isConfigurationSection("nbt")) return;
        ConfigurationSection nbt = s.getConfigurationSection("nbt");
        nbt.getKeys(false).forEach(key -> {
            String raw = nbt.getString(key);
            if (raw == null) return;
            String[] p = raw.split(":", 2);
            if (p.length != 2) return;
            try {
                switch (p[0].toLowerCase()) {
                    case "string"           -> b.nbt(key, p[1]);
                    case "int", "integer"   -> b.nbt(key, Integer.parseInt(p[1]));
                    case "double"           -> b.nbt(key, Double.parseDouble(p[1]));
                    case "bool", "boolean"  -> b.nbt(key, Boolean.parseBoolean(p[1]));
                    case "long"             -> b.nbt(key, Long.parseLong(p[1]));
                }
            } catch (NumberFormatException ignored) {}
        });
    }
}