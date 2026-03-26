package com.kammoun.api.utils.items;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemBuilder {

    protected ItemStack itemStack;
    protected ItemMeta meta;
    private final Map<String, Object> nbtTags = new HashMap<>();

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material);
    }

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.meta = itemStack.getItemMeta();
    }

    public ItemBuilder(ItemStack existing) {
        this.itemStack = existing.clone();
        this.meta = this.itemStack.getItemMeta();
    }

    public ItemBuilder name(String name) {
        if (name != null)
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        return this;
    }

    public ItemBuilder amount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        if (lore != null && !lore.isEmpty())
            meta.setLore(lore.stream()
                    .map(l -> ChatColor.translateAlternateColorCodes('&', l))
                    .collect(Collectors.toList()));
        return this;
    }

    public ItemBuilder lore(String... lines) {
        return lore(Arrays.asList(lines));
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder flag(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder glow() {
        enchant(itemStack.getType() != Material.BOW ? Enchantment.LURE : Enchantment.LUCK_OF_THE_SEA, 1);
        flag(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder unbreakable(boolean value) {
        meta.setUnbreakable(value);
        return this;
    }

    public ItemBuilder modelData(int data) {
        meta.setCustomModelData(data);
        return this;
    }

    public ItemBuilder damage(int damage) {
        if (meta instanceof Damageable)
            ((Damageable) meta).setDamage(damage);
        return this;
    }

    public ItemBuilder armorColor(Color color) {
        if (meta instanceof LeatherArmorMeta)
            ((LeatherArmorMeta) meta).setColor(color);
        return this;
    }

    public ItemBuilder potionEffect(PotionEffectType type, int duration, int amplifier) {
        if (meta instanceof PotionMeta)
            ((PotionMeta) meta).addCustomEffect(new PotionEffect(type, duration, amplifier), true);
        return this;
    }

    public ItemBuilder potionColor(Color color) {
        if (meta instanceof PotionMeta)
            ((PotionMeta) meta).setColor(color);
        return this;
    }

    public ItemBuilder bannerPattern(Pattern pattern) {
        if (meta instanceof BannerMeta)
            ((BannerMeta) meta).addPattern(pattern);
        return this;
    }

    public ItemBuilder nbt(String key, Object value) {
        nbtTags.put(key, value);
        return this;
    }

    public ItemBuilder modifyMeta(Consumer<ItemMeta> consumer) {
        consumer.accept(meta);
        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(meta);
        if (nbtTags.isEmpty()) return itemStack;

        NBTItem nbt = new NBTItem(itemStack);
        nbtTags.forEach((key, value) -> {
            if (value instanceof String)   nbt.setString(key, (String) value);
            else if (value instanceof Integer)  nbt.setInteger(key, (Integer) value);
            else if (value instanceof Double)   nbt.setDouble(key, (Double) value);
            else if (value instanceof Boolean)  nbt.setBoolean(key, (Boolean) value);
            else if (value instanceof Long)     nbt.setLong(key, (Long) value);
            else if (value instanceof int[])    nbt.setIntArray(key, (int[]) value);
            else if (value instanceof byte[])   nbt.setByteArray(key, (byte[]) value);
        });
        return nbt.getItem();
    }
}