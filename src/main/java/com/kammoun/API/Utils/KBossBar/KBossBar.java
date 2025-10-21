package com.kammoun.API.Utils.KBossBar;

import com.kammoun.API.Utils.Chat.ChatFormater;
import com.kammoun.API.Utils.PlaceHolderHelper;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class KBossBar {

    private static final Map<UUID, BossBar> ACTIVE_BOSS_BARS = new ConcurrentHashMap<>();

    private final String text;
    private final BarColor color;
    private final BarStyle style;
    private final double progress;

    public KBossBar(@NotNull String text, BarColor color, BarStyle style, double progress) {
        this.text = text;
        this.color = color;
        this.style = style;
        this.progress = progress;
    }

    public void send(@NotNull Player player) {
        send(player,progress);
    }
    public void send(@NotNull Player player,double progress){
        stop(player);

        BossBar bossBar = Bukkit.createBossBar(ChatFormater.color(text), color, style);
        bossBar.setProgress(progress);
        bossBar.addPlayer(player);
        ACTIVE_BOSS_BARS.put(player.getUniqueId(), bossBar);
    }

    public KBossBar withPlaceholders(String... placeholders) {
        String newText = PlaceHolderHelper.parsePlaceholders(this.text, placeholders);
        return new KBossBar(newText, this.color, this.style, this.progress);
    }

    public static void stop(@NotNull Player player) {
        BossBar existingBar = ACTIVE_BOSS_BARS.remove(player.getUniqueId());
        if (existingBar != null) {
            existingBar.removeAll();
        }
    }

    public static KBossBar fromConfig(@NotNull ConfigurationSection config) {
        String text = config.getString("text", "");
        BarColor color;
        BarStyle style;

        try {
            color = BarColor.valueOf(config.getString("color", "WHITE").toUpperCase());
            style = BarStyle.valueOf(config.getString("style", "SOLID").toUpperCase());
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Invalid BossBar color or style in config. Defaulting to WHITE/SOLID.");
            color = BarColor.WHITE;
            style = BarStyle.SOLID;
        }

        double progress = config.getDouble("progress", 1.0);
        return new KBossBar(text, color, style, progress);
    }


}