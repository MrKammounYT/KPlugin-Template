package com.kammoun.API.Utils.Items;

import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Setter
public class KItem {

    private final ItemStack itemStack;
    private ItemMeta meta;
    private final Map<String, Object> nbtTags = new HashMap<>();
    private final JavaPlugin plugin;

    // Optional menu-related properties
    private Integer slot;
    private double price = 0;
    private List<String> clickCommands = new ArrayList<>();

    public KItem(Material material) {
        this(null, material);
    }

    public KItem(JavaPlugin plugin, Material material) {
        this.plugin = plugin;
        this.itemStack = new ItemStack(material);
        this.meta = itemStack.getItemMeta();
    }

    public KItem(ItemStack itemStack) {
        this(null, itemStack);
    }

    public KItem(JavaPlugin plugin, ItemStack itemStack) {
        this.plugin = plugin;
        this.itemStack = itemStack.clone();
        this.meta = this.itemStack.getItemMeta();
    }

    public KItem name(String name) {
        if (name != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        }
        return this;
    }

    public KItem amount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public KItem lore(List<String> lore) {
        if (lore != null && !lore.isEmpty()) {
            meta.setLore(lore.stream()
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .collect(Collectors.toList()));
        }
        return this;
    }

    public KItem lore(String... loreLines) {
        return lore(Arrays.asList(loreLines));
    }

    public KItem damage(int damage) {
        if (meta instanceof Damageable) {
            ((Damageable) meta).setDamage(damage);
        }
        return this;
    }

