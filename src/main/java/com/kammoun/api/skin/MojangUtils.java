package com.kammoun.api.skin;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MojangUtils {
    
    private static final String MOJANG_SESSION_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final Pattern TEXTURE_PATTERN = Pattern.compile("\"value\"\\s*:\\s*\"([^\"]+)\"");
    
    private MojangUtils() {
    }
    
    @Nullable
    public static String getSkinBase64Paper(@NotNull Player player) {
        PlayerProfile profile = player.getPlayerProfile();
        Set<ProfileProperty> properties = profile.getProperties();
        for (ProfileProperty property : properties) {
            if ("textures".equals(property.getName())) {
                return property.getValue();
            }
        }
        return null;
    }
    
    @Nullable
    public static String getSignaturePaper(@NotNull Player player) {
        PlayerProfile profile = player.getPlayerProfile();
        Set<ProfileProperty> properties = profile.getProperties();
        for (ProfileProperty property : properties) {
            if ("textures".equals(property.getName())) {
                return property.getSignature();
            }
        }
        return null;
    }
    
    public static CompletableFuture<String> getSkinRawValue(@NotNull String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                UUID uuid = fetchUUID(playerName);
                if (uuid == null) {
                    return null;
                }
                return fetchTextureValue(uuid);
            } catch (Exception e) {
                return null;
            }
        });
    }
    
    public static CompletableFuture<String> getSkinRawValue(@NotNull UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return fetchTextureValue(uuid);
            } catch (Exception e) {
                return null;
            }
        });
    }
    
    @Nullable
    private static UUID fetchUUID(@NotNull String playerName) throws Exception {
        URI uri = URI.create("https://api.mojang.com/users/profiles/minecraft/" + playerName);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        
        try {
            if (connection.getResponseCode() != 200) {
                return null;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                String json = response.toString();
                Pattern pattern = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"");
                Matcher matcher = pattern.matcher(json);
                if (matcher.find()) {
                    String id = matcher.group(1);
                    return UUID.fromString(
                            id.replaceFirst(
                                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                                    "$1-$2-$3-$4-$5"
                            )
                    );
                }
            }
        } finally {
            connection.disconnect();
        }
        return null;
    }
    
    @Nullable
    private static String fetchTextureValue(@NotNull UUID uuid) throws Exception {
        String uuidStr = uuid.toString().replace("-", "");
        URI uri = URI.create(MOJANG_SESSION_URL + uuidStr);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        
        try {
            if (connection.getResponseCode() != 200) {
                return null;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                Matcher matcher = TEXTURE_PATTERN.matcher(response.toString());
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        } finally {
            connection.disconnect();
        }
        return null;
    }
    
    public static String decodeTextureUrl(@NotNull String base64Value) {
        try {
            String decoded = new String(Base64.getDecoder().decode(base64Value));
            Pattern urlPattern = Pattern.compile("\"url\"\\s*:\\s*\"([^\"]+)\"");
            Matcher matcher = urlPattern.matcher(decoded);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
