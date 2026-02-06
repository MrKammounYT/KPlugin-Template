package com.kammoun.api.skin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ConcurrentSkinCache implements SkinCache {

    private final Map<UUID, SkinData> cache = new ConcurrentHashMap<>();

    @Override
    public @Nullable SkinData get(@NotNull UUID uuid) {
        return cache.get(uuid);
    }

    @Override
    public void put(@NotNull SkinData skinData) {
        cache.put(skinData.uuid(), skinData);
    }

    @Override
    public boolean contains(@NotNull UUID uuid) {
        return cache.containsKey(uuid);
    }

    @Override
    public void invalidate(@NotNull UUID uuid) {
        cache.remove(uuid);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public int size() {
        return cache.size();
    }
}
