package com.kammoun.api.skin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record SkinData(
        @NotNull UUID uuid,
        @NotNull String textureValue,
        @Nullable String signature,
        long lastSeen
) {

    public static final String DEFAULT_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM0ZTA2M2NhZmI0NjdhNWM4ZGU0M2VjNzg2MTkzOTlmMzY5ZjRhNTI0MzRkYTgwMTdhOTgzY2RkOTI1MTZhMCJ9fX0=";


    public static SkinData createDefault(@NotNull UUID uuid) {
        return new SkinData(uuid, DEFAULT_TEXTURE, null, System.currentTimeMillis());
    }
    
    public static SkinData of(@NotNull UUID uuid, @NotNull String textureValue) {
        return new SkinData(uuid, textureValue, null, System.currentTimeMillis());
    }
    
    public static SkinData of(@NotNull UUID uuid, @NotNull String textureValue, @Nullable String signature) {
        return new SkinData(uuid, textureValue, signature, System.currentTimeMillis());
    }
    
    public boolean isDefault() {
        return DEFAULT_TEXTURE.equals(textureValue);
    }
    
    public String getTextureUrl() {
        return "https://textures.minecraft.net/texture/" + extractTextureId();
    }
    
    private String extractTextureId() {
        try {
            String decoded = new String(java.util.Base64.getDecoder().decode(textureValue));
            int urlStart = decoded.indexOf("http://textures.minecraft.net/texture/");
            if (urlStart == -1) {
                urlStart = decoded.indexOf("https://textures.minecraft.net/texture/");
            }
            if (urlStart != -1) {
                int idStart = urlStart + "http://textures.minecraft.net/texture/".length();
                if (decoded.charAt(urlStart + 4) == 's') {
                    idStart = urlStart + "https://textures.minecraft.net/texture/".length();
                }
                int idEnd = decoded.indexOf('"', idStart);
                if (idEnd != -1) {
                    return decoded.substring(idStart, idEnd);
                }
            }
        } catch (Exception ignored) {
        }
        return textureValue;
    }
}
