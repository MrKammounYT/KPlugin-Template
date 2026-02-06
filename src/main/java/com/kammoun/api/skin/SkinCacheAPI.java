package com.kammoun.api.skin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class SkinCacheAPI {
    
    private static SkinCacheAPI instance;
    
    private final SkinService service;
    
    private SkinCacheAPI(@NotNull SkinService service) {
        this.service = service;
    }
    
    public static SkinCacheAPI getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SkinCacheAPI has not been initialized. Call SkinCacheAPI.init(plugin) first.");
        }
        return instance;
    }
    
    public static CompletableFuture<SkinCacheAPI> init(@NotNull Plugin plugin) {
        if (instance != null) {
            return CompletableFuture.completedFuture(instance);
        }
        
        SkinService service = new SkinService(plugin);
        return service.initialize().thenApply(v -> {
            instance = new SkinCacheAPI(service);
            return instance;
        });
    }
    
    public static boolean isInitialized() {
        return instance != null;
    }
    
    public void getSkin(@NotNull UUID uuid, @NotNull Consumer<SkinData> callback) {
        service.getSkin(uuid, callback);
    }
    
    public void getSkin(@NotNull Player player, @NotNull Consumer<SkinData> callback) {
        service.getSkin(player.getUniqueId(), callback);
    }
    
    public CompletableFuture<SkinData> getSkin(@NotNull UUID uuid) {
        return service.getSkin(uuid);
    }
    
    public CompletableFuture<SkinData> getSkin(@NotNull Player player) {
        return service.getSkin(player.getUniqueId());
    }
    
    public void getPlayerHeadTexture(@NotNull UUID uuid, @NotNull Consumer<String> callback) {
        service.getPlayerHeadTexture(uuid, callback);
    }
    
    public void getPlayerHeadTexture(@NotNull Player player, @NotNull Consumer<String> callback) {
        service.getPlayerHeadTexture(player.getUniqueId(), callback);
    }
    
    public CompletableFuture<String> getPlayerHeadTexture(@NotNull UUID uuid) {
        return service.getPlayerHeadTexture(uuid);
    }
    
    public CompletableFuture<String> getPlayerHeadTexture(@NotNull Player player) {
        return service.getPlayerHeadTexture(player.getUniqueId());
    }
    
    @Nullable
    public String getTextureUrl(@NotNull UUID uuid) {
        return service.getTextureUrl(uuid);
    }
    
    @Nullable
    public String getTextureUrl(@NotNull Player player) {
        return service.getTextureUrl(player.getUniqueId());
    }
    
    public CompletableFuture<String> getTextureUrlAsync(@NotNull UUID uuid) {
        return service.getTextureUrlAsync(uuid);
    }
    
    public CompletableFuture<String> getTextureUrlAsync(@NotNull Player player) {
        return service.getTextureUrlAsync(player.getUniqueId());
    }
    
    public void cacheOnlinePlayer(@NotNull Player player) {
        service.cacheOnlinePlayer(player);
    }
    
    public void preload(@NotNull UUID uuid) {
        service.preloadFromStorage(uuid);
    }
    
    public void invalidate(@NotNull UUID uuid) {
        service.invalidate(uuid);
    }
    
    public void invalidate(@NotNull Player player) {
        service.invalidate(player.getUniqueId());
    }
    
    public int getCacheSize() {
        return service.getCacheSize();
    }
    
    SkinService getService() {
        return service;
    }
}
