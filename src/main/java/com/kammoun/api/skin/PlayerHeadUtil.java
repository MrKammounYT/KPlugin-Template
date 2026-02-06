package com.kammoun.api.skin;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class PlayerHeadUtil {
    
    private static final UUID TEXTURE_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    
    private PlayerHeadUtil() {
    }
    
    public static ItemStack createHead(@NotNull String base64Texture) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        
        PlayerProfile profile = Bukkit.createProfile(TEXTURE_UUID);
        profile.setProperty(new ProfileProperty("textures", base64Texture));
        meta.setPlayerProfile(profile);
        
        head.setItemMeta(meta);
        return head;
    }
    
    public static ItemStack createHead(@NotNull SkinData skinData) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        
        PlayerProfile profile = Bukkit.createProfile(skinData.uuid());
        profile.setProperty(new ProfileProperty("textures", skinData.textureValue(), skinData.signature()));
        meta.setPlayerProfile(profile);
        
        head.setItemMeta(meta);
        return head;
    }
    
    public static void createHeadAsync(@NotNull UUID uuid, @NotNull Consumer<ItemStack> callback) {
        SkinCacheAPI.getInstance().getSkin(uuid, skinData -> callback.accept(createHead(skinData)));
    }
    
    public static CompletableFuture<ItemStack> createHeadAsync(@NotNull UUID uuid) {
        return SkinCacheAPI.getInstance().getSkin(uuid).thenApply(PlayerHeadUtil::createHead);
    }
    
    public static ItemStack createDefaultHead() {
        return createHead(SkinData.DEFAULT_TEXTURE);
    }
}