    public KItem enchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public KItem flag(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    public KItem glow() {
        if (itemStack.getType() != Material.BOW) {
            enchant(Enchantment.LURE, 1);
        } else {
            enchant(Enchantment.LUCK_OF_THE_SEA, 1);
        }
        flag(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public KItem unbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    public KItem modelData(int modelData) {
        meta.setCustomModelData(modelData);
        return this;
    }


    public KItem price(double price) {
        this.price = price;
        return this;
    }

    public KItem commands(List<String> commands) {
        this.clickCommands = commands;
        return this;
    }

    public KItem commands(String... commands) {
        this.clickCommands = new ArrayList<>(Arrays.asList(commands));
        return this;
    }

    public KItem skullOwner(String ownerName) {
        if (plugin != null && meta instanceof SkullMeta && ownerName != null) {
            String currentName = meta.hasDisplayName() ? meta.getDisplayName() : null;
            List<String> currentLore = meta.hasLore() ? meta.getLore() : null;

            ItemStack tempSkull = SkullHandler.getSkullByName(ownerName);
            if (tempSkull != null && tempSkull.hasItemMeta()) {
                this.meta = tempSkull.getItemMeta();
                if (currentName != null) this.meta.setDisplayName(currentName);
                if (currentLore != null) this.meta.setLore(currentLore);
            }
        } else if (plugin == null) {
            Bukkit.getLogger().warning("[KItem] Cannot set skull owner without a plugin instance.");
        }
        return this;
    }

    public KItem texture(String textureValue) {
        if (plugin != null && meta instanceof SkullMeta && textureValue != null) {
            String currentName = meta.hasDisplayName() ? meta.getDisplayName() : null;
            List<String> currentLore = meta.hasLore() ? meta.getLore() : null;

            ItemStack tempSkull = SkullHandler.getSkullByBase64EncodedTextureUrl( plugin, textureValue);
            if (tempSkull != null && tempSkull.hasItemMeta()) {
                this.meta = tempSkull.getItemMeta();
                if (currentName != null) this.meta.setDisplayName(currentName);
                if (currentLore != null) this.meta.setLore(currentLore);
            }
        } else if (plugin == null) {
            Bukkit.getLogger().warning("[KItem] Cannot set texture without a plugin instance.");
        }
        return this;
    }

    public KItem armorColor(Color color) {
        if (meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(color);
        }
        return this;
    }

    public KItem potionEffect(PotionEffectType effectType, int duration, int amplifier) {
        if (meta instanceof PotionMeta) {
            ((PotionMeta) meta).addCustomEffect(new PotionEffect(effectType, duration, amplifier), true);
        }
        return this;
    }

    public KItem potionColor(Color color) {
        if (meta instanceof PotionMeta) {
            ((PotionMeta) meta).setColor(color);
        }
        return this;
    }

    public KItem addBannerPattern(Pattern pattern) {
        if (meta instanceof BannerMeta) {
            ((BannerMeta) meta).addPattern(pattern);
        }
        return this;
    }

    public KItem bannerPatterns(List<Pattern> patterns) {
        if (meta instanceof BannerMeta) {
            ((BannerMeta) meta).setPatterns(patterns);
        }
        return this;
    }

    public KItem nbt(String key, Object value) {
        this.nbtTags.put(key, value);
        return this;
    }

    public KItem modifyMeta(Consumer<ItemMeta> metaConsumer) {
        metaConsumer.accept(this.meta);
        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(meta);

        if (!nbtTags.isEmpty()) {
            NBTItem nbtItem = new NBTItem(itemStack);
            nbtTags.forEach((key, value) -> {
                if (value instanceof String) nbtItem.setString(key, (String) value);
                else if (value instanceof Integer) nbtItem.setInteger(key, (Integer) value);
                else if (value instanceof Double) nbtItem.setDouble(key, (Double) value);
                else if (value instanceof Boolean) nbtItem.setBoolean(key, (Boolean) value);
                else if (value instanceof Long) nbtItem.setLong(key, (Long) value);
                else if (value instanceof int[]) nbtItem.setIntArray(key, (int[]) value);
                else if (value instanceof byte[]) nbtItem.setByteArray(key, (byte[]) value);
            });
            return nbtItem.getItem();
        }
        return itemStack;
    }

    public static KItem fromConfig(ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        String materialName = section.getString("material");
        if (materialName == null) {
            Bukkit.getLogger().warning("[ItemBuilder] 'material' is not defined in config section: " + section.getName());
            return null;
        }

        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("[ItemBuilder] Invalid material '" + materialName + "' in section: " + section.getName());
            return null;
        }

       KItem builder = new KItem(material);

        // --- Load Optional Menu Properties ---
        if (section.contains("slot")) builder.setSlot(section.getInt("slot"));
        if (section.contains("price")) builder.price(section.getDouble("price"));
        if (section.contains("commands")) builder.commands(section.getStringList("commands"));

        // --- Load ItemStack Properties ---
        builder.amount(section.getInt("amount", 1));
        if (section.contains("name")) builder.name(section.getString("name"));
        if (section.contains("lore")) builder.lore(section.getStringList("lore"));
        if (section.contains("damage")) builder.damage(section.getInt("damage"));
        if (section.contains("custom-model-data")) builder.modelData(section.getInt("custom-model-data"));
        if (section.getBoolean("unbreakable")) builder.unbreakable(true);
        if (section.getBoolean("glow")) builder.glow();

        if (section.contains("flags")) {
            section.getStringList("flags").forEach(flagName -> {
                try {
                    builder.flag(ItemFlag.valueOf(flagName.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("[ItemBuilder] Invalid item flag '" + flagName + "' in section: " + section.getName());
                }
            });
        }

        if (section.contains("enchantments")) {
            section.getStringList("enchantments").forEach(line -> {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    Enchantment ench = Enchantment.getByName(parts[0].toUpperCase());
                    if (ench != null) {
                        try {
                            builder.enchant(ench, Integer.parseInt(parts[1]));
                        } catch (NumberFormatException e) {
                            Bukkit.getLogger().warning("[ItemBuilder] Invalid enchantment level for '" + parts[0] + "' in section: " + section.getName());
                        }
                    } else {
                        Bukkit.getLogger().warning("[ItemBuilder] Invalid enchantment '" + parts[0] + "' in section: " + section.getName());
                    }
                }
            });
        }

        if (material == Material.PLAYER_HEAD) {
            if (section.contains("texture")) builder.texture(section.getString("texture"));
            else if (section.contains("skull-owner")) builder.skullOwner(section.getString("skull-owner"));
        }

        if (material.name().startsWith("LEATHER_") && section.contains("color")) {
            String[] rgb = section.getString("color").split(",");
            if (rgb.length == 3) {
                try {
                    builder.armorColor(Color.fromRGB(Integer.parseInt(rgb[0].trim()), Integer.parseInt(rgb[1].trim()), Integer.parseInt(rgb[2].trim())));
                } catch(NumberFormatException e) {
                    Bukkit.getLogger().warning("[ItemBuilder] Invalid RGB color in section: " + section.getName());
                }
            }
        }

        if ((material == Material.POTION || material == Material.SPLASH_POTION || material == Material.LINGERING_POTION)) {
            if (section.contains("potion-color")) {
                String[] rgb = section.getString("potion-color").split(",");
                if (rgb.length == 3) {
                    try {
                        builder.potionColor(Color.fromRGB(Integer.parseInt(rgb[0].trim()), Integer.parseInt(rgb[1].trim()), Integer.parseInt(rgb[2].trim())));
                    } catch(NumberFormatException e) {
                        Bukkit.getLogger().warning("[KItem] Invalid potion RGB color in section: " + section.getName());
                    }
                }
            }
            if (section.contains("potion-effects")) {
                section.getStringList("potion-effects").forEach(line -> {
                    String[] parts = line.split(":");
                    if (parts.length == 3) {
                        PotionEffectType type = PotionEffectType.getByName(parts[0].toUpperCase());
                        if (type != null) {
                            try {
                                builder.potionEffect(type, Integer.parseInt(parts[1]) * 20, Integer.parseInt(parts[2]));
                            } catch (NumberFormatException e) {
                                Bukkit.getLogger().warning("[KItem] Invalid potion effect format for '" + parts[0] + "' in section: " + section.getName());
                            }
                        }
                    }
                });
            }
        }

        if (section.isConfigurationSection("nbt")) {
            ConfigurationSection nbtSection = section.getConfigurationSection("nbt");
            nbtSection.getKeys(false).forEach(key -> {
                String[] parts = nbtSection.getString(key).split(":", 2);
                if (parts.length == 2) {
                    String type = parts[0].toLowerCase(), value = parts[1];
                    try {
                        switch (type) {
                            case "string": builder.nbt(key, value); break;
                            case "int": case "integer": builder.nbt(key, Integer.parseInt(value)); break;
                            case "double": builder.nbt(key, Double.parseDouble(value)); break;
                            case "bool": case "boolean": builder.nbt(key, Boolean.parseBoolean(value)); break;
                            case "long": builder.nbt(key, Long.parseLong(value)); break;
                        }
                    } catch (NumberFormatException e) {
                        Bukkit.getLogger().warning("[KItem] Invalid NBT value for type '" + type + "' in section: " + section.getName());
                    }
                }
            });
        }

        return builder;
    }
}



