package com.kammoun.api.skin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class SkinService {
    
    private final Plugin plugin;
    private final SkinCache cache;
    private final SkinStorage storage;
    private final Map<UUID, CompletableFuture<SkinData>> pendingFetches;
    
    public SkinService(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.cache = new ConcurrentSkinCache();
        this.storage = new SQLiteSkinStorage(plugin.getDataFolder(), plugin.getLogger());
        this.pendingFetches = new ConcurrentHashMap<>();
    }
    
    public CompletableFuture<Void> initialize() {
        return storage.initialize();
    }
    
    public void getSkin(@NotNull UUID uuid, @NotNull Consumer<SkinData> callback) {
        SkinData cached = cache.get(uuid);
        if (cached != null) {
            callback.accept(cached);
            return;
        }
        
        fetchSkinAsync(uuid).thenAccept(callback);
    }
    
    public CompletableFuture<SkinData> getSkin(@NotNull UUID uuid) {
        SkinData cached = cache.get(uuid);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }
        return fetchSkinAsync(uuid);
    }
    
    public void getPlayerHeadTexture(@NotNull UUID uuid, @NotNull Consumer<String> callback) {
        getSkin(uuid, skinData -> callback.accept(skinData.textureValue()));
    }
    
    public CompletableFuture<String> getPlayerHeadTexture(@NotNull UUID uuid) {
        return getSkin(uuid).thenApply(SkinData::textureValue);
    }
    
    @Nullable
    public String getTextureUrl(@NotNull UUID uuid) {
        SkinData cached = cache.get(uuid);
        if (cached != null) {
            return cached.getTextureUrl();
        }
        return null;
    }
    
    public CompletableFuture<String> getTextureUrlAsync(@NotNull UUID uuid) {
        return getSkin(uuid).thenApply(SkinData::getTextureUrl);
    }

    public void cacheOnlinePlayer(@NotNull Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String texture = extractTextureFromPlayerProfile(player);
            if (texture != null) {
                String signature = MojangUtils.getSignaturePaper(player);
                SkinData skinData = SkinData.of(player.getUniqueId(), texture, signature);
                cache.put(skinData);
                storage.save(skinData);
            }
        });
    }
    
    public void preloadFromStorage(@NotNull UUID uuid) {
        storage.load(uuid).thenAccept(opt -> opt.ifPresent(cache::put));
    }
    
    public void invalidate(@NotNull UUID uuid) {
        cache.invalidate(uuid);
    }
    
    public int getCacheSize() {
        return cache.size();
    }
    
    public CompletableFuture<Void> shutdown() {
        cache.clear();
        pendingFetches.clear();
        return storage.shutdown();
    }
    
    private CompletableFuture<SkinData> fetchSkinAsync(@NotNull UUID uuid) {
        return pendingFetches.computeIfAbsent(uuid, this::doFetch);
    }
    
    private CompletableFuture<SkinData> doFetch(@NotNull UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        
        if (player != null && player.isOnline()) {
            return fetchFromOnlinePlayer(player);
        }
        
        return fetchFromStorageOrDefault(uuid);
    }
    
    private CompletableFuture<SkinData> fetchFromOnlinePlayer(@NotNull Player player) {
        return CompletableFuture.supplyAsync(() -> {
            String texture = extractTextureFromPlayerProfile(player);
            if (texture != null) {
                String signature = MojangUtils.getSignaturePaper(player);
                SkinData skinData = SkinData.of(player.getUniqueId(), texture, signature);
                cache.put(skinData);
                storage.save(skinData);
                pendingFetches.remove(player.getUniqueId());
                return skinData;
            }
            return SkinData.createDefault(player.getUniqueId());
        }).whenComplete((result, ex) -> pendingFetches.remove(player.getUniqueId()));
    }
    
    private CompletableFuture<SkinData> fetchFromStorageOrDefault(@NotNull UUID uuid) {
        return storage.load(uuid)
                .thenApply(opt -> {
                    SkinData skinData = opt.orElseGet(() -> SkinData.createDefault(uuid));
                    cache.put(skinData);
                    return skinData;
                })
                .whenComplete((result, ex) -> pendingFetches.remove(uuid));
    }
    
    @Nullable
    private String extractTextureFromPlayerProfile(@NotNull Player player) {
        try {
            return MojangUtils.getSkinBase64Paper(player);
        } catch (Exception e) {
            try {
                return MojangUtils.getSkinRawValue(player.getName()).join();
            } catch (Exception e0) {
                plugin.getLogger().warning(
                        "Failed to extract texture from PlayerProfile for "
                                + player.getName() + ": " + e.getMessage()
                );
            }
            return null;
        }
    }
}
