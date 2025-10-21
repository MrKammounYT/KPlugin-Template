package com.kammoun.API.Utils.Title;


import com.kammoun.API.Utils.Chat.ChatFormater;
import com.kammoun.API.Utils.PlaceHolderHelper;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class KTitle {

    private final String title;
    private final String subtitle;
    private int fadeIn;
    private int stay;
    private int fadeOut;

    public KTitle(@Nullable String title, @Nullable String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = 10;
        this.stay = 70;
        this.fadeOut = 20;
    }

    public KTitle fadeIn(int fadeInTicks) {
        this.fadeIn = fadeInTicks;
        return this;
    }

    public KTitle stay(int stayTicks) {
        this.stay = stayTicks;
        return this;
    }

    public KTitle fadeOut(int fadeOutTicks) {
        this.fadeOut = fadeOutTicks;
        return this;
    }

    public void send(@NotNull Player player) {
        String coloredTitle = (title != null) ? ChatFormater.color(title) : "";
        String coloredSubtitle = (subtitle != null) ? ChatFormater.color(subtitle) : "";
        player.sendTitle(coloredTitle, coloredSubtitle, fadeIn, stay, fadeOut);
    }

    public KTitle withPlaceholders(String... placeholders) {
        String newTitle = (this.title != null) ? PlaceHolderHelper.parsePlaceholders(this.title, placeholders) : null;
        String newSubtitle = (this.subtitle != null) ? PlaceHolderHelper.parsePlaceholders(this.subtitle, placeholders) : null;

        KTitle newKTitle = new KTitle(newTitle, newSubtitle);
        newKTitle.fadeIn(this.fadeIn).stay(this.stay).fadeOut(this.fadeOut);
        return newKTitle;
    }

    public static KTitle fromConfig(@NotNull ConfigurationSection config) {
        String title = config.getString("header");
        String subtitle = config.getString("footer");

        KTitle kTitle = new KTitle(title, subtitle);
        kTitle.fadeIn(config.getInt("fade_in", kTitle.getFadeIn()));
        kTitle.stay(config.getInt("stay", kTitle.getStay()));
        kTitle.fadeOut(config.getInt("fade_out", kTitle.getFadeOut()));

        return kTitle;
    }
}