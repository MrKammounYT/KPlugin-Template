package com.kammoun.api.utils.items;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kammoun.api.utils.VersionHelper;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public final class SkullHandler {

    private static final Gson GSON = new Gson();

    private SkullHandler() {}

    @NotNull
    public static String encodeTextureUrl(@NotNull String url) {
        String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}";
        return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    @Nullable
    public static String decodeTextureUrl(@NotNull String base64Texture) {
        String decoded = new String(Base64.getDecoder().decode(base64Texture));
        JsonObject object = GSON.fromJson(decoded, JsonObject.class);

        JsonElement textures = object.get("textures");
        if (textures == null) return null;

        JsonElement skin = textures.getAsJsonObject().get("SKIN");
        if (skin == null) return null;

        JsonElement url = skin.getAsJsonObject().get("url");
        return url == null ? null : url.getAsString();
    }

    @NotNull
    private static ItemStack getBaseHead() {
        if (!VersionHelper.IS_ITEM_LEGACY) {
            return new ItemStack(Material.PLAYER_HEAD, 1);
        }
        return new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
    }

    @NotNull
    public static ItemStack getSkullByTexture(@NotNull JavaPlugin plugin, @NotNull String base64Url) {
        ItemStack head = getBaseHead().clone();
        if (base64Url.isEmpty()) return head;

        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta == null) return head;

        if (VersionHelper.HAS_PLAYER_PROFILES) {
            meta.setOwnerProfile(buildPlayerProfile(plugin, base64Url));
            head.setItemMeta(meta);
            return head;
        }

        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, buildGameProfile(base64Url));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            plugin.getLogger().warning("[SkullHandler] Failed to set skull texture: " + e.getMessage());
        }

        head.setItemMeta(meta);
        return head;
    }

    @NotNull
    public static ItemStack getSkullByName(@NotNull String playerName) {
        ItemStack head = getBaseHead().clone();
        if (playerName.isEmpty()) return head;

        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta == null) return head;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

        if (VersionHelper.HAS_PLAYER_PROFILES && offlinePlayer.getPlayerProfile().getTextures().isEmpty()) {
            meta.setOwnerProfile(offlinePlayer.getPlayerProfile().update().join());
        } else if (!VersionHelper.IS_SKULL_OWNER_LEGACY) {
            meta.setOwningPlayer(offlinePlayer);
        } else {
            meta.setOwner(offlinePlayer.getName());
        }

        head.setItemMeta(meta);
        return head;
    }

    @Nullable
    public static String getTextureFromSkull(@NotNull JavaPlugin plugin, @NotNull ItemStack item) {
        if (!(item.getItemMeta() instanceof SkullMeta meta)) return null;

        if (VersionHelper.HAS_PLAYER_PROFILES) {
            PlayerProfile profile = meta.getOwnerProfile();
            if (profile == null) return null;
            URL url = profile.getTextures().getSkin();
            return url == null ? null : url.toString();
        }

        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            GameProfile gp = (GameProfile) profileField.get(meta);
            if (gp == null) return null;
            for (Property property : gp.getProperties().get("textures")) {
                if ("textures".equals(property.getName())) {
                    return decodeTextureUrl(property.getValue());
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            plugin.getLogger().warning("[SkullHandler] Failed to get skull texture: " + e.getMessage());
        }
        return null;
    }

    @Nullable
    public static String getSkullOwner(@NotNull ItemStack skull) {
        if (!(skull.getItemMeta() instanceof SkullMeta meta)) return null;

        if (!VersionHelper.IS_SKULL_OWNER_LEGACY) {
            return meta.getOwningPlayer() == null ? null : meta.getOwningPlayer().getName();
        }
        return meta.getOwner();
    }

    @NotNull
    private static GameProfile buildGameProfile(@NotNull String base64Url) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", base64Url));
        return profile;
    }

    @NotNull
    private static PlayerProfile buildPlayerProfile(@NotNull JavaPlugin plugin, @NotNull String base64Url) {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        String url = decodeTextureUrl(base64Url);
        if (url == null) return profile;

        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(new URL(url));
        } catch (MalformedURLException e) {
            plugin.getLogger().warning("[SkullHandler] Malformed skin URL: " + e.getMessage());
        }
        profile.setTextures(textures);
        return profile;
    }
}