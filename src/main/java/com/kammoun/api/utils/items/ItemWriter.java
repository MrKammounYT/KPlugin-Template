package com.kammoun.api.utils.items;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.kammoun.api.utils.VersionHelper;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemWriter {
    // In ItemReader.java

    /**
     * Writes an ItemStack to a config section.
     * Existing keys are overwritten; the section is created if absent.
     */
    public static void writeItem(ConfigurationSection parent, String key, ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return;

        // Always start fresh so stale keys don't linger
        // (create a fresh map-backed section under the key)
        parent.set(key, null);
        ConfigurationSection s = parent.createSection(key);

        writeItem(s, item);
    }

    /**
     * Writes into an already-existing section (useful when the section
     * was created by the caller, e.g. inside a list loop).
     */
    public static void writeItem(ConfigurationSection s, ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return;

        ItemMeta meta = item.getItemMeta();

        s.set("material", item.getType().name());
        if (item.getAmount() != 1) s.set("amount", item.getAmount());

        if (meta == null) return;

        // ── Display ──────────────────────────────────────────────────────────────
        if (meta.hasDisplayName())
            s.set("name", reverseColor(meta.getDisplayName()));

        if (meta.hasLore())
            s.set("lore", meta.getLore().stream()
                    .map(ItemReader::reverseColor)
                    .collect(Collectors.toList()));

        // ── Misc meta ────────────────────────────────────────────────────────────
        if (meta.isUnbreakable())
            s.set("unbreakable", true);

        if (meta.hasCustomModelData())
            s.set("custom-model-data", meta.getCustomModelData());

        if (meta instanceof Damageable d && d.getDamage() != 0)
            s.set("damage", d.getDamage());

        // ── Enchantments ─────────────────────────────────────────────────────────
        if (meta.hasEnchants()) {
            // Detect glow: single LURE/LUCK_OF_THE_SEA + HIDE_ENCHANTS and nothing else
            boolean isGlow = isGlowOnly(item, meta);
            if (isGlow) {
                s.set("glow", true);
            } else {
                List<String> enchList = new ArrayList<>();
                meta.getEnchants().forEach((ench, lvl) ->
                        enchList.add(ench.getName() + ":" + lvl));
                s.set("enchantments", enchList);
            }
        }

        // ── Item flags (skip HIDE_ENCHANTS if glow was already saved) ────────────
        if (!meta.getItemFlags().isEmpty()) {
            boolean savedGlow = s.getBoolean("glow");
            List<String> flags = meta.getItemFlags().stream()
                    .filter(f -> !(savedGlow && f == ItemFlag.HIDE_ENCHANTS))
                    .map(Enum::name)
                    .collect(Collectors.toList());
            if (!flags.isEmpty()) s.set("flags", flags);
        }

        // ── Skull ────────────────────────────────────────────────────────────────
        if (meta instanceof SkullMeta skull) {
            writeSkull(s, skull);
        }

        // ── Leather armor ────────────────────────────────────────────────────────
        if (meta instanceof LeatherArmorMeta leather) {
            Color c = leather.getColor();
            s.set("color", c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        }

        // ── Potions ──────────────────────────────────────────────────────────────
        if (meta instanceof PotionMeta potion) {
            writePotion(s, potion);
        }

        // ── NBT (via NBT-API) ────────────────────────────────────────────────────
        writeNbt(s, item);
    }

// ── MenuItem overload ─────────────────────────────────────────────────────────

    /**
     * Writes a MenuItem (item + slot/price/permission/action) to a section.
     */
    public static void writeMenuItem(ConfigurationSection parent, String key, MenuItem menuItem) {
        writeItem(parent, key, menuItem.getItemStack());
        ConfigurationSection s = parent.getConfigurationSection(key);
        if (s == null) return;

        if (menuItem.getSlot() >= 0)
            s.set("slot", menuItem.getSlot());

        if (menuItem.getPrice() != 0)
            s.set("price", menuItem.getPrice());

        if (menuItem.getPermission() != null && !menuItem.getPermission().isEmpty())
            s.set("permission", menuItem.getPermission());

        ClickAction action = menuItem.getAction();
        if (action != null && !action.isEmpty()) {
            s.set("commands", action.commands());
            s.set("CommandType", action.type().name());
        }
    }

// ── Private write helpers ─────────────────────────────────────────────────────

    private static void writeSkull(ConfigurationSection s, SkullMeta skull) {
        if (VersionHelper.HAS_PLAYER_PROFILES) {
            PlayerProfile profile = skull.getOwnerProfile();
            if (profile != null) {
                URL skinUrl = profile.getTextures().getSkin();
                if (skinUrl != null) {
                    // Re-encode the URL back into base64 so readItem() can restore it
                    String base64 = encodeTextureUrl(skinUrl.toString());
                    if (base64 != null) s.set("texture", base64);
                    return;
                }
                // No texture — fall through to name-based owner
                if (profile.getName() != null)
                    s.set("skull-owner", profile.getName());
            }
            return;
        }

        // Legacy path (< 1.18.1): extract via GameProfile reflection
        try {
            Field profileField = skull.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            GameProfile gp = (GameProfile) profileField.get(skull);
            if (gp != null) {
                for (Property prop : gp.getProperties().get("textures")) {
                    if ("textures".equals(prop.getName())) {
                        s.set("texture", prop.getValue());
                        return;
                    }
                }
                if (gp.getName() != null && !gp.getName().isEmpty())
                    s.set("skull-owner", gp.getName());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Silently skip — partial data is better than a crash
        }
    }

    private static void writePotion(ConfigurationSection s, PotionMeta potion) {
        if (potion.getColor() != null) {
            Color c = potion.getColor();
            s.set("potion-color", c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        }
        if (!potion.getCustomEffects().isEmpty()) {
            List<String> effects = potion.getCustomEffects().stream()
                    .map(e -> e.getType().getName()
                            + ":" + (e.getDuration() / 20)   // back to seconds
                            + ":" + e.getAmplifier())
                    .collect(Collectors.toList());
            s.set("potion-effects", effects);
        }
    }

    private static void writeNbt(ConfigurationSection s, ItemStack item) {
        NBTItem nbt = new NBTItem(item);
        Set<String> keys = nbt.getKeys();
        if (keys.isEmpty()) return;

        ConfigurationSection nbtSection = s.createSection("nbt");
        for (String key : keys) {
            // NBT-API doesn't expose type cleanly, so we probe in priority order
            // and skip internal/Bukkit-managed keys
            if (key.startsWith("PublicBukkitValues") || key.equals("Damage")) continue;

            String serialized = probeNbtValue(nbt, key);
            if (serialized != null) nbtSection.set(key, serialized);
        }
    }

    /** Probes NBT type in order and returns "type:value", or null if unrecognised. */
    private static String probeNbtValue(NBTItem nbt, String key) {
        try {
            // Try the most specific types first
            if (nbt.getString(key) != null)     return "string:" + nbt.getString(key);
            if (nbt.getLong(key) != null)       return "long:"   + nbt.getLong(key);
            if (nbt.getDouble(key) != null)     return "double:" + nbt.getDouble(key);
            if (nbt.getInteger(key) != null)    return "int:"    + nbt.getInteger(key);
            if (nbt.getBoolean(key) != null)    return "bool:"   + nbt.getBoolean(key);
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * Reverses ChatColor.translateAlternateColorCodes('&', ...) back to &-codes.
     * The § character is the compiled color prefix Bukkit uses.
     */
    private static String reverseColor(String colored) {
        if (colored == null) return null;
        return colored.replace(ChatColor.COLOR_CHAR, '&');
    }

    /**
     * Encodes a plain texture URL back to the base64 format Bukkit expects.
     * e.g. "https://textures.minecraft.net/texture/abc123"
     *   →  base64( {"textures":{"SKIN":{"url":"https://..."}}} )
     */
    private static String encodeTextureUrl(String url) {
        if (url == null) return null;
        String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}";
        return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    /** Returns true if the only enchantment is the glow-trick one and HIDE_ENCHANTS is set. */
    private static boolean isGlowOnly(ItemStack item, ItemMeta meta) {
        if (meta.getEnchants().size() != 1) return false;
        if (!meta.getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) return false;
        Enchantment e = meta.getEnchants().keySet().iterator().next();
        return (item.getType() != Material.BOW && e == Enchantment.LURE)
                || (item.getType() == Material.BOW  && e == Enchantment.LUCK_OF_THE_SEA);
    }
}
