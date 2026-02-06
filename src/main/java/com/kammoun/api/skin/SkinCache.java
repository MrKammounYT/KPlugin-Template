package com.kammoun.api.skin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface SkinCache {

    @Nullable
    SkinData get(@NotNull UUID uuid);

    void put(@NotNull SkinData skinData);

    boolean contains(@NotNull UUID uuid);

    void invalidate(@NotNull UUID uuid);

    void clear();

    int size();
}